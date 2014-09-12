package com.carl.t9search.module.settings.appselection;

import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.carl.t9search.R;
import com.carl.t9search.framework.app.AppInfo;
import com.carl.t9search.framework.app.AppManager;
import com.carl.t9search.framework.base.BaseFragment;
import com.carl.t9search.framework.base.ItemAdapter;

public class AppInfoSelectionFragment extends BaseFragment implements OnItemClickListener {

    private ListView mListView;
    private ItemAdapter mAdapter;
    private static OnAppInfoSelectedListener mListener;
    
    public interface OnAppInfoSelectedListener{
        void onAppInfoSelected(AppInfo info);
    }
    
    public static void setOnAppInfoSelectedListener(OnAppInfoSelectedListener listener){
        mListener = listener;
    }
    
    @Override
    public int getLayoutRes() {
        return R.layout.fragment_appinfo_selection;
    }

    @Override
    public void initViews(View root) {
        mListView = (ListView)root.findViewById(R.id.lv_app_list);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void initDatas() {
        List<AppInfo> infos = AppManager.getInstance().getAllApps();
        infos.add(0, null);
        mAdapter = new ItemAdapter(infos, AppInfoSelectionItemView.class);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        AppInfo info = (AppInfo) mAdapter.getItem(arg2);
        if(mListener != null){
            mListener.onAppInfoSelected(info);
            mListener = null;
        }
        getFragmentManager().popBackStack();
    }

}
