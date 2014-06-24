package cn.kli.t9search.module.search;

import java.util.ArrayList;
import java.util.List;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import cn.kli.t9search.R;
import cn.kli.t9search.framework.app.AppInfo;
import cn.kli.t9search.framework.app.AppManager;
import cn.kli.t9search.framework.base.BaseFragment;
import cn.kli.t9search.framework.base.ItemAdapter;
import cn.kli.t9search.module.search.KeyboardView.T9KeyboardListener;
import cn.kli.t9search.utils.BlurUtils;

public class SearchFragment extends BaseFragment implements T9KeyboardListener, OnItemClickListener {
    private GridView mGridView;
    private KeyboardView mKeyboardView;
    private ImageView mBkgView;
    
    private List<AppInfo> mAllAppList;
    private ItemAdapter mAdapter;
    
    @Override
    public int getLayoutRes() {
        return R.layout.fragment_search;
    }

    @Override
    public void initViews(View root) {
        mGridView = (GridView)root.findViewById(R.id.gv_list);
        mKeyboardView = (KeyboardView)root.findViewById(R.id.kbv_keyboard);
        mBkgView = (ImageView)root.findViewById(R.id.iv_bg);
        mKeyboardView.setOnDigitsChangedListener(this);
        initBackgroud();
    }

    @Override
    public void initDatas() {
        mAdapter = new ItemAdapter();
        mAdapter.setView(SearchItemView.class);
        mGridView.setAdapter(mAdapter);
        mAllAppList = AppManager.getInstance().getAllApps();
        updateList(mAllAppList);
        mGridView.setOnItemClickListener(this);
    }
    
    private void initBackgroud(){
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        final Bitmap bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();
        mBkgView.setImageBitmap(bm);
        new Thread(){

            @Override
            public void run() {
                super.run();
                final Bitmap bm1 = BlurUtils.blurFilter(bm);
                final TransitionDrawable transition = new TransitionDrawable(new Drawable[]{
                        mBkgView.getDrawable(), new BitmapDrawable(getResources(), bm1)
                });
                getActivity().runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        mBkgView.setImageDrawable(transition);
                        transition.startTransition(1000);
                    }
                    
                });
            }
            
        }.start();
    }

    @Override
    public void onDigitsChanged(String digits) {
        List<AppInfo> list = null;
        if(TextUtils.isEmpty(digits)){
            list = mAllAppList;
        }else{
            list = new ArrayList<AppInfo>();
            for(AppInfo info : mAllAppList){
                if(!TextUtils.isEmpty(info.keyword_quanpin_t9) && info.keyword_quanpin_t9.contains(digits)){
                    list.add(info);
                }
            }
        }
        updateList(list);
    }

    private void updateList(List<AppInfo> list){
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onOpenFirstClick() {
        Object o = mAdapter.getItem(0);
        if(o != null && o instanceof AppInfo){
            startApp((AppInfo)o);
        }
    }

    private void startApp(AppInfo info){
        if(info != null){
            startActivity(info.getIntent());
            info.count++;
            info.saveAsync();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Object o = mAdapter.getItem(position);
        if(o != null && o instanceof AppInfo){
            startApp((AppInfo)o);
        }
    }
}
