package cn.kli.t9search;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

public class DbHelper extends SQLiteOpenHelper {
	private final static String DB_NAME = "database.db";
	private final static int DB_VERSION = 1;
	
	private final static String TABLE_APPS = "apps";
	private final static String APPS_NAME = "name";
	private final static String APPS_ICON = "icon";
	private final static String APPS_INTENT = "intent";
	private final static String APPS_PACKAGE = "pkg";
	private final static String APPS_COUNT = "count";
	private final static String APPS_KEY = "key";

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql_apps = "create table if not exists "+TABLE_APPS+" ("+
				APPS_NAME+" text,"+
				APPS_ICON+" blob,"+
				APPS_INTENT+" text,"+
				APPS_PACKAGE+" text,"+
				APPS_COUNT+" integer,"+
				APPS_KEY+" text);";
		db.execSQL(sql_apps);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

	public void addAppItems(List<AppItem> items){
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		for(AppItem item : items){
			ContentValues cv = new ContentValues();
			cv.put(APPS_NAME, item.name);
			if(item.icon != null){
				cv.put(APPS_ICON, flattenBitmap(item.icon));
			}
			cv.put(APPS_INTENT, item.intent.toUri(0));
			if(!TextUtils.isEmpty(item.pkg)){
				cv.put(APPS_PACKAGE, item.pkg);
			}
			cv.put(APPS_COUNT, 0);
			cv.put(APPS_KEY, item.key);
			db.insert(TABLE_APPS, null, cv);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
	public List<AppItem> query(String index){
		List<AppItem> appList = new ArrayList<AppItem>();
		Cursor c;
		if(TextUtils.isEmpty(index)){
			c = getReadableDatabase().query(TABLE_APPS, null, null, null, null, null, null);
		}else{
			c = getReadableDatabase().query(TABLE_APPS, null, APPS_KEY+" like '%"+index+"%'", null, null, null, null);
		}
		
		if(c.moveToFirst()){
			do{
				AppItem item = new AppItem();
				item.name = c.getString(c.getColumnIndex(APPS_NAME));
				item.icon = getIconFromCursor(c, c.getColumnIndex(APPS_ICON));
				try {
					item.intent = Intent.parseUri(c.getString(c.getColumnIndex(APPS_INTENT)), 0);
				} catch (URISyntaxException e) {
					e.printStackTrace();
					continue;
				}
				item.pkg = c.getString(c.getColumnIndex(APPS_PACKAGE));
				item.count = c.getInt(c.getColumnIndex(APPS_COUNT));
				item.key = c.getString(c.getColumnIndex(APPS_KEY));
				appList.add(item);
			}while(c.moveToNext());
		}
		c.close();
		return appList;
	}
	
	public void removeByPackage(String pkg){
		SQLiteDatabase db = getWritableDatabase();
		klilog.i("dbhelper remove package:"+pkg);
		db.delete(TABLE_APPS, APPS_PACKAGE+"='"+pkg+"'", null);
		db.close();
	}
	
	public void appOpen(AppItem item){
		SQLiteDatabase db = getWritableDatabase();
		klilog.i("item count +1, intent:"+item.intent.toUri(0));
		db.execSQL("update "+TABLE_APPS+" set "+APPS_COUNT+"="+APPS_COUNT+"+1 where "+APPS_INTENT+"="+item.intent.toUri(0));
		db.close();
	}
	
	private Bitmap getIconFromCursor(Cursor c, int iconIndex) {
        byte[] data = c.getBlob(iconIndex);
        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {
            return null;
        }
    }
	
	private static byte[] flattenBitmap(Bitmap bitmap){
		int size = bitmap.getWidth() * bitmap.getHeight() * 4;
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		try {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
