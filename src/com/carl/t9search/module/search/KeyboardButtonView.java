package com.carl.t9search.module.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carl.t9search.R;

public class KeyboardButtonView extends LinearLayout {
    
    private ImageView mBgView;
    private ImageView mIconView;
    private TextView mTitleView;
    private TextView mSubTitleView;

    public KeyboardButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);inflate(getContext(), R.layout.view_keyboard_button, this);
        setClickable(true);
        initViews();
        initAttrs(attrs);
    }
    
    private void initViews(){
        mBgView = (ImageView)findViewById(R.id.iv_bg);
        mIconView = (ImageView)findViewById(R.id.iv_icon);
        mTitleView = (TextView)findViewById(R.id.tv_title);
        mSubTitleView = (TextView)findViewById(R.id.tv_sub);
    }
    
    public void setBg(Bitmap bitmap){
        mBgView.setImageBitmap(bitmap);
    }
    
    public void setTitle(String title){
        mTitleView.setText(title);
    }
    
    @SuppressLint("Recycle")
    private void initAttrs(AttributeSet attrs){
        TypedArray t = getContext().obtainStyledAttributes(attrs,R.styleable.KeyboardButtonView);
        
        int resId = t.getResourceId(R.styleable.KeyboardButtonView_buttonIcon, 0);
        if(resId == 0){
            mIconView.setVisibility(View.GONE);
            String title = t.getString(R.styleable.KeyboardButtonView_mainText);
            String subTitle = t.getString(R.styleable.KeyboardButtonView_subText);
            mTitleView.setText(title);
            if(TextUtils.isEmpty(subTitle)){
                mSubTitleView.setVisibility(View.GONE);
            }else{
                mSubTitleView.setText(subTitle);
            }
        }else{
            mTitleView.setVisibility(View.GONE);
            mSubTitleView.setVisibility(View.GONE);
            mIconView.setVisibility(View.VISIBLE);
            mIconView.setImageResource(resId);
        }
        
    }
}
