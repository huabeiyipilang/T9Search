package cn.kli.t9search;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Phone;


public class ContactsCache extends Cache {
	private static ContactsCache sInstance;

    protected static class PhoneQuery {
        private static final String[] PROJECTION_PRIMARY = new String[] {
            Phone._ID,                          // 0
            Phone.TYPE,                         // 1
            Phone.LABEL,                        // 2
            Phone.NUMBER,                       // 3
            Phone.CONTACT_ID,                   // 4
            Phone.LOOKUP_KEY,                   // 5
            Phone.PHOTO_ID,                     // 6
            Phone.DISPLAY_NAME_PRIMARY,         // 7
        };

        public static final int PHONE_ID           = 0;
        public static final int PHONE_TYPE         = 1;
        public static final int PHONE_LABEL        = 2;
        public static final int PHONE_NUMBER       = 3;
        public static final int PHONE_CONTACT_ID   = 4;
        public static final int PHONE_LOOKUP_KEY   = 5;
        public static final int PHONE_PHOTO_ID     = 6;
        public static final int PHONE_DISPLAY_NAME = 7;
    }


	protected ContactsCache(Context context) {
		super(context);
	}

	public static ContactsCache getInstance(Context context){
		if(sInstance == null){
			sInstance = new ContactsCache(context);
			sInstance.registerObserver(Phone.CONTENT_URI);
		}
		return sInstance;
	}
	
	public static boolean hasBuilt(){
		return sInstance != null;
	}
	
	public class ContactItem extends Item{
		String number;
		long contactId;
		String lookupKey;
		int photoId;
		String label;
		int type;
	}

	@Override
	void onCacheBuild() {
		Cursor cursor = mContext.getContentResolver().query(Phone.CONTENT_URI,
				PhoneQuery.PROJECTION_PRIMARY,
				null,
				null, 
				Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
		ContactItem item;
		int total = cursor.getCount();
		int pos = 0;
		while(cursor.moveToNext()){
			item = new ContactItem();
			item.pName = cursor.getString(PhoneQuery.PHONE_DISPLAY_NAME);
			item.number = cursor.getString(PhoneQuery.PHONE_NUMBER).trim();
			item.key = translate.convert(item.pName)+ " " + translate.convert(item.number);
			item.contactId = cursor.getLong(PhoneQuery.PHONE_CONTACT_ID);
			item.lookupKey = cursor.getString(PhoneQuery.PHONE_LOOKUP_KEY);
			item.photoId = cursor.getInt(PhoneQuery.PHONE_PHOTO_ID);
			item.label = cursor.getString(PhoneQuery.PHONE_TYPE);
			item.type = cursor.getInt(PhoneQuery.PHONE_TYPE);
			mAllList.add(item);
			++pos;
			updateBuildProgress(pos + "/" + total);
		}
	}

}
