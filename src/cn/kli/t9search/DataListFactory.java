package cn.kli.t9search;

import android.content.Context;
import cn.kli.t9search.SearchManager.SearchMode;

public class DataListFactory {
	public static IDataList create(Context context, SearchMode mode){
		IDataList list = null;
		switch(mode){
		case APP_MODE:
			list = new PackagesList(context);
			break;
		case CONTACTS_MODE:
			list = new ContactsList(context);
			break;
		}
		return list;
	}
}
