package cn.kli.t9search.module.settings;

import android.view.View;
import cn.kli.t9search.R;
import cn.kli.t9search.framework.app.AppManager;
import cn.kli.t9search.framework.base.BaseFragment;
import cn.kli.t9search.framework.widget.SettingItemView;
import cn.kli.t9search.framework.widget.SettingItemView.OnSettingItemCheckListener;
import cn.kli.t9search.framework.widget.SettingItemView.OnSettingItemClickListener;

import com.umeng.fb.FeedbackAgent;

public class SettingsFragment extends BaseFragment implements OnSettingItemClickListener, OnSettingItemCheckListener{
    
    private SettingItemView mRebuildView;
    private SettingItemView mVibrateView;
    private SettingItemView mSoundView;
    private SettingItemView mFeedbackView;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_settings;
    }

    @Override
    public void initViews(View root) {
        mRebuildView = (SettingItemView)root.findViewById(R.id.siv_rebuild_index);
        mRebuildView.setOnSettingClickListener(this);
        
        mVibrateView = (SettingItemView)root.findViewById(R.id.siv_fb_vibrate);
        mVibrateView.setSwitchChecked(SettingsManager.getVibrateFeedback());
        mVibrateView.setOnCheckChangeListener(this);
        
        mSoundView = (SettingItemView)root.findViewById(R.id.siv_fb_sound);
        mSoundView.setSwitchChecked(SettingsManager.getSoundFeedback());
        mSoundView.setOnCheckChangeListener(this);
        mSoundView.setVisibility(View.GONE);
        
        mFeedbackView = (SettingItemView)root.findViewById(R.id.siv_feedback);
        mFeedbackView.setOnSettingClickListener(this);
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
        case R.id.siv_feedback:
            FeedbackAgent agent = new FeedbackAgent(getActivity());
            agent.startFeedbackActivity();
            break;
        }
    }

    @Override
    public void onSettingItemCheckChanged(View view, boolean checked) {
        switch(view.getId()){
        case R.id.siv_fb_vibrate:
            SettingsManager.setVibrateFeedback(checked);
            break;
        case R.id.siv_fb_sound:
            SettingsManager.setSoundFeedback(checked);
            break;
        }
    }

}
