package cn.kli.t9search.framework.base;

import cn.kli.t9search.analytics.Umeng;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

abstract public class BaseFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutRes(), container, false);
        initViews(rootView);
        initDatas();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Umeng.onFragmentResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Umeng.onFragmentPause(this);
    }

    abstract public int getLayoutRes();
    
    abstract public void initViews(View root);
    
    abstract public void initDatas();
}
