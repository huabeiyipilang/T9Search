package com.carl.t9search.framework.base;

import android.content.Context;
import android.widget.RelativeLayout;

abstract public class BaseItemView extends RelativeLayout {

    public BaseItemView(Context context, int layout) {
        super(context);
        inflate(context, layout, this);
    }

    abstract public void bindData(Object data);
}
