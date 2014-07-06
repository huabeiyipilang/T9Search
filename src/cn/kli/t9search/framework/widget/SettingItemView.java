package cn.kli.t9search.framework.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kli.t9search.R;

public class SettingItemView extends LinearLayout {

    public static final int TYPE_CLICK = 1;
    public static final int TYPE_CHECK = 2;
    public static final int TYPE_TITLE = 3;

    private TextView mTitleView;
    private TextView mSubTitleView;
    private CheckBox mSwitchView;
    
    private OnSettingItemClickListener mOnClickListener;
    
    private int mType = TYPE_CLICK;
    
    public interface OnSettingItemClickListener{
        void onSettingItemClick(View view);
    }
    
    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.view_setting_item, this);
        
        this.setClickable(true);
        this.setBackgroundResource(R.drawable.setting_item_bkg);
        
        initViews();
        initAttrs(attrs);
        setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                switch(mType){
                case TYPE_CHECK:
                    mSwitchView.toggle();
                    break;
                case TYPE_CLICK:
                    if(mOnClickListener != null){
                        mOnClickListener.onSettingItemClick(SettingItemView.this);
                    }
                    break;
                }
            }
        });
    }
    
    private void initViews(){
        mTitleView = (TextView)findViewById(R.id.tv_title);
        mSubTitleView = (TextView)findViewById(R.id.tv_subtitle);
        mSwitchView = (CheckBox)findViewById(R.id.cb_switch);
    }
    
    @SuppressLint("Recycle")
    private void initAttrs(AttributeSet attrs){
        TypedArray t = getContext().obtainStyledAttributes(attrs,R.styleable.SettingItemView);
        
        int type = t.getInt(R.styleable.SettingItemView_settingType, TYPE_CLICK);
        String title = t.getString(R.styleable.SettingItemView_titleText);
        String subTitle = t.getString(R.styleable.SettingItemView_subTitleText);

        if(TextUtils.isEmpty(subTitle)){
            mSubTitleView.setVisibility(View.GONE);
        }else{
            mSubTitleView.setText(subTitle);
        }
        
        switch(type){
        case TYPE_CHECK:
            mTitleView.setText(title);
            mSwitchView.setVisibility(View.VISIBLE);
            break;
        case TYPE_CLICK:
            mTitleView.setText(title);
            mSwitchView.setVisibility(View.GONE);
            break;
        case TYPE_TITLE:
            mSubTitleView.setText(title);
            mSubTitleView.setVisibility(View.VISIBLE);
            mTitleView.setVisibility(View.GONE);
            mSwitchView.setVisibility(View.GONE);
            setEnabled(false);
            break;
        }
    }
    
    public void setOnCheckChangeListener(OnCheckedChangeListener listener){
        mSwitchView.setOnCheckedChangeListener(listener);
    }
    
    public void setSwitchChecked(boolean checked){
        mSwitchView.setChecked(checked);
    }
    
    public void setOnSettingClickListener(OnSettingItemClickListener listener){
        mOnClickListener = listener;
    }

}
