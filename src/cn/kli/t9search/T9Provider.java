package cn.kli.t9search;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class T9Provider extends ContentProvider {

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

	private class DbHelper extends SQLiteOpenHelper {
		private final static String DB_NAME = "database.db";
		private final static int DB_VERSION = 1;
		
		private final static String TABLE_APPS = "apps";
		private final static String APPS_NAME = "name";
		private final static String APPS_ICON = "icon";
		private final static String APPS_INTENT = "intent";
		private final static String APPS_PACKAGE = "package";
		private final static String APPS_COUNT = "count";

		private DbHelper(Context context) {
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

	}
}
