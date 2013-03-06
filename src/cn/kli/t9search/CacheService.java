package cn.kli.t9search;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CacheService extends Service {

	PackagesCache mAppCache;
	SettingsHelper mSettingsHelper;

	@Override
	public void onCreate() {
		super.onCreate();
		mAppCache = PackagesCache.getInstance(getApplicationContext());
		mAppCache.buildCache();
		mSettingsHelper = SettingsHelper.init(getApplicationContext());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
