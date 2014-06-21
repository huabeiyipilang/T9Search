package cn.kli.t9search.framework.app;

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
    
    public AppManager init(Context context){
        if(sInstance == null){
            sInstance = new AppManager(context);
        }
        return sInstance;
    }
    
    public AppManager getInstance(){
        return sInstance;
    }
}
