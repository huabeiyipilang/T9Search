package com.carl.t9search.framework.base;

import java.util.ArrayList;
import java.util.List;

import com.carl.t9search.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import com.carl.t9search.analytics.Umeng;

public class BaseActivity extends ActionBarActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Umeng.onActivityResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Umeng.onActivityPause(this);
    }

    protected void setContentFragment(Class<? extends BaseFragment> fragmentClass, Bundle arguments) {
        Fragment fragment = Fragment.instantiate(this, fragmentClass.getName(), arguments);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.content_frame, fragment);
        t.commit();
    }
    
    
    
    @Override
    public void onBackPressed() {
        for(BaseFragment fragment : getAvailableFragment()){
            if(fragment.onBackKeyDown()){
                return;
            }
        }
        super.onBackPressed();
    }

    private List<BaseFragment> getAvailableFragment(){
        List<BaseFragment> res = new ArrayList<BaseFragment>();
        List<Fragment> list = getSupportFragmentManager().getFragments();
        for(Fragment fragment : list){
            if(fragment instanceof BaseFragment && fragment.isVisible()){
                res.add((BaseFragment)fragment);
            }
        }
        return res;
    }
}
