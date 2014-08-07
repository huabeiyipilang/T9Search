package cn.kli.t9search.framework.app;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.sprinkles.Query;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import cn.kli.t9search.utils.DbUtils;
import cn.kli.t9search.utils.Logger;
import cn.kli.t9search.utils.PrefUtils;

/**
 * 应用管理
 * @Package cn.kli.t9search.framework.app
 * @ClassName: AppManager
 * @author Carl Li
 * @mail huabeiyipilang@gmail.com
 * @date 2014-6-21 下午4:56:02
 */
public class AppManager{
    private static AppManager sInstance;
    
    private static final String PREF_INITED = "app_manager_inited";
    
    private Logger log = new Logger(AppManager.class);
    
    private Context mContext;
    private PackageManager mPackageManager;
    
    private List<OnAppChangedListener> mListener = new ArrayList<OnAppChangedListener>();
    
    public interface OnAppChangedListener{
        void onAppChanged();
    }
    
    private AppManager(Context context){
        mContext = context;
        mPackageManager = mContext.getPackageManager();
    }
    
    public static AppManager init(Context context){
        if(sInstance == null){
            sInstance = new AppManager(context);
        }
        return sInstance;
    }
    
    public static AppManager getInstance(){
        return sInstance;
    }
    
    public LoadTask startLoadTask(Activity activity, IAppLoadListener listener, boolean showDialog){
        LoadTask task = new LoadTask(activity, showDialog);
        task.setListener(listener);
        task.execute();
        return task;
    }
    
    /**
     * 获取已安装的应用列表
     * @Title: getAllApps
     * @return
     * @return List<AppInfo>
     * @date 2014-6-22 下午2:36:15
     */
    public List<AppInfo> getAllApps(){
      String sql = "select * from app_info where installed = 1 order by count desc";
//        String sql = "select * from app_info order by count desc";
        return DbUtils.getDataList(AppInfo.class, sql, "");
    }
    
    public boolean isInited(){
        return PrefUtils.getBoolean(PREF_INITED, false);
    }
    
    public void setInited(boolean inited){
        PrefUtils.setBoolean(PREF_INITED, inited);
    }
    
    public void listenAppListChanged(OnAppChangedListener observer){
        mListener.add(observer);
    }
    
    public void unlistenAppListChanged(OnAppChangedListener observer){
        mListener.remove(observer);
    }

    public void onAppInstalled(final String packageName) {
        new Thread(){

            @Override
            public void run() {
                super.run();
                List<AppInfo> apps = findAppInDbByPkg(packageName);
                if(apps == null || apps.size() == 0){
                    apps = new ArrayList<AppInfo>();
                    List<ResolveInfo> infos = findActivitiesByPackage(packageName);
                    if(infos == null || infos.size() == 0){
                        return;
                    }else{
                        for(ResolveInfo info : infos){
                            apps.add(AppInfo.newInstance(info));
                        }
                    }
                }else{
                    for(AppInfo info : apps){
                        info.installed = true;
                    }
                }
                DbUtils.saveAll(apps);
                
                notifyAppChanged();
            }
            
        }.start();
    }

    public void onAppUninstalled(final String packageName) {
        new Thread(){

            @Override
            public void run() {
                super.run();
                List<AppInfo> infos = findAppInDbByPkg(packageName);
                log.i("app uninstalled package name:"+packageName+", appinfo:"+ ((infos == null) ? "null" : infos.size()+""));
                for(AppInfo info : infos){
                    info.installed = false;
                }
                DbUtils.saveAll(infos);
                notifyAppChanged();
            }
            
        }.start();
    }
    
    void notifyAppChanged(){
        for(OnAppChangedListener listener : mListener){
            try {
                listener.onAppChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private List<ResolveInfo> findActivitiesByPackage(String pkgName){
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        if(!TextUtils.isEmpty(pkgName)){
            mainIntent.setPackage(pkgName);
        }
        
        final List<ResolveInfo> apps = mPackageManager.queryIntentActivities(mainIntent, 0);
        return apps == null ? new ArrayList<ResolveInfo>() : apps;
    }
    
    private List<AppInfo> findAppInDbByPkg(String packageName){
        String sql = "select * from app_info where package_name = \""+packageName+"\"";
        return DbUtils.getDataList(AppInfo.class, sql, "");
    }
    
}
