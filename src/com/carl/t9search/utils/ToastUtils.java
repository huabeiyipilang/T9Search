package com.carl.t9search.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    public static void showToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
