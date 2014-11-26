package com.carl.t9search.framework.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AppsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.i("klilog", "CachePersistReceiver received action = "+action);
		if(Intent.ACTION_PACKAGE_ADDED.equals(action)){
			String packageName = intent.getDataString().split(":")[1]; 
			if(!context.getPackageName().equals(packageName)){
				AppManager.getInstance().onAppInstalled(packageName);
			}
		}else if(Intent.ACTION_PACKAGE_REMOVED.equals(action)){
			String packageName = intent.getDataString().split(":")[1]; 
			if(!context.getPackageName().equals(packageName)){
				AppManager.getInstance().onAppUninstalled(packageName);
			}
		}
	}

}
