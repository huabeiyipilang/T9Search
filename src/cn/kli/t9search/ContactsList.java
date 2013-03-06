/*
 * kli add for new dialpad, dfdun merge
 * */

package cn.kli.t9search;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;
import cn.kli.t9search.ContactsCache.ContactItem;


public class ContactsList extends LinearLayout implements IDataList, OnItemClickListener{
	private static final String TAG = "DialContactsList";
	
    private static final int RUN_QUERY_CONTACT_TASK = 1;
    
	private Context mContext;
	private ListView mListView;
	private TextView mListTitle;
	private TextView mNoDataTextView;
	private HashMap<Long, Integer> mContactHash;
	private ContactPhotoManager mContactPhotoManager;
	private ContactsListAdapter mAdapter;
	private boolean isContactTaskRunning;

	private Cache mCache;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case RUN_QUERY_CONTACT_TASK:
				if (!isContactTaskRunning) {
					new QueryContactTask().execute("");
					isContactTaskRunning = true;
				}
				break;
			}
		}
		
	};
	
	private Cache.CacheCallBack mCallBack = new Cache.CacheCallBack() {

		@Override
		public void buildFinished(List items) {
			
			updateList(items);
			Toast.makeText(mContext, "Load finish!", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void buildProgress(String progress) {
			
		}
		
		@Override
		public void searchCompleted(List items) {
			updateList(items);
		}

	};
	
	private IDataList.OnItemSelectListener mListener = new IDataList.OnItemSelectListener(){

		@Override
		public void onItemSelect(Object o) {
			makeCall(String.valueOf(o));
		}
		
	};
    
	private void makeCall(String number) {
        final Intent intent = new Intent("android.intent.action.CALL_PRIVILEGED",Uri.fromParts("tel", number, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}
	
	public ContactsList(Context context) {
		super(context);
		init(context);
	}

	public ContactsList(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context){
		mContext = context;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.contacts_query_list, this,true);
		mListView = (ListView)view.findViewById(R.id.contactsListview);
		mListTitle = (TextView)view.findViewById(R.id.listTitle);
		mNoDataTextView = (TextView)view.findViewById(R.id.noDataView);
		mContactPhotoManager = ContactPhotoManager.getInstance(mContext);
		mListView.setOnItemClickListener(this);
		startQueryContact();

		mCache = ContactsCache.getInstance(context);
		mCache.addCallBack(mCallBack);

		if(mCache.isBuilt()){
			updateQueryString("");
		}else{
			mCache.buildCache();
		}
	}
	
	
	//fresh mContactHash
	private void startQueryContact(){
		updateData(null);
	}

    public void setOnItemSelectListener(IDataList.OnItemSelectListener listener){
        mListener = listener;
    }

	@Override
	public void updateQueryString(String query){
//		lastQueryString = removeNotNumber(query);
//		updateData(null);
		mCache.Search(query);
	} 
	
	private void contactTaskCompleted(HashMap<Long, Integer> hm){
		mContactHash = hm;
		isContactTaskRunning = false;
		updateData(null);
	}
	
	private void updateData(String selection){
//		new QueryPhoneTask().execute(lastQueryString);
	}
	
	private void updateList(List<ContactItem> items){
        /*
		if (c.getCount() == 0) {
			if(TextUtils.isEmpty(lastQueryString)){
				mNoDataTextView.setText(R.string.no_star);
			}else{
				mNoDataTextView.setText(R.string.no_result);
			}
			mNoDataTextView.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}else{
			mNoDataTextView.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		}
		*/
		
		mAdapter = new ContactsListAdapter(mContext, items);
		
//		if(TextUtils.isEmpty(lastQueryString)){
//			mListTitle.setText(mContext.getString(R.string.list_title_star, mAdapter.getCount()));
//		}else{
//			mListTitle.setText(mContext.getString(R.string.list_title_result, mAdapter.getCount()));
//		}
        mListView.setAdapter(mAdapter);
        mListTitle.setText("items.size() = "+items.size());
	}
	
	//Contact list adapter
	private class ContactsListAdapter extends ArrayAdapter<ContactsCache.ContactItem>{
		private LayoutInflater inflater;
		private List<ContactItem> items;
		public ContactsListAdapter(Context context, List<ContactItem> items) {
			super(context, 0, items);
			inflater = LayoutInflater.from(context);
			this.items = items;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null || convertView.getTag() == null){
				convertView = inflater.inflate(R.layout.contacts_list_item, null);
				holder = new ViewHolder();
				holder.photoView = (QuickContactBadge)(convertView.findViewById(R.id.contact_photo));
				holder.nameView = (TextView)(convertView.findViewById(R.id.contact_name));
				holder.numberView = (TextView)(convertView.findViewById(R.id.contact_number));
				holder.labelView = (TextView)(convertView.findViewById(R.id.contact_type));
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			bindView(holder, position);
			return convertView;
		}
		
		private void bindView(ViewHolder holder, int position){
			ContactItem item = (ContactItem)items.get(position);
			holder.nameView.setText(item.pName);
			holder.numberView.setText(item.number);

            //set photo
			Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI, item.contactId + "");
			holder.photoView.assignContactUri(contactUri);
			mContactPhotoManager.loadPhoto(holder.photoView, item.photoId, false,true);  
			//set phone tab
            CharSequence labelDisplay = Phone.getTypeLabel(mContext.getResources(),item.type, item.label);
            holder.labelView.setText(labelDisplay);
		}
		
		final class ViewHolder {
	        public TextView nameView, numberView, labelView;
	        public QuickContactBadge photoView;
	        public CharArrayBuffer nameBuffer = new CharArrayBuffer(128);
	    }
		
	}


	class QueryContactTask extends AsyncTask<Object, Object, Object>{
		
		@Override
		protected void onPostExecute(Object result) {
			contactTaskCompleted((HashMap)result);
			super.onPostExecute(result);
		}

		@Override
		protected Object doInBackground(Object... params) {
			
			//query contact type
			HashMap<Long, Integer> hm = new HashMap<Long, Integer>();
            String[] projections = {Contacts._ID, "contact_type"};
            Cursor contactTypeCursor = null;
			try {
				contactTypeCursor = mContext.getContentResolver().query(Contacts.CONTENT_URI,
						projections, null,null,null);
			} catch (Exception e) {
				Log.e(TAG,"Not support contact type");
				return null;
			}
            if(contactTypeCursor.moveToFirst()){
            	long key = contactTypeCursor.getLong(contactTypeCursor.getColumnIndex(Contacts._ID));
            	int value = contactTypeCursor.getInt(contactTypeCursor.getColumnIndex("contact_type"));
            	hm.put(key, value);
            	while(contactTypeCursor.moveToNext()){
                	key = contactTypeCursor.getLong(contactTypeCursor.getColumnIndex(Contacts._ID));
                	value = contactTypeCursor.getInt(contactTypeCursor.getColumnIndex("contact_type"));
                	hm.put(key, value);
            	}
            }
            contactTypeCursor.close();
			return hm;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
	}

	@Override
	public void selectTheFirst() {
		// TODO Auto-generated method stub
		
	}
}
