package cn.kli.t9search;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import cn.kli.t9search.translate.NameToNumber;
import cn.kli.t9search.translate.NameToNumberFactory;

public class AppsManager {
	enum State{
		IDLE, BUILDING, QUERYING; 
	}
	
	private static AppsManager sInstance;
	private Context mContext;
	
	private PackageManager mPm;
	private List<StateChangeListener> mListeners;
	private DbHelper mDbHelper;
	//state
	private State mState;
	
	private String mDelayedQuery;

	private AppsManager(Context context) {
		mContext = context;
		mPm = mContext.getPackageManager();
		mListeners = new ArrayList<StateChangeListener>();
		mDbHelper = new DbHelper(mContext);
	}

	public static AppsManager getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new AppsManager(context);
		}
		return sInstance;
	}

	public void listen(StateChangeListener listener){
		mListeners.add(listener);
	}
	
	public void unlisten(StateChangeListener listener){
		mListeners.remove(listener);
	}
	
	public void build(){
		new BuildDataTask().execute("");
	}
	
	public boolean hasBuilt(){
		SharedPreferences pref = mContext.getSharedPreferences("AppsManager", Context.MODE_PRIVATE);
		return pref.getBoolean("has_build", false);
	}
	
	public void query(String index){
		if(mState == State.QUERYING || mState == State.BUILDING){
			mDelayedQuery = index;
		}else{
			new QueryTask().execute(index);
		}
	}
	
	private void checkDelayedQuery(){
		if(!TextUtils.isEmpty(mDelayedQuery)){
			new QueryTask().execute(mDelayedQuery);
			mDelayedQuery = null;
		}
	}
	
	public void onAppInstalled(String pkg){
		new BuildDataTask().execute(pkg);
	}
	
	public void onAppUninstalled(String pkg){
		mDbHelper.removeByPackage(pkg);
		changeState(State.IDLE);
	}

	public void onAppOpen(AppItem item){
		mDbHelper.appOpen(item);
	}
	
	interface StateChangeListener{
		void onStateChanged(State state);
		void onProgressUpdate(int progress);
		void onQueryCompleted(List<AppItem> list);
	}
	
	private void changeState(State state) {
		if (mState == state) {
			return;
		}
		mState = state;
		for (StateChangeListener listener : mListeners) {
			listener.onStateChanged(state);
		}
	}
	
	private class QueryTask extends AsyncTask<String, Integer, Integer>{
		@Override
		protected Integer doInBackground(String... arg0) {
			List<AppItem> list = mDbHelper.query(arg0[0]);
			for(StateChangeListener listener : mListeners){
				listener.onQueryCompleted(list);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			checkDelayedQuery();
		}
		
	}
	
	private class BuildDataTask extends AsyncTask<String, Integer, Integer> {
		List<AppItem> itemList;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDbHelper = new DbHelper(mContext);
			itemList = new ArrayList<AppItem>();
		}
		
		@Override
		protected Integer doInBackground(String... arg0) {
			changeState(State.BUILDING);
			String pkg = arg0[0];
			List<ResolveInfo> apps = findActivitiesByPackage(pkg);
			NameToNumber translate = NameToNumberFactory.createChineseTranslate();
			//spend 80% progress
			int length = apps.size();
			int i = 1;
			for(ResolveInfo info : apps){
				AppItem item = new AppItem();
				item.name = info.loadLabel(mPm).toString();
				item.icon = drawableToBitmap(info.loadIcon(mPm));
				item.intent = getLaunchIntent(info);
				item.pkg = info.activityInfo.packageName;
				item.key = translate.convert(item.name);
				itemList.add(item);
				klilog.i(item.name+"has been added");
				publishProgress(i*80/length);
				i++;
			}
			publishProgress(80);
			
			//spend 20% progress
			mDbHelper.addAppItems(itemList);
			publishProgress(100);
			
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onCancelled(Integer result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			SharedPreferences pref = mContext.getSharedPreferences("AppsManager", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit();
			editor.putBoolean("has_build", true);
			editor.commit();
			checkDelayedQuery();
			changeState(State.IDLE);
		}


		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			for (StateChangeListener listener : mListeners) {
				listener.onProgressUpdate(values[0]);
			}
		}

		private Intent getLaunchIntent(ResolveInfo reInfo){
			if(reInfo == null){
				return null;
			}
			String packageName = reInfo.activityInfo.packageName;
			String name = reInfo.activityInfo.name;
			ComponentName cn = new ComponentName(packageName,name);
			Intent intent = new Intent();
			intent.setComponent(cn);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			return intent;
		}
	}
	
	public static Bitmap drawableToBitmap(Drawable drawable) {  
        // 取 drawable 的长宽  
        int w = drawable.getIntrinsicWidth();  
        int h = drawable.getIntrinsicHeight();  
  
        // 取 drawable 的颜色格式  
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                : Bitmap.Config.RGB_565;  
        // 建立对应 bitmap  
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);  
        // 建立对应 bitmap 的画布  
        Canvas canvas = new Canvas(bitmap);  
        drawable.setBounds(0, 0, w, h);  
        // 把 drawable 内容画到画布中  
        drawable.draw(canvas);  
        return bitmap;  
    } 
	
	/**
	 * Query MAIN/LAUNCHER activities by package name.
	 * @param pkgName  null means query all packages
	 * @return
	 */
	private List<ResolveInfo> findActivitiesByPackage(String pkgName){
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		if(!TextUtils.isEmpty(pkgName)){
			mainIntent.setPackage(pkgName);
		}
		
		final List<ResolveInfo> apps = mPm.queryIntentActivities(mainIntent, 0);
		return apps == null ? new ArrayList<ResolveInfo>() : apps;
	}
	
}
