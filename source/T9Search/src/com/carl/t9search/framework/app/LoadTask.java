package com.carl.t9search.framework.app;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.sprinkles.Transaction;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.carl.t9search.App;
import com.carl.t9search.framework.app.IAppLoadListener.Result;
import com.carl.t9search.framework.app.LoadTask.Progress;
import com.carl.t9search.utils.DbUtils;
import com.carl.t9search.utils.Logger;

public class LoadTask extends AsyncTask<Void, Progress, Result> {
    
    private Logger log = new Logger(LoadTask.class);
    
    private IAppLoadListener mListener;
    private PackageManager mPackageManager = App.getContext().getPackageManager();
    private List<AppInfo> mAppInDb;
    private Activity mActivity;
    private ProgressDialog mDialog;

    public LoadTask(Activity activity, boolean showDialog) {
        super();
        mActivity = activity;
        if(showDialog){
            mDialog = new ProgressDialog(mActivity);
        }
    }
    
    public void setListener(IAppLoadListener listener){
        mListener = listener;
    }
    
    
    
    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(mDialog != null){
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(mDialog != null){
            mDialog.setMessage("开始创建索引");
            mDialog.show();
        }
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

        mAppInDb = DbUtils.getAllData(AppInfo.class);
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
            //过滤本应用
            if(info.activityInfo.packageName.equals(App.getContext().getPackageName())){
                continue;
            }
            AppInfo item = AppInfo.newInstance(info);
            
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
        log.i("newApps size:"+newApps.size());

        //step3  64%
        progress.info = "保存数据";
        progress.progress = cost1 + cost2;
        publishProgress(progress);
        itemSpend = cost3 / mAppInDb.size();
        Transaction t = new Transaction();
        size = mAppInDb.size();
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

        if(mDialog != null){
            mDialog.dismiss();
        }
        
        AppManager.getInstance().notifyAppChanged();
    }
    
    

    @Override
    protected void onProgressUpdate(Progress... values) {
        super.onProgressUpdate(values);
        Progress progress = values[0];
        if(mListener != null){
            mListener.onProgressUpdate(progress.progress, progress.max, progress.info);
        }
        if(mDialog != null){
            mDialog.setMessage(progress.info + " " + (int)(progress.progress * 100 / progress.max)+"%");
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
}
