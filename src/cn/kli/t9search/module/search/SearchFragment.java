package cn.kli.t9search.module.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import cn.kli.t9search.App;
import cn.kli.t9search.R;
import cn.kli.t9search.analytics.Umeng;
import cn.kli.t9search.framework.app.AppInfo;
import cn.kli.t9search.framework.app.AppManager;
import cn.kli.t9search.framework.app.IAppLoadListener;
import cn.kli.t9search.framework.app.LoadTask;
import cn.kli.t9search.framework.base.BaseFragment;
import cn.kli.t9search.framework.base.ItemAdapter;
import cn.kli.t9search.module.search.KeyboardView.T9KeyboardListener;
import cn.kli.t9search.utils.BlurUtils;

public class SearchFragment extends BaseFragment implements T9KeyboardListener, OnItemClickListener, Observer {
    private GridView mGridView;
    private KeyboardView mKeyboardView;
    private ImageView mBkgView;
    private View mSearchViews;

    private List<AppInfo> mAllAppList;
    private ItemAdapter mAdapter;
    
    private LoadTask mLoadTask;

    private AppManager mAppManager = AppManager.getInstance();

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_search;
    }

    @Override
    public void initViews(View root) {
        mSearchViews = root.findViewById(R.id.search_content);
        mGridView = (GridView)root.findViewById(R.id.gv_list);
        mKeyboardView = (KeyboardView)root.findViewById(R.id.kbv_keyboard);
        mBkgView = (ImageView)root.findViewById(R.id.iv_bg);

        mKeyboardView.setOnDigitsChangedListener(this);
//        initBackgroud();
    }

    @Override
    public void initDatas() {
        if(mAppManager.isInited()){
            mAdapter = new ItemAdapter();
            mAdapter.setView(SearchItemView.class);
            mGridView.setAdapter(mAdapter);
            mAllAppList = AppManager.getInstance().getAllApps();
            updateList(mAllAppList);
            mGridView.setOnItemClickListener(this);
            mGridView.setOnScrollListener(new OnScrollListener() {
                
                @Override
                public void onScrollStateChanged(AbsListView arg0, int state) {
                    if(state == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                        mKeyboardView.showKeyboard(false);
                    }
                }
                
                @Override
                public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
                    
                }
            });
            mAppManager.addObserver(this);
        }else{
            loadApp();
        }
    }

    private void loadApp(){
        mLoadTask = mAppManager.startLoadTask(getActivity(), new IAppLoadListener(){

            @Override
            public void onProgressUpdate(float progress, float max, String info) {
                
            }

            @Override
            public void onFinished() {
                initDatas();
            }
            
        }, true);
    }

    private void initBackgroud(){
        new Thread(){

            @Override
            public void run() {
                super.run();
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
                Drawable wallpaperDrawable = wallpaperManager.getDrawable();
                Bitmap bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();
                final Bitmap bm1 = Bitmap.createScaledBitmap(bm, bm.getWidth()/2, bm.getHeight()/2, true);
                try {
                    getActivity().runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            mBkgView.setImageBitmap(bm1);
                        }

                    });
                } catch (Exception e) {
                }
                final Bitmap bm2 = BlurUtils.blurFilter(bm1);
                final TransitionDrawable transition = new TransitionDrawable(new Drawable[]{
                        mBkgView.getDrawable(), new BitmapDrawable(App.getContext().getResources(), bm2)});
                try {
                    getActivity().runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            mBkgView.setImageDrawable(transition);
                            transition.startTransition(1000);
                        }

                    });
                } catch (Exception e) {
                }
            }

        }.start();
    }

    @Override
    public void onDigitsChanged(String digits) {
        List<AppInfo> list = null;
        if(TextUtils.isEmpty(digits)){
            list = mAllAppList;
        }else if("15110176045".equals(digits)){
            loadApp();
            return;
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
        getActivity().runOnUiThread(new Runnable(){

            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
            
        });
    }

    @Override
    public void onOpenFirstClick() {
        Object o = mAdapter.getItem(0);
        if(o != null && o instanceof AppInfo){
            startApp(true, (AppInfo)o);
        }
    }

    private void startApp(boolean fromOpenButton, AppInfo info){
        if(info != null){
            startActivity(info.getIntent());
            info.count++;
            info.saveAsync();
            new Thread(){

                @Override
                public void run() {
                    super.run();
                    mAllAppList = AppManager.getInstance().getAllApps();
                    updateList(mAllAppList);
                }
                
            }.start();
            Umeng.onEventOpenApp(fromOpenButton, info);
        }
        mKeyboardView.clearInput();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Object o = mAdapter.getItem(position);
        if(o != null && o instanceof AppInfo){
            startApp(false, (AppInfo)o);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mLoadTask != null && !mLoadTask.isCancelled()){
            mLoadTask.cancel(true);
        }
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        mKeyboardView.clearInput();
        mAllAppList = AppManager.getInstance().getAllApps();
        updateList(mAllAppList);
    }
    
    
}
