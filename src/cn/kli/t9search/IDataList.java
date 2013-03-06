package cn.kli.t9search;
import cn.kli.t9search.SearchManager.SearchMode;

public interface IDataList {
	
	interface OnItemSelectListener{
		void onItemSelect(Object o);
	}
	void setOnItemSelectListener(OnItemSelectListener listener);
	void updateQueryString(String query);
	void selectTheFirst();
}
