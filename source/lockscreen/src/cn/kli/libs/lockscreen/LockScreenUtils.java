package cn.kli.libs.lockscreen;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class LockScreenUtils {
    public static final int LENGTH_SHORT = 1;
    private static LockScreenUtils sInstance;

    DevicePolicyManager mDeviceManager;
    private Context mContext;
    
    private LockScreenUtils(Context context){
        mContext = context;
        mDeviceManager = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }
    
    public static LockScreenUtils getInstance(Context context){
        if(sInstance == null){
            sInstance = new LockScreenUtils(context);
        }
        return sInstance;
    }
    
    public boolean lockScreen() {
        boolean res = false;
        try {
            mDeviceManager.lockNow();
            res = true;
        } catch (Exception e) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            ComponentName componentName = new ComponentName(mContext, DeviceReceiver.class);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            mContext.startActivity(intent);
        }
        return res;
    }
    
}
