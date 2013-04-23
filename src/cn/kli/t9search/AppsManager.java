package cn.kli.t9search;

import android.content.Context;
import android.content.pm.PackageManager;

public class AppsManager {
	private static AppsManager sInstance;
	private Context mContext;
	
	private AppsManager(Context context){
		mContext = context;
	}
	
	public static AppsManager getInstance(Context context){
		if(sInstance == null){
			sInstance = new AppsManager(context);
		}
		return sInstance;
	}
	
	public void checkForUpdate(){
		PackageManager pm = mContext.getPackageManager();
		//get packages from pm
		//get packages from db
		//contrast pkgs
		//add pkg
		//remove pkg
	}
	
	private void addPackage(String pkg){
		
	}
	
	private void removePackage(String pkg){
		
	}
}
