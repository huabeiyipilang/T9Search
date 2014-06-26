package cn.kli.t9search;

import android.os.Bundle;
import cn.kli.t9search.framework.base.BaseActivity;
import cn.kli.t9search.module.search.SearchFragment;

public class MainActivity extends BaseActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setContentFragment(SearchFragment.class, null);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
//        super.onBackPressed();
    }

    
    
}
