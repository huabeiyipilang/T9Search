package cn.kli.t9search.framework.app;

import java.util.List;

import cn.kli.t9search.utils.DbUtils;
import android.content.Context;

/**
 * 应用管理
 * @Package cn.kli.t9search.framework.app
 * @ClassName: AppManager
 * @author Carl Li
 * @mail huabeiyipilang@gmail.com
 * @date 2014-6-21 下午4:56:02
 */
public class AppManager {
    private static AppManager sInstance;
    
    private Context mContext;
    
    private AppManager(Context context){
        mContext = context;
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
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void startLoadTask(IAppLoadListener listener){
        LoadTask task = new LoadTask();
        task.setListener(listener);
        task.execute();
    }
    
    /**
     * 获取已安装的应用列表
     * @Title: getAllApps
     * @return
     * @return List<AppInfo>
     * @date 2014-6-22 下午2:36:15
     */
    public List<AppInfo> getAllApps(){
        String sql = "select * from app_info order by count desc";
        return DbUtils.getDataList(AppInfo.class, sql, "");
    }
}
