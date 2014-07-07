package cn.kli.t9search.module.settings;

import cn.kli.t9search.utils.PrefUtils;

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
}
