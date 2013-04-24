package cn.kli.t9search;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

public class DbHelper extends SQLiteOpenHelper {
	private final static String DB_NAME = "database.db";
	private final static int DB_VERSION = 1;
	
	private final static String TABLE_APPS = "apps";
	private final static String APPS_NAME = "name";
	private final static String APPS_ICON = "icon";
	private final static String APPS_INTENT = "intent";
	private final static String APPS_PACKAGE = "package";
	private final static String APPS_COUNT = "count";

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql_apps = "create table if not exists "+TABLE_APPS+" ("+
				APPS_NAME+" text"+
				APPS_ICON+" blob"+
				APPS_INTENT+" text"+
				APPS_PACKAGE+" text"+
				APPS_COUNT+" integer);";
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
			cv.put(APPS_PACKAGE, item.pkg);
			cv.put(APPS_COUNT, 0);
			db.insert(TABLE_APPS, null, cv);
		}
		db.endTransaction();
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
