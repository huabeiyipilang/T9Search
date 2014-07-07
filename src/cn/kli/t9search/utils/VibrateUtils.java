package cn.kli.t9search.utils;

import cn.kli.t9search.App;
import android.content.Context;
import android.os.Vibrator;

public class VibrateUtils {
    public static final int LENGTH_SHORT = 1;
    private static VibrateUtils sInstance;
    
    private Vibrator mVibrator;
    private Context mContext;
    
    private VibrateUtils(Context context){
        mContext = context;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }
    
    public static VibrateUtils getInstance(){
        if(sInstance == null){
            sInstance = new VibrateUtils(App.getContext());
        }
        return sInstance;
    }
    
    public void vibrateShortly(){
        mVibrator.vibrate(LENGTH_SHORT);
    }
    
}
