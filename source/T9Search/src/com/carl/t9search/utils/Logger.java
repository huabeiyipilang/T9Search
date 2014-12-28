package com.carl.t9search.utils;

import com.carl.t9search.BuildConfig;

import android.text.TextUtils;
import android.util.Log;

public class Logger {
	private static String TAG = "klilog";
	
	private String tag;
	
	public Logger(){
		this(null);
	}
	
	public Logger(Class<?> cls){
		this.tag = cls.getSimpleName();
	}
	
	static void setTAG(String tag){
		TAG = tag;
	}
	
	public static void info(String msg){
		if(BuildConfig.DEBUG)
		Log.i(TAG, msg);
	}
	
	public static void error(String msg){
		if(BuildConfig.DEBUG)
		Log.e(TAG, msg);
	}
	
	public void i(String msg){
		if(BuildConfig.DEBUG)
		Log.i(TAG, buildMessage(msg));
	}
	
	public void e(String msg){
		if(BuildConfig.DEBUG)
		Log.e(TAG, buildMessage(msg));
	}
	
	private String buildMessage(String msg){
		if(!TextUtils.isEmpty(tag)){
			msg = tag+":"+msg;
		}
		return msg;
	}
	
}
