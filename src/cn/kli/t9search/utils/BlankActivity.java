package cn.kli.t9search.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import cn.kli.t9search.R;
import cn.kli.t9search.framework.base.BaseActivity;
import cn.kli.t9search.framework.base.BaseFragment;

public class BlankActivity extends BaseActivity {

    public static final String INTENT_FRAGMENT_NAME = "intent_fragment_name";
    
    public static void startFragmentActivity(Context context, Class<? extends BaseFragment> fragmentClass, Bundle extras) {
        Intent intent = new Intent(context, BlankActivity.class);
        intent.putExtra(INTENT_FRAGMENT_NAME, fragmentClass);
        if (null != extras)
            intent.putExtras(extras);
        context.startActivity(intent);
    }
    
    public static void startFragmentActivityNewTask(Context context, Class<? extends BaseFragment> fragmentClass, Bundle extras) {
        Intent intent = new Intent(context, BlankActivity.class);
        intent.putExtra(INTENT_FRAGMENT_NAME, fragmentClass);
        if (null != extras)
            intent.putExtras(extras);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_blank);
        @SuppressWarnings("unchecked")
        Class<? extends BaseFragment> fragmentClass = (Class<? extends BaseFragment>)getIntent().getSerializableExtra(INTENT_FRAGMENT_NAME);
        if (fragmentClass != null) {
            setContentFragment(fragmentClass, getIntent().getExtras());
        }
    }

    protected void setContentFragment(Class<? extends BaseFragment> fragmentClass, Bundle arguments) {
        Fragment fragment = Fragment.instantiate(this, fragmentClass.getName(), arguments);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.content, fragment);
        t.commit();
    }
}
