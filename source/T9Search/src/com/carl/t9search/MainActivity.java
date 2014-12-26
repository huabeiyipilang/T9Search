package com.carl.t9search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;
import com.carl.t9search.analytics.Umeng;
import com.carl.t9search.framework.base.BaseActivity;
import com.carl.t9search.framework.base.BaseFragment;
import com.carl.t9search.module.search.SearchFragment;
import com.carl.t9search.utils.Logger;
import com.carl.t9search.utils.PrefUtils;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends BaseActivity {

	InterstitialAd interAd;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Umeng.init();
        setContentView(R.layout.activity_main);
        setContentFragment(SearchFragment.class, null);
        setupInsertAds();
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

    protected void setContentFragment(Class<? extends BaseFragment> fragmentClass, Bundle arguments) {
        Fragment fragment = Fragment.instantiate(this, fragmentClass.getName(), arguments);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.content_frame, fragment);
        t.commit();
    }

    private void setupInsertAds(){
    	interAd=new InterstitialAd(this);
		interAd.setListener(new InterstitialAdListener(){

			@Override
			public void onAdClick(InterstitialAd arg0) {
			}

			@Override
			public void onAdDismissed() {
				interAd.loadAd();
			}

			@Override
			public void onAdFailed(String arg0) {
			}

			@Override
			public void onAdPresent() {
			}

			@Override
			public void onAdReady() {
		    	String value = MobclickAgent.getConfigParams(App.getContext(), "insert_ad_interval");
		    	int interval = 0;
		    	try {
					interval = Integer.valueOf(value);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
		    	if(interval == 0){
		    		interval = 4;
		    	}
		    	Logger.info("interval:"+interval);
				if(System.currentTimeMillis() - PrefUtils.getLong("interad_last_show_time", 0) >= interval * 60 * 60 * 1000){
					interAd.showAd(MainActivity.this);
					PrefUtils.setLong("interad_last_show_time", System.currentTimeMillis());
				}
			}
			
		});
		interAd.loadAd();
    }
}
