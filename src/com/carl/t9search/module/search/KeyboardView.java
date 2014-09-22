package com.carl.t9search.module.search;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.kli.libs.lockscreen.LockScreenUtils;

import com.carl.t9search.R;
import com.carl.t9search.framework.app.AppInfo;
import com.carl.t9search.module.settings.SettingsFragment;
import com.carl.t9search.module.settings.SettingsManager;
import com.carl.t9search.utils.BlankActivity;
import com.carl.t9search.utils.VibrateUtils;

public class KeyboardView extends LinearLayout implements OnClickListener {
    
    private static final int[] keyids = {R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight,
            R.id.nine};
    
    private KeyboardButtonView mShowKeyboardView;
    private View mKeyboardView;
    private View mDelView;
    private View mOkView;
    private View mHideView;
    private TextView mDigitsView;
    private View mLockScreenView;
    private KeyboardButtonView mUninstallView;
    
    private T9KeyboardListener mListener;
    private DigitsController mDigitsController = new DigitsController();
    
    public interface T9KeyboardListener{
        void onDigitsChanged(String digits);
        void onOpenFirstClick();
        void onListTypeChanged();
        void onKeyLongClick(String key);
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
        mUninstallView.setTitle(SearchItemView.sDelMode ? "取消" : "卸载");
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
        mShowKeyboardView = (KeyboardButtonView)findViewById(R.id.btn_keyboard_show);
        mKeyboardView = findViewById(R.id.ll_keyboard);
        mDelView = findViewById(R.id.del);
        mOkView = findViewById(R.id.ok);
        mHideView = findViewById(R.id.hide);
        mDigitsView = (TextView)findViewById(R.id.input);
        mLockScreenView = findViewById(R.id.bt_lock_screen);
        mUninstallView = (KeyboardButtonView)findViewById(R.id.bt_uninstall);
        
        freshQuickDialUI(-1);
        
        View.OnClickListener onClickListener = new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                onKeyClickFeedback();
                mDigitsController.push(String.valueOf(view.getTag()));
            }
        };
        
        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View view) {
                if(mListener != null){
                    mListener.onKeyLongClick(String.valueOf(view.getTag()));
                    return true;
                }
                return false;
            }
        };
        
        for(int id : keyids){
            View keyButton = findViewById(id);
            keyButton.setOnClickListener(onClickListener);
            keyButton.setOnLongClickListener(onLongClickListener);
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
    
    public void freshQuickDialUI(int index){
        if(index < 0){
            for(int i = 1; i <= 9; i++){
                AppInfo info = SettingsManager.getQuickDial(i);
                if(info != null){
                    Bitmap icon = info.icon;
                    Bitmap bg = Bitmap.createBitmap(icon, icon.getWidth()/10, (int)icon.getHeight()/10, (int)icon.getWidth()*4/5, (int)icon.getHeight()*4/5, null, true);
                    ((KeyboardButtonView)findViewById(keyids[i])).setBg(bg);
                }
            }
        }else{
            AppInfo info = SettingsManager.getQuickDial(index);
            Bitmap bg = null;
            if(info != null){
                Bitmap icon = info.icon;
                bg = Bitmap.createBitmap(icon, icon.getWidth()/10, (int)icon.getHeight()/10, (int)icon.getWidth()*4/5, (int)icon.getHeight()*4/5, null, true);
            }
            ((KeyboardButtonView)findViewById(keyids[index])).setBg(bg);
        }
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
