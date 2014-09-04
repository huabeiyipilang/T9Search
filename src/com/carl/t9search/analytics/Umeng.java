package com.carl.t9search.analytics;

import java.util.HashMap;

import android.content.Context;
import android.support.v4.app.Fragment;
import com.carl.t9search.framework.app.AppInfo;

import com.carl.t9search.App;
import com.umeng.analytics.MobclickAgent;

public class Umeng {
    
    public static Context sContext = App.getContext();
    
    static{
        MobclickAgent.setSessionContinueMillis(5);
    }
    
    public static void init(){
        MobclickAgent.updateOnlineConfig(sContext);
    }
    
    public static void onActivityResume(){
        MobclickAgent.onResume(sContext);
    }
    
    public static void onActivityPause(){
        MobclickAgent.onPause(sContext);
    }
    
    public static void onFragmentResume(Fragment fragment){
        MobclickAgent.onPageStart(fragment.getClass().getSimpleName());
    }
    
    public static void onFragmentPause(Fragment fragment){
        MobclickAgent.onPageEnd(fragment.getClass().getSimpleName());
    }
    
    public static void onEventOpenApp(boolean fromOpenButton, AppInfo info){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("open_from", fromOpenButton ? "click open" : "click app");
        map.put("title", info.title);
        map.put("package_name", info.packageName);
        MobclickAgent.onEvent(sContext, "open_app", map);  
    }
}
