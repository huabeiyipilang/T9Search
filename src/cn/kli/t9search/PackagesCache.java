package cn.kli.t9search;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class PackagesCache extends Cache{
	
	private static PackagesCache sInstance;
	
	protected PackagesCache(Context context) {
		super(context);
	}

	public static PackagesCache getInstance(Context context){
		if(sInstance == null){
			sInstance = new PackagesCache(context);
		}
		return sInstance;
	}

	public static boolean hasBuilt(){
		return sInstance != null;
	}
	
	public class PackageItem extends Item{
		ResolveInfo info;
	}
	
	
	@Override
	void onCacheBuild() {
		PackageManager pm = mContext.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null); 
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);  
		PackageItem item;
		int total = apps.size();
		int pos = 0;
		for (ResolveInfo app : apps) {
			String name = app.loadLabel(pm).toString();
			item = new PackageItem();
			item.pName = name;
			item.key = translate.convert(name);
			item.info = app;
			mAllList.add(item);
			++pos;
			updateBuildProgress(pos + "/" + total);
		}
	}

}
