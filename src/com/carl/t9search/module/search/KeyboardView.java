package com.carl.t9search.module.search;

import cn.kli.libs.lockscreen.LockScreenUtils;

import com.carl.t9search.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.carl.t9search.module.settings.SettingsFragment;
import com.carl.t9search.module.settings.SettingsManager;
import com.carl.t9search.utils.BlankActivity;
import com.carl.t9search.utils.VibrateUtils;

public class KeyboardView extends LinearLayout implements OnClickListener {
    
    private ImageButton mShowKeyboardView;
    private View mKeyboardView;
    private ImageButton mDelView;
    private Button mOkView;
    private ImageButton mHideView;
    private TextView mDigitsView;
    private Button mLockScreenView;
    private Button mUninstallView;
    
    private T9KeyboardListener mListener;
    private DigitsController mDigitsController = new DigitsController();
    
    public interface T9KeyboardListener{
        void onDigitsChanged(String digits);
        void onOpenFirstClick();
        void onListTypeChanged();
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.keyboard_layout, this);
        initViews();
    }
    
    public void exitDelMode(){
        if(SearchItemView.sDelMode){
            SearchItemView.sDelMode = false;
            updateUi();
        }
    }
    
    private void updateUi(){
        mUninstallView.setText(SearchItemView.sDelMode ? "取消" : "卸载");
    }


    @Override
    public void onClick(View view) {
        onKeyClickFeedback();
        switch(view.getId()){
        case R.id.btn_keyboard_show:
            showKeyboard(true);
            break;
        case R.id.del:
            mDigitsController.del();
            break;
        case R.id.ok:
            if(mListener != null){
                mListener.onOpenFirstClick();
            }
            break;
        case R.id.hide:
            showKeyboard(false);
            break;
        case R.id.bt_lock_screen:
            LockScreenUtils.getInstance(getContext()).lockScreen();
            break;
        case R.id.bt_uninstall:
            SearchItemView.sDelMode = !SearchItemView.sDelMode;
            updateUi();
            if(mListener != null){
                mListener.onListTypeChanged();
            }
        }
    }
    
    public void clearInput(){
        mDigitsController.clear();
    }
    
    private void onKeyClickFeedback(){
        
        if(SettingsManager.getVibrateFeedback()){
            VibrateUtils.getInstance().vibrateShortly();
        }
        
    }
    
    private void initViews(){
        mShowKeyboardView = (ImageButton)findViewById(R.id.btn_keyboard_show);
        mKeyboardView = findViewById(R.id.ll_keyboard);
        mDelView = (ImageButton)findViewById(R.id.del);
        mOkView = (Button)findViewById(R.id.ok);
        mHideView = (ImageButton)findViewById(R.id.hide);
        mDigitsView = (TextView)findViewById(R.id.input);
        mLockScreenView = (Button)findViewById(R.id.bt_lock_screen);
        mUninstallView = (Button)findViewById(R.id.bt_uninstall);
        
        int[] keyids = {R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight,
                R.id.nine, R.id.zero};
        
        for(int id : keyids){
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    onKeyClickFeedback();
                    mDigitsController.push(String.valueOf(view.getTag()));
                }
            });
        }
        
        mDelView.setOnClickListener(this);
        mOkView.setOnClickListener(this);
        mHideView.setOnClickListener(this);
        mShowKeyboardView.setOnClickListener(this);
        mLockScreenView.setOnClickListener(this);
        mUninstallView.setOnClickListener(this);
        mDelView.setOnLongClickListener(new OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View v) {
                mDigitsController.clear();
                return true;
            }
        });
        
        findViewById(R.id.ib_setting).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                BlankActivity.startFragmentActivity(getContext(), SettingsFragment.class, null);
            }
        });
    }
    
    public void setOnDigitsChangedListener(T9KeyboardListener listener){
        mListener = listener;
    }
    
    private void onDigitsChanged(String digits){
        mDigitsView.setText(digits);
    }
    
    public String getDigits(){
        return mDigitsView.getText().toString();
    }
    
    public void showKeyboard(boolean show){
        mKeyboardView.setVisibility(show ? View.VISIBLE : View.GONE);
        mShowKeyboardView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    public boolean isKeyboardShow(){
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }
    
    private class DigitsController{
        String lastDigits = "";
        StringBuilder sb = new StringBuilder();
        
        void push(String digit){
            sb.append(digit);
            notifyListener();
        }
        
        void del(){
            if(sb.length() > 0){
                sb.deleteCharAt(sb.length() - 1);
                notifyListener();
            }
        }
        
        void clear(){
            sb = new StringBuilder();
            notifyListener();
        }
        
        void notifyListener(){
            String digits = sb.toString();
            if(!lastDigits.equals(digits)){
                lastDigits = digits;
                onDigitsChanged(digits);
                if(mListener != null){
                    mListener.onDigitsChanged(digits);
                }
            }
        }
    }
}
