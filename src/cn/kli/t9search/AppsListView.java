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
import cn.kli.t9search.AppsManager.State;
import cn.kli.t9search.PackagesCache.PackageItem;

public class AppsListView extends LinearLayout implements OnItemClickListener, IDataList {
	
	private final static int MSG_UPDATE_ADAPTER = 1;
	private final static int MSG_UPDATE_BUILD_PROGRESS = 2;
	
	private Context mContext;
	private AbsListView mListView;
	private ViewGroup mContainer;
	private AppsManager mAppsManager;
	private PackagesAdapter mAdapter;
	private OnItemSelectListener mListener = new OnItemSelectListener(){

		@Override
		public void onItemSelect(Object o) {
			
		}
		
	};
	
	private AppsManager.StateChangeListener mDataChangeListener = new AppsManager.StateChangeListener(){

		@Override
		public void onStateChanged(State state) {
			klilog.i("State changed:"+state);
			switch(state){
			case IDLE:
				mContainer.removeAllViews();
				mContainer.addView(mListView);
				updateQueryString(null);
				break;
			case BUILDING:
			case QUERYING:
				break;
			}
		}

		@Override
		public void onProgressUpdate(int progress) {
			klilog.i("Progress changed:"+progress);
			Message msg = mHandler.obtainMessage(MSG_UPDATE_BUILD_PROGRESS);
			msg.obj = progress;
			msg.sendToTarget();
		}

		@Override
		public void onQueryCompleted(List<AppItem> list) {
			updateList(list);
		}
		
		
	};
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case MSG_UPDATE_ADAPTER:
				List<AppItem> items = (List<AppItem>)msg.obj;
				mAdapter = new PackagesAdapter(mContext, items);
				((GridView)mListView).setAdapter(mAdapter);
				break;
			case MSG_UPDATE_BUILD_PROGRESS:
			}
		}
		
	};
	
	public AppsListView(final Context context) {
		super(context);
		init(context);
	}
	
	public AppsListView(final Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context){
		mContext = context;
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		LayoutInflater inflater = LayoutInflater.from(context);
		mContainer = (ViewGroup)inflater.inflate(R.layout.package_list, this,true);


		mAppsManager = AppsManager.getInstance(mContext);
		mAppsManager.listen(mDataChangeListener);
		
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

		if(mAppsManager.hasBuilt()){
			mContainer.removeAllViews();
			mContainer.addView(mListView);
			updateQueryString("");
		}else{
			mAppsManager.build();
		}
	}
	
	private class PackagesAdapter extends ArrayAdapter<AppItem>{
		private LayoutInflater inflater;
		private List<AppItem> items;
		public PackagesAdapter(Context context, List<AppItem> items) {
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
			AppItem item = items.get(position);
			holder.p_icon.setImageBitmap(item.icon);
			holder.p_name.setText(item.name);
			holder.p_key.setText(item.key);
			return convertView;
		}
		
		private class ViewHolder{
			ImageView p_icon;
			TextView p_name;
			TextView p_key;
		}
		
	}
	
	private void updateList(List<AppItem> items){
		Message msg = new Message();
		msg.what = MSG_UPDATE_ADAPTER;
		msg.obj = items;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(mAdapter != null){
			AppItem item = mAdapter.items.get(position);
			mAppsManager.onAppOpen(item);
			launchApp(item.intent);
		}
		mListener.onItemSelect(null);
	}
	
	private void launchApp(Intent intent){
		if(intent == null){
			return;
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			mContext.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setOnItemSelectListener(OnItemSelectListener listener) {
		mListener = listener;
	}

	@Override
	public void updateQueryString(String query) {
		mAppsManager.query(query);
	}

	@Override
	public void selectTheFirst() {

		if(mAdapter != null){
			AppItem item = mAdapter.items.get(0);
			launchApp(item.intent);
		}
		
	}

}
