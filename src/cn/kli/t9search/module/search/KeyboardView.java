package cn.kli.t9search.module.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kli.t9search.R;

public class KeyboardView extends LinearLayout {
    
    private ImageButton mShowKeyboardView;
    private View mKeyboardView;
    private ImageButton mDelView;
    private Button mOkView;
    private ImageButton mHideView;
    private TextView mDigitsView;
    
    private OnDigitsChangedListener mListener;
    private DigitsController mDigitsController = new DigitsController();
    
    public interface OnDigitsChangedListener{
        void onDigitsChanged(String digits);
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.keyboard_layout, this);
        initViews();
    }

    private void initViews(){
        mShowKeyboardView = (ImageButton)findViewById(R.id.btn_keyboard_show);
        mKeyboardView = findViewById(R.id.ll_keyboard);
        mDelView = (ImageButton)findViewById(R.id.del);
        mOkView = (Button)findViewById(R.id.ok);
        mHideView = (ImageButton)findViewById(R.id.hide);
        mDigitsView = (TextView)findViewById(R.id.input);
        
        int[] keyids = {R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight,
                R.id.nine, R.id.zero};
        View.OnClickListener onDigitsClickListener = new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                mDigitsController.push(String.valueOf(view.getTag()));
            }
        };
        
        for(int id : keyids){
            findViewById(id).setOnClickListener(onDigitsClickListener);
        }
        
        mDelView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                mDigitsController.del();
            }
        });
        mDelView.setOnLongClickListener(new OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View v) {
                mDigitsController.clear();
                return true;
            }
        });
    }
    
    public void setOnDigitsChangedListener(OnDigitsChangedListener listener){
        mListener = listener;
    }
    
    private void onDigitsChanged(String digits){
        mDigitsView.setText(digits);
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
