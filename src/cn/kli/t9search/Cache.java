package cn.kli.t9search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import cn.kli.t9search.translate.NameToNumber;
import cn.kli.t9search.translate.NameToNumberFactory;

public abstract class Cache {
	protected Context mContext;
	protected final static int MSG_SEARCH = 1;
	protected final static int MSG_BUILD_CACHE = 2;
	protected final static int MSG_DATA_CHANGED = 3;
	protected List<CacheCallBack> mCallBacks;
	protected List<Item> mAllList;
	protected List<Item> mCurrentList = new ArrayList<Item>();
	protected Handler mHandler = new CacheHandler();
	protected DataLoader mDataLoader = new DataLoader();
	protected SearchTask mSearchTask = new SearchTask();
	protected NameToNumber translate;
	protected String mLastInput = "";
	private boolean isBuilding = false;
	private long startSearchTime;
	
	protected Cache(Context context) {
		mContext = context;
		translate = NameToNumberFactory.createChineseTranslate();
		mCallBacks = new ArrayList<CacheCallBack>();
	}
	
	protected void addCallBack(CacheCallBack callback){
		mCallBacks.add(callback);
	}

	interface CacheCallBack{
		void buildProgress(String progress);
		void buildFinished(List<Item> items);
		void searchCompleted(List<Item> items);
	}

	public class Item{
		String pName;
		String key;
	}
	
	public Item getCurrentSelectedItem(int pos){
		try {
			return mCurrentList.get(pos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void Search(String input){
		if(mAllList == null || isBuilding){
			return;
		}
		String currentInput = removeNonDigits(input);
		mHandler.removeMessages(MSG_SEARCH);
		Message msg = new Message();
		msg.what = MSG_SEARCH;
		msg.obj = currentInput;
		mHandler.sendMessage(msg);
		startSearchTime = System.currentTimeMillis(); 
	}
	
	
	protected void registerObserver(Uri uri){
		mContext.getContentResolver().registerContentObserver(uri, true, new CacheObserver(mHandler));
	}
	
	private String removeNonDigits(String number) {
        int len = number.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char ch = number.charAt(i);
            if ((ch >= '0' && ch <= '9') || ch == '*' || ch == '#' || ch == '+') {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

	public void buildCache(){
		if(!isBuilding){
			isBuilding = true;
			mHandler.sendEmptyMessage(MSG_BUILD_CACHE);
		}
	}

	public boolean isBuilt(){
		return mAllList != null;
	}
	
	protected class CacheHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case MSG_SEARCH:
				new SearchTask().execute(msg.obj);
				break;
			case MSG_BUILD_CACHE:
				mDataLoader = new DataLoader();
				mDataLoader.execute("");
				break;
			case MSG_DATA_CHANGED:
				this.sendEmptyMessage(MSG_BUILD_CACHE);
			}
		}
	}
	
	private class DataLoader extends AsyncTask{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mAllList = new ArrayList<Item>();
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			onCacheBuild();
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Object... values) {
			super.onProgressUpdate(values);
			onBuildProgressUpdate(String.valueOf(values[0]));
		}



		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			onBuildCompleted();
		}
	}
	
	abstract void onCacheBuild();
	
	protected void onBuildProgressUpdate(String progress){
		for(CacheCallBack callback: mCallBacks){
			callback.buildProgress(progress);
		}
	}

	protected void onBuildCompleted(){
		Log.i("klilog", "cache build complelted, notify call backs, mCallBacks size = "+mCallBacks.size());
		mCurrentList = mAllList;
		isBuilding = false;
		for(CacheCallBack callback: mCallBacks){
			callback.buildFinished(mAllList);
		}
	}
	
	protected void updateBuildProgress(String progress){
		mDataLoader.onProgressUpdate(progress);
	}

	protected void onSearching(String input) {
		List<Item> items = input.contains(mLastInput) ? 
				mCurrentList : mAllList;
		List<Item> results = new ArrayList<Item>();
		for(Item item : items){
			if(item.key.contains(input)){
				results.add(item);
			}
		}
		mCurrentList = results;
		
	}

	void onSearchCompleted() {
		orderList(mLastInput, mCurrentList);
		Log.i("klilog", "order completed time = "+(System.currentTimeMillis() - startSearchTime));
		for(CacheCallBack callback: mCallBacks){
			callback.searchCompleted(mCurrentList);
		}
	}
	
	
	protected void orderList(final String search, List<Item> list){
		if(TextUtils.isEmpty(search)){
			return;
		}
		Collections.sort(list, new Comparator<Item>(){

			@Override
			public int compare(Item lhs, Item rhs) {
				return getIndex(lhs) - getIndex(rhs);
			}
			
			private int getIndex(Item item){
				int index = 1000;
				String[] keys = item.key.split(" ");
				for(String key : keys){
					int key_index = key.indexOf(search);
					if(key_index >= 0 && key_index < index){
						index = key_index;
					}
				}
				return index;
			}
			
		});
	}
	
	
	private class SearchTask extends AsyncTask{
		String search = "";
		@Override
		protected Object doInBackground(Object... arg0) {
			search = String.valueOf(arg0[0]);
			onSearching(search);
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			Log.i("klilog", "search completed  time = "+(System.currentTimeMillis() - startSearchTime));
			startSearchTime = System.currentTimeMillis();
			onSearchCompleted();
			mLastInput = search;
		}
		
	}
	
	private class CacheObserver extends ContentObserver{

		public CacheObserver(Handler handler) {
			super(handler);
			
		}

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			mHandler.sendEmptyMessage(MSG_DATA_CHANGED);
		}
		
	}
}
