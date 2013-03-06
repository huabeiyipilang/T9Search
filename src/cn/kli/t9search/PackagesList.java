package cn.kli.t9search;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kli.t9search.PackagesCache.PackageItem;

public class PackagesList extends LinearLayout implements OnItemClickListener, IDataList {
	
	private final static int MSG_UPDATE_ADAPTER = 1;
	private final static int MSG_UPDATE_BUILD_PROGRESS = 2;
	
	private Context mContext;
	private AbsListView mListView;
	private ViewGroup mContainer;
	private Cache mCache;
	private OnItemSelectListener mListener = new OnItemSelectListener(){

		@Override
		public void onItemSelect(Object o) {
			
		}
		
	};
	
	private Cache.CacheCallBack mCallBack = new Cache.CacheCallBack() {

		@Override
		public void buildFinished(List items) {
			Log.i("klilog", "cache build finished. items counts = "+items.size());
			mContainer.removeAllViews();
			mContainer.addView(mListView);
			updateList(items);
		}

		@Override
		public void buildProgress(String progress) {
			Message msg = mHandler.obtainMessage(MSG_UPDATE_BUILD_PROGRESS);
			msg.obj = progress;
			msg.sendToTarget();
		}

		@Override
		public void searchCompleted(List items) {
			updateList(items);
		}
	};
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case MSG_UPDATE_ADAPTER:
				List<PackageItem> items = (List<PackageItem>)msg.obj;
				((GridView)mListView).setAdapter(new PackagesAdapter(mContext, items));
				break;
			case MSG_UPDATE_BUILD_PROGRESS:
			}
		}
		
	};
	
	public PackagesList(final Context context) {
		super(context);
		init(context);
	}
	
	public PackagesList(final Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context){
		mContext = context;
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		LayoutInflater inflater = LayoutInflater.from(context);
		mContainer = (ViewGroup)inflater.inflate(R.layout.package_list, this,true);

		mCache = PackagesCache.getInstance(context);
		mCache.addCallBack(mCallBack);
		
		mListView = new GridView(mContext);
		mListView.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				
			}

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				if(arg1 == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
					Mediator.getInstance().hideKeyboard();
				}
			}
			
		});
		((GridView)mListView).setNumColumns(4);
		mListView.setOnItemClickListener(this);
		mListView.setSelector(R.drawable.app_icon_bg);

		if(mCache.isBuilt()){
			mContainer.removeAllViews();
			mContainer.addView(mListView);
			updateQueryString("");
		}else{
			mCache.buildCache();
		}
	}
	
	private class PackagesAdapter extends ArrayAdapter<PackagesCache.PackageItem>{
		private LayoutInflater inflater;
		private List<PackageItem> items;
		public PackagesAdapter(Context context, List<PackageItem> items) {
			super(context, 0, items);
			inflater = LayoutInflater.from(context);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null || convertView.getTag() == null){
				convertView = inflater.inflate(R.layout.package_item, null);
				holder = new ViewHolder();
				holder.p_name = (TextView)convertView.findViewById(R.id.p_name);
				holder.p_key = (TextView)convertView.findViewById(R.id.p_key);
				holder.p_icon = (ImageView)convertView.findViewById(R.id.p_icon);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			PackageItem item = items.get(position);
			holder.p_icon.setImageDrawable(item.info.loadIcon(mContext.getPackageManager()));
			holder.p_name.setText(item.pName);
			holder.p_key.setText(item.key);
			return convertView;
		}
		
		private class ViewHolder{
			ImageView p_icon;
			TextView p_name;
			TextView p_key;
		}
		
	}
	
	private void updateList(List<PackageItem> items){
		Message msg = new Message();
		msg.what = MSG_UPDATE_ADAPTER;
		msg.obj = items;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		PackageItem currentItem = (PackageItem)mCache.getCurrentSelectedItem(position);
		launchApp(currentItem.info);
		mListener.onItemSelect(null);
	}
	
	private void launchApp(ResolveInfo reInfo){
		if(reInfo == null){
			return;
		}
		Mediator.getInstance().keyboardClear();
		String packageName = reInfo.activityInfo.packageName;
		String name = reInfo.activityInfo.name;
		ComponentName cn = new ComponentName(packageName,name);
		Intent intent = new Intent();
		intent.setComponent(cn);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}

	@Override
	public void setOnItemSelectListener(OnItemSelectListener listener) {
		mListener = listener;
	}

	@Override
	public void updateQueryString(String query) {
		mCache.Search(query);
	}

	@Override
	public void selectTheFirst() {
		PackageItem currentItem = (PackageItem)mCache.getCurrentSelectedItem(0);
		if(currentItem == null){
			return;
		}
		launchApp(currentItem.info);
	}

}
