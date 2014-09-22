package com.carl.t9search.module.settings.appselection;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.carl.t9search.R;
import com.carl.t9search.framework.app.AppInfo;
import com.carl.t9search.framework.base.BaseItemView;

public class AppInfoSelectionItemView extends BaseItemView {

    private ImageView mIconView;
    private TextView mTitleView;

    public AppInfoSelectionItemView(Context context) {
        super(context, R.layout.view_search_item);
        mIconView = (ImageView) findViewById(R.id.iv_icon);
        mTitleView = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    public void bindData(Object data) {
        if (data == null) {
            mIconView.setVisibility(View.GONE);
            mTitleView.setText("取消快捷方式");
        } else {
            AppInfo info = (AppInfo) data;
            mIconView.setVisibility(View.VISIBLE);
            mIconView.setImageBitmap(info.icon);
            mTitleView.setText(info.title);
        }
    }

}
