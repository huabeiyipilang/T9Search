package cn.kli.t9search.framework.app;

import java.util.ArrayList;
import java.util.List;

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
import cn.kli.t9search.utils.DbUtils;
import cn.kli.t9search.utils.PinYinUtils;

class LoadTask<Void, Progress, Result> extends AsyncTask<Void, Float, Result> {
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
        //step1  10%
        List<ResolveInfo> resolveInfos = findActivitiesByPackage(null);
        publishProgress(10f);
        
        //step2  80%
        int size = resolveInfos.size();
        float itemSpend = 80f / size;
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
            
            publishProgress(10f + (i + 1) * itemSpend);
        }
        
        //step3  10%
        mAppInDb.addAll(newApps);
        DbUtils.saveAll(mAppInDb);
        publishProgress(100f);
        
        return null;
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
    }

    @Override
    protected void onProgressUpdate(Float... values) {
        super.onProgressUpdate(values);
        if(mListener != null){
            mListener.onProgressUpdate(values[0]);
        }
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
