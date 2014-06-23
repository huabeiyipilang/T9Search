package cn.kli.t9search;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.kli.t9search.framework.app.AppManager;
import cn.kli.t9search.framework.app.IAppLoadListener;
import cn.kli.t9search.module.search.SearchFragment;
import cn.kli.t9search.utils.BlankActivity;
import cn.kli.t9search.utils.PinYinUtils;

public class MainActivity extends Activity {

    private TextView mDisplay;
    private Button mStart;
    private EditText mHanziInput;
    private TextView mPinyinOutput;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDisplay = (TextView)findViewById(R.id.display);
        mHanziInput = (EditText)findViewById(R.id.et_pinyin_test);
        mPinyinOutput = (TextView)findViewById(R.id.tv_pinyin_list);
        
        mStart = (Button)findViewById(R.id.start);
        mStart.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                AppManager.getInstance().startLoadTask(new IAppLoadListener(){

                    @Override
                    public void onProgressUpdate(float progress) {
                        mDisplay.setText(""+progress);
                    }
                    
                });
            }
        });
        
        findViewById(R.id.open).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                BlankActivity.startFragmentActivityNewTask(getApplicationContext(), SearchFragment.class, null);
            }
        });
        
        findViewById(R.id.bt_translate).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                String input = mHanziInput.getText().toString();
                if(TextUtils.isEmpty(input)){
                    mPinyinOutput.setText("不能为空");
                    return;
                }
                List<String> outputs = PinYinUtils.string2PinYin(input);
                StringBuilder sb = new StringBuilder();
                for(String pinyin : outputs){
                    sb.append(pinyin+"\n");
                }
                mPinyinOutput.setText(sb.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
