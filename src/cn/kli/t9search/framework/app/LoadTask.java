package cn.kli.t9search.framework.app;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.sprinkles.SqlStatement;
import se.emilsjolander.sprinkles.Transaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import cn.kli.t9search.App;
import cn.kli.t9search.R;
import cn.kli.t9search.framework.app.IAppLoadListener.Result;
import cn.kli.t9search.framework.app.LoadTask.Progress;
import cn.kli.t9search.utils.DbUtils;
import cn.kli.t9search.utils.Logger;
import cn.kli.t9search.utils.PinYinUtils;

class LoadTask extends AsyncTask<Void, Progress, Result> {
    
    private Logger log = new Logger(LoadTask.class);
    
    private IAppLoadListener mListener;
    private PackageManager mPackageManager = App.getContext().getPackageManager();
    private List<AppInfo> mAppInDb;
    
    public void setListener(IAppLoadListener listener){
        mListener = listener;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mAppInDb = DbUtils.getAllData(AppInfo.class);
    }

    @Override
    protected Result doInBackground(Void... arg0) {
        long start = System.currentTimeMillis();
        //step1  
        float cost1 = 1;
        float cost2 = 4;
        float cost3 = 16;
        float total = cost1 + cost2 + cost3;
        Progress progress = new Progress();
        progress.max = total;
        progress.progress = 0;
        progress.info = "加载应用";
        publishProgress(progress);

        List<ResolveInfo> resolveInfos = findActivitiesByPackage(null);
        for(AppInfo info : mAppInDb){
            info.installed = false;
        }
        progress.progress = cost1;
        progress.info = "生成索引";
        publishProgress(progress);
        
        //step2  16%
        int size = resolveInfos.size();
        float itemSpend = cost2 / size;
        List<AppInfo> newApps = new ArrayList<AppInfo>();
        
        for(int i = 0; i < size; i++){
            ResolveInfo info = resolveInfos.get(i);
            AppInfo item = new AppInfo();
            item.title = info.loadLabel(mPackageManager).toString();
            item.icon = drawableToBitmap(info.loadIcon(mPackageManager));
            item.intent = getLaunchIntent(info);
            item.packageName = info.activityInfo.packageName;
            item.setQuanpin(PinYinUtils.string2PinYin(item.title));
            
            AppInfo app = findAppInfoFromDb(item);
            if(app != null){
                app.merge(item);
            }else{
                newApps.add(item);
            }

            progress.progress = cost1 + (i + 1) * itemSpend;
            publishProgress(progress);
        }
        mAppInDb.addAll(newApps);

        //step3  64%
        progress.info = "保存数据";
        progress.progress = cost1 + cost2;
        publishProgress(progress);
        itemSpend = cost3 / mAppInDb.size();
        Transaction t = new Transaction();
        for(int i = 0; i < size; i++){
            mAppInDb.get(i).save(t);
            progress.progress = cost2 + (i + 1) * itemSpend;
            publishProgress(progress);
        }
        t.setSuccessful(true);
        t.finish();

        progress.progress = progress.max;
        progress.info = "完成";
        publishProgress(progress);
        log.i("cost:"+(System.currentTimeMillis() - start));
        return null;
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if(mListener != null){
            mListener.onFinished();
        }
    }
    
    

    @Override
    protected void onProgressUpdate(Progress... values) {
        super.onProgressUpdate(values);
        if(mListener != null){
            Progress progress = values[0];
            mListener.onProgressUpdate(progress.progress, progress.max, progress.info);
        }
    }


    public static class Progress{
        float max;
        float progress;
        String info;
    }
    
    private AppInfo findAppInfoFromDb(AppInfo info){
        for(AppInfo app : mAppInDb){
            if(app.equals(info)){
                return app;
            }
        }
        return null;
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        int w = App.getContext().getResources().getDimensionPixelSize(R.dimen.search_item_icon_size);
        int h = w;  
  
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                : Bitmap.Config.RGB_565;  
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);  
        Canvas canvas = new Canvas(bitmap);  
        drawable.setBounds(0, 0, w, h);  
        drawable.draw(canvas);  
        return bitmap;  
    } 
    
    /**
     * Query MAIN/LAUNCHER activities by package name.
     * @param pkgName  null means query all packages
     * @return
     */
    private List<ResolveInfo> findActivitiesByPackage(String pkgName){
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        if(!TextUtils.isEmpty(pkgName)){
            mainIntent.setPackage(pkgName);
        }
        
        final List<ResolveInfo> apps = mPackageManager.queryIntentActivities(mainIntent, 0);
        return apps == null ? new ArrayList<ResolveInfo>() : apps;
    }
    
    private Intent getLaunchIntent(ResolveInfo reInfo){
        if(reInfo == null){
            return null;
        }
        String packageName = reInfo.activityInfo.packageName;
        String name = reInfo.activityInfo.name;
        ComponentName cn = new ComponentName(packageName,name);
        Intent intent = new Intent();
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
