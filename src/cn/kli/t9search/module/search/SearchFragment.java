package cn.kli.t9search.module.search;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import cn.kli.t9search.R;
import cn.kli.t9search.framework.app.AppInfo;
import cn.kli.t9search.framework.app.AppManager;
import cn.kli.t9search.framework.base.BaseFragment;
import cn.kli.t9search.framework.base.ItemAdapter;
import cn.kli.t9search.module.search.KeyboardView.OnDigitsChangedListener;

public class SearchFragment extends BaseFragment implements OnDigitsChangedListener {
    private GridView mGridView;
    private KeyboardView mKeyboardView;
    
    private List<AppInfo> mAllAppList;
    private ItemAdapter mAdapter;
    
    @Override
    public int getLayoutRes() {
        return R.layout.fragment_search;
    }

    @Override
    public void initViews(View root) {
        mGridView = (GridView)root.findViewById(R.id.gv_list);
        mKeyboardView = (KeyboardView)root.findViewById(R.id.kbv_keyboard);
        mKeyboardView.setOnDigitsChangedListener(this);
    }

    @Override
    public void initDatas() {
        mAdapter = new ItemAdapter();
        mAdapter.setView(SearchItemView.class);
        mGridView.setAdapter(mAdapter);
        mAllAppList = AppManager.getInstance().getAllApps();
        updateList(mAllAppList);
    }

    @Override
    public void onDigitsChanged(String digits) {
        List<AppInfo> list = null;
        if(TextUtils.isEmpty(digits)){
            list = mAllAppList;
        }else{
            list = new ArrayList<AppInfo>();
            for(AppInfo info : mAllAppList){
                if(!TextUtils.isEmpty(info.keyword_quanpin_t9) && info.keyword_quanpin_t9.contains(digits)){
                    list.add(info);
                }
            }
        }
        updateList(list);
    }

    private void updateList(List<AppInfo> list){
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

}
