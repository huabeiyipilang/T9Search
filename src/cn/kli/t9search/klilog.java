package cn.kli.t9search;

import android.util.Log;

class klilog {
	private final static String TAG = "weather engine";
	
	public static void i(String msg){
		Log.i(TAG, msg);
	}
	
	public static void e(String msg){
		Log.e(TAG, msg);
	}
}
