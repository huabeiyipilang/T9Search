package com.carl.t9search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.carl.t9search.analytics.Umeng;
import com.carl.t9search.framework.base.BaseActivity;
import com.carl.t9search.framework.base.BaseFragment;
import com.carl.t9search.module.search.SearchFragment;

public class MainActivity extends BaseActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentFragment(SearchFragment.class, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Umeng.init();
        Umeng.onActivityResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Umeng.onActivityPause();
    }

    protected void setContentFragment(Class<? extends BaseFragment> fragmentClass, Bundle arguments) {
        Fragment fragment = Fragment.instantiate(this, fragmentClass.getName(), arguments);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.content_frame, fragment);
        t.commit();
    }
}
