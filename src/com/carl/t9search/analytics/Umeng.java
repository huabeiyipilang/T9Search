package com.carl.t9search.analytics;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.carl.t9search.App;
import com.carl.t9search.framework.app.AppInfo;
import com.umeng.analytics.MobclickAgent;

public class Umeng {
    
    public static Context sContext = App.getContext();
    
    static{
        MobclickAgent.setSessionContinueMillis(5);
    }
    
    public static void init(){
        MobclickAgent.updateOnlineConfig(sContext);
    }
    
    public static void onActivityResume(Activity activity){
        MobclickAgent.onResume(activity);
    }
    
    public static void onActivityPause(Activity activity){
        MobclickAgent.onPause(activity);
    }
    
    public static void onFragmentResume(Fragment fragment){
        MobclickAgent.onPageStart(fragment.getClass().getSimpleName());
    }
    
    public static void onFragmentPause(Fragment fragment){
        MobclickAgent.onPageEnd(fragment.getClass().getSimpleName());
    }
    
    public static final int OPEN_APP_BY_OPEN_BUTTON = 1;
    public static final int OPEN_APP_BY_CLICK_ICON = 2;
    public static final int OPEN_APP_BY_QUICK_DIAL = 3;
    
    public static void onEventOpenApp(int openBy, AppInfo info){
        HashMap<String,String> map = new HashMap<String,String>();
        String open_from = "unknown";
        switch(openBy){
        case OPEN_APP_BY_OPEN_BUTTON:
            open_from = "click open";
            break;
        case OPEN_APP_BY_CLICK_ICON:
            open_from = "click app";
            break;
        case OPEN_APP_BY_QUICK_DIAL:
            open_from = "quick dial";
            break;
        }
        map.put("open_from", open_from);
        map.put("title", info.title);
        map.put("package_name", info.packageName);
        MobclickAgent.onEvent(sContext, "open_app", map);  
    }
}
