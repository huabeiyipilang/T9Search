package cn.kli.t9search;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsHelper {
	private static SettingsHelper sInstance;
	private Context mContext;
	private SharedPreferences mPref;
	private SharedPreferences.Editor mPrefEditor;
	
	private final static String SETTINGS = "settings";
	public final static String KEY_HAS_KEY_TONE = "has_key_tone";
	
	private SettingsHelper(Context context){
		mContext = context;
		mPref = mContext.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		mPrefEditor = mPref.edit();
	}
	
	public static SettingsHelper getInstance(Context context){
		if(sInstance == null){
			sInstance = new SettingsHelper(context);
		}
		return sInstance;
	}
	
	public void put(String key, boolean value){
		mPref.edit().putBoolean(key, value).commit();
	}
	
	public boolean getBoolean(String key){
		return mPref.getBoolean(key, false);
	}

	public boolean getBoolean(String key, boolean def){
		return mPref.getBoolean(key, def);
	}
}
