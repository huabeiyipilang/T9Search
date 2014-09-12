package com.carl.t9search.module.settings;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

import com.carl.t9search.R;
import com.carl.t9search.framework.app.AppInfo;
import com.carl.t9search.framework.base.BaseFragment;
import com.carl.t9search.framework.widget.SettingItemView;
import com.carl.t9search.framework.widget.SettingItemView.OnSettingItemClickListener;
import com.carl.t9search.module.settings.appselection.AppInfoSelectionFragment;
import com.carl.t9search.module.settings.appselection.AppInfoSelectionFragment.OnAppInfoSelectedListener;

public class QuickDialFragment extends BaseFragment implements OnAppInfoSelectedListener {

    private final int[] mKeyIds = { R.id.siv_quick_1, R.id.siv_quick_2, R.id.siv_quick_3, R.id.siv_quick_4,
            R.id.siv_quick_5, R.id.siv_quick_6, R.id.siv_quick_7, R.id.siv_quick_8, R.id.siv_quick_9 };
    
    private List<SettingItemView> mItemViews = new ArrayList<SettingItemView>();
    private SettingItemView mCurrentView;
    
    private OnSettingItemClickListener mOnItemClickListener = new OnSettingItemClickListener(){

        @Override
        public void onSettingItemClick(View view) {
            mCurrentView = (SettingItemView) view;
            AppInfoSelectionFragment.setOnAppInfoSelectedListener(QuickDialFragment.this);
            openFragment(QuickDialFragment.this, AppInfoSelectionFragment.class, null);
        }
        
    };

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_settings_quick_dial;
    }

    @Override
    public void initViews(View root) {
        for(int i = 0; i < mKeyIds.length; i++){
            SettingItemView item = (SettingItemView) root.findViewById(mKeyIds[i]);
            item.setTag(R.id.tag_index, i+1);
            item.setOnSettingClickListener(mOnItemClickListener);
            mItemViews.add(item);
        }
        updateUI();
    }

    @Override
    public void initDatas() {

    }

    private void updateUI(){
        for(SettingItemView item : mItemViews){
            int index = (Integer) item.getTag(R.id.tag_index);
            AppInfo info = SettingsManager.getQuickDial(index);
            item.setTag(R.id.tag_appinfo, info);
            
            AppInfo appInfo = (AppInfo) item.getTag(R.id.tag_appinfo);
            item.setSubTitle(appInfo == null ? "未设置" : appInfo.title);
        }
    }

    @Override
    public void onAppInfoSelected(AppInfo info) {
        SettingsManager.setQuickDial((Integer) mCurrentView.getTag(R.id.tag_index), info.intent);
        mCurrentView.setTag(R.id.tag_appinfo, info);
        updateUI();
    }
}
