package com.carl.t9search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.alimama.mobile.sdk.MmuSDK;
import com.alimama.mobile.sdk.config.BannerController;
import com.alimama.mobile.sdk.config.BannerProperties;
import com.alimama.mobile.sdk.config.InsertController;
import com.alimama.mobile.sdk.config.InsertProperties;
import com.alimama.mobile.sdk.config.MmuSDKFactory;
import com.carl.t9search.analytics.Umeng;
import com.carl.t9search.framework.app.AppManager;
import com.carl.t9search.framework.base.BaseActivity;
import com.carl.t9search.framework.base.BaseFragment;
import com.carl.t9search.module.search.SearchFragment;

public class MainActivity extends BaseActivity {

    private BannerProperties mBannerProperties;
    private BannerController<?> mBannerController;
    
    private InsertProperties mInsertProperties;
    private InsertController<?> mInsertController;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Umeng.init();
        setContentView(R.layout.activity_main);
        setContentFragment(SearchFragment.class, null);
        showBanner();
        showInsert();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    public void onBackPressed() {
        boolean interrupt = false;
        if (mBannerController != null) {// 通知Banner推广返回键按下，如果Banner进行了一些UI切换将返回true
            // 否则返回false(如从 expand状态切换会normal状态将返回true)
            interrupt = mBannerController.onBackPressed();
        }
        
        if(mInsertController != null){
        	mInsertController.onBackPressed();
        }

        if (!interrupt)
            super.onBackPressed();
    }

    protected void setContentFragment(Class<? extends BaseFragment> fragmentClass, Bundle arguments) {
        Fragment fragment = Fragment.instantiate(this, fragmentClass.getName(), arguments);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.content_frame, fragment);
        t.commit();
    }
    
    private void showBanner(){
        ViewGroup nat = (ViewGroup) findViewById(R.id.ad_container);
        String slotId = "64272";
        BannerProperties properties = new BannerProperties(slotId, nat);
        mBannerController = (BannerController<?>) properties.getMmuController();

        MmuSDK mmuSDK = MmuSDKFactory.getMmuSDK();
        mmuSDK.init(getApplication());
        mmuSDK.attach(properties); 
    }
    
    private void showInsert(){
        ViewGroup parent = (ViewGroup) findViewById(R.id.content_frame);
        String slotId = "64273";
        InsertProperties properties = new InsertProperties(slotId, parent);
        mInsertController = (InsertController<?>) properties.getMmuController();
        
        MmuSDK mmu = MmuSDKFactory.getMmuSDK();
        mmu.init(getApplication());
        mmu.attach(properties);
        
        if(mInsertController != null){
        	mInsertController.load(slotId);
        }
    }
}
