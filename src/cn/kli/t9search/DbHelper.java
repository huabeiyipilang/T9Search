package cn.kli.t9search;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
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
