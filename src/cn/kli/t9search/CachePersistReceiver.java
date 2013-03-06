package cn.kli.t9search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CachePersistReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(Intent.ACTION_BATTERY_CHANGED.equals(action)){
			PackagesCache.getInstance(context);
			ContactsCache.getInstance(context);
		}
	}

}
