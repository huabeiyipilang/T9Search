package cn.kli.t9search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CachePersistReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.i("klilog", "CachePersistReceiver received action = "+action);
		if(Intent.ACTION_BATTERY_CHANGED.equals(action)){
			context.startService(new Intent(context, CacheService.class));
		}else if(Intent.ACTION_PACKAGE_ADDED.equals(action) || Intent.ACTION_PACKAGE_REMOVED.equals(action)){
			PackagesCache.getInstance(context).buildCache();
		}
	}

}
