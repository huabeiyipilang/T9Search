package cn.kli.t9search.module.settings;

import android.view.View;
import cn.kli.t9search.R;
import cn.kli.t9search.framework.app.AppManager;
import cn.kli.t9search.framework.base.BaseFragment;
import cn.kli.t9search.framework.widget.SettingItemView;
import cn.kli.t9search.framework.widget.SettingItemView.OnSettingItemClickListener;

public class SettingsFragment extends BaseFragment implements OnSettingItemClickListener{
    
    private SettingItemView mRebuildView;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_settings;
    }

    @Override
    public void initViews(View root) {
        mRebuildView = (SettingItemView)root.findViewById(R.id.siv_rebuild_index);
        mRebuildView.setOnSettingClickListener(this);
    }

    @Override
    public void initDatas() {
        
    }

    @Override
    public void onSettingItemClick(View view) {
        switch(view.getId()){
        case R.id.siv_rebuild_index:
            AppManager.getInstance().startLoadTask(getActivity(), null, true);
            break;
        }
    }

}
