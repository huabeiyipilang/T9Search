package com.carl.t9search.framework.base;

import java.util.ArrayList;
import java.util.List;

import com.carl.t9search.App;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ItemAdapter extends BaseAdapter {
    
    private List<? extends Object> mDatas = new ArrayList<Object>();
    private Class<? extends BaseItemView> mViewClass;
    private Context mContext = App.getContext();
    
    public ItemAdapter(){
        
    }
    
    public ItemAdapter(List<? extends Object> list, Class<? extends BaseItemView> viewClass){
        mDatas = list;
        mViewClass = viewClass;
    }
    
    public void setData(List<? extends Object> list){
        mDatas = list;
    }
    
    public void setView(Class<? extends BaseItemView> viewClass){
        mViewClass = viewClass;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mDatas == null || mDatas.size() == 0 ? null : mDatas.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View convertView, ViewGroup arg2) {
        BaseItemView view = null;
        if(convertView == null){
            try {
                view = (BaseItemView) mViewClass.getConstructor(Context.class)
                        .newInstance(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }else{
            view = (BaseItemView)convertView;
        }
        view.bindData(getItem(arg0));
        return view;
    }

}
