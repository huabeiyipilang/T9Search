package cn.kli.t9search.module.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kli.t9search.R;
import cn.kli.t9search.framework.app.AppInfo;
import cn.kli.t9search.framework.base.BaseItemView;

public class SearchItemView extends BaseItemView {

    private ImageView mIconView;
    private TextView mTitleView;
    private TextView mTestView;
    private View mDelView;
    
    static boolean sDelMode;
    
    public SearchItemView(Context context) {
        super(context, R.layout.view_search_item);
        mIconView = (ImageView)findViewById(R.id.iv_icon);
        mTitleView = (TextView)findViewById(R.id.tv_title);
        mTestView = (TextView)findViewById(R.id.tv_test);
        mDelView = findViewById(R.id.iv_del_icon);
    }

    @SuppressLint("NewApi")
    @Override
    public void bindData(Object data) {
        AppInfo info = (AppInfo)data;
        mIconView.setImageBitmap(info.icon);
        mTitleView.setText(info.title);
        mTestView.setText(info.keyword_quanpin_t9);
        mDelView.setVisibility(sDelMode ? View.VISIBLE : View.GONE);
    }

}
