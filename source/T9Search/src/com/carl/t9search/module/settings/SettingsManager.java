package com.carl.t9search.module.settings;

import se.emilsjolander.sprinkles.Query;
import android.content.Intent;

import com.carl.t9search.framework.app.AppInfo;
import com.carl.t9search.utils.PrefUtils;

public class SettingsManager {
    
    public static void setVibrateFeedback(boolean enable){
        PrefUtils.setBoolean("feedback_vibrate", enable);
    }
    
    public static boolean getVibrateFeedback(){
        return PrefUtils.getBoolean("feedback_vibrate", true);
    }
    

    public static void setSoundFeedback(boolean enable){
        PrefUtils.setBoolean("feedback_sound", enable);
    }
    
    public static boolean getSoundFeedback(){
        return PrefUtils.getBoolean("feedback_sound", true);
    }
    
    
    public static void setHideAfterOpenApp(boolean enable){
        PrefUtils.setBoolean("hide_after_open_app", enable);
    }
    
    public static boolean getHideAfterOpenApp(){
        return PrefUtils.getBoolean("hide_after_open_app", false);
    }
    
    //QuickDial
    
    private static QuickDialListener mQuickDialListener;
    
    public static void registerQuickDialListener(QuickDialListener listener){
        mQuickDialListener = listener;
    }
    
    public static interface QuickDialListener{
        void onQuickDialChanged(int index);
    }
    
    public static void setQuickDial(int index, Intent intent){
        PrefUtils.setString("quick_dial_"+index, intent == null ? null : intent.toUri(0));
        if(mQuickDialListener != null){
            mQuickDialListener.onQuickDialChanged(index);
        }
    }
    
    public static AppInfo getQuickDial(int index){
        String intent = PrefUtils.getString("quick_dial_"+index, null);
        AppInfo info = Query.one(AppInfo.class, String.format("select * from app_info where intent=\"%s\"", intent)).get();
        return info;
    }
}
