package com.carl.t9search.module.search;

import java.util.ArrayList;
import java.util.List;

import com.carl.t9search.App;
import com.carl.t9search.R;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import com.carl.t9search.analytics.Umeng;
import com.carl.t9search.framework.app.AppInfo;
import com.carl.t9search.framework.app.AppManager;
import com.carl.t9search.framework.app.AppManager.OnAppChangedListener;
import com.carl.t9search.framework.app.IAppLoadListener;
import com.carl.t9search.framework.app.LoadTask;
import com.carl.t9search.framework.base.BaseFragment;
import com.carl.t9search.framework.base.ItemAdapter;
import com.carl.t9search.module.search.KeyboardView.T9KeyboardListener;
import com.carl.t9search.module.settings.SettingsManager;
import com.carl.t9search.utils.BlurUtils;
import com.carl.t9search.utils.ToastUtils;

public class SearchFragment extends BaseFragment implements T9KeyboardListener, OnItemClickListener,
            OnAppChangedListener {
    private GridView mGridView;
    private KeyboardView mKeyboardView;
    private ImageView mBkgView;
    private View mSearchViews;

    private List<AppInfo> mAllAppList;
    private ItemAdapter mAdapter;
    
    private LoadTask mLoadTask;
    private com.carl.t9search.utils.NetUtils mNetUtils;

    private AppManager mAppManager = AppManager.getInstance();
    
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

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
        initBackgroud();
    }

    @Override
    public void initDatas() {
        if(mAppManager.isInited()){
            mAdapter = new ItemAdapter();
            mAdapter.setView(SearchItemView.class);
            mGridView.setAdapter(mAdapter);
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
        }else{
            loadApp();
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        mAppManager.listenAppListChanged(this);
        mKeyboardView.showKeyboard(true);
        if(mAllAppList == null){
            new Thread(){
                
                @Override
                public void run() {
                    super.run();
                    if (mAppManager.isInited()) {
                        mAllAppList = AppManager.getInstance().getAllApps();
                        updateList();
                    }
                }
                
            }.start();
        }
            
    }

    private void loadApp(){
        mLoadTask = mAppManager.startLoadTask(getActivity(), new IAppLoadListener(){

            @Override
            public void onProgressUpdate(float progress, float max, String info) {
                
            }

            @Override
            public void onFinished() {
                mAppManager.setInited(true);
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
                final Bitmap bm1 = Bitmap.createScaledBitmap(bm, bm.getWidth()/4, bm.getHeight()/4, true);
                mMainHandler.post(new Runnable(){

                    @Override
                    public void run() {
                        mBkgView.setImageBitmap(bm1);
                    }

                });
                final Bitmap bm2 = BlurUtils.blurFilter(bm1);
                final TransitionDrawable transition = new TransitionDrawable(new Drawable[]{
                        mBkgView.getDrawable(), new BitmapDrawable(App.getContext().getResources(), bm2)});
                mMainHandler.post(new Runnable(){

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
    protected boolean onBackKeyDown() {
        if(SearchItemView.sDelMode){
            mKeyboardView.exitDelMode();
            mAdapter.notifyDataSetChanged();
        }else if(mKeyboardView.isKeyboardShow()){
            mKeyboardView.showKeyboard(false);
        }else{
            hide();
        }
        return true;
    }
    
    private void hide(){
        getActivity().moveTaskToBack(true);
    }

    @Override
    public void onDigitsChanged(String digits) {
        filterList(digits);
    }
    
    private void updateList(){
        filterList(mKeyboardView.getDigits());
    }
    
    private void filterList(String keyword){
        List<AppInfo> list = null;
        if(TextUtils.isEmpty(keyword)){
            list = mAllAppList;
        }else{
            list = new ArrayList<AppInfo>();
            if(mAllAppList != null){
                for(AppInfo info : mAllAppList){
                    if(!TextUtils.isEmpty(info.keyword_quanpin_t9) && info.keyword_quanpin_t9.contains(keyword)){
                        list.add(info);
                    }
                }
            }
        }
        try {
            updateList(list);
        } catch (Exception e) {
        }
    }

    private void updateList(List<AppInfo> list){
        mAdapter.setData(list);
        mMainHandler.post(new Runnable(){

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
            startApp(Umeng.OPEN_APP_BY_OPEN_BUTTON, (AppInfo)o);
        }
    }

    private void startApp(int from, AppInfo info){
        if(info != null){
            try {
                startActivity(info.getIntent());
            } catch (Exception e) {
                Toast.makeText(getActivity(), "应用不存在，请到设置中，重建索引", Toast.LENGTH_SHORT).show();
                return;
            }
            if(SettingsManager.getHideAfterOpenApp()){
                hide();
            }
            info.count++;
            info.saveAsync();
            new Thread(){

                @Override
                public void run() {
                    super.run();
                    mAllAppList = AppManager.getInstance().getAllApps();
                    updateList();
                }
                
            }.start();
            Umeng.onEventOpenApp(from, info);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Object o = mAdapter.getItem(position);
        if(o != null && o instanceof AppInfo){
            AppInfo app =  (AppInfo)o;
            if(SearchItemView.sDelMode){
                Uri uri = Uri.fromParts("package", app.packageName, null);
                Intent intent = new Intent(Intent.ACTION_DELETE, uri);
                startActivity(intent);
            }else{
                startApp(Umeng.OPEN_APP_BY_CLICK_ICON, app);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mLoadTask != null && !mLoadTask.isCancelled()){
            mLoadTask.cancel(true);
        }
        mKeyboardView.clearInput();
        mAppManager.unlistenAppListChanged(this);
    }

    @Override
    public void onAppChanged() {
        mKeyboardView.clearInput();
        mAllAppList = AppManager.getInstance().getAllApps();
        updateList();
    }

    @Override
    public void onListTypeChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onKeyLongClick(String key) {
        AppInfo info = SettingsManager.getQuickDial(Integer.parseInt(key));
        if(info == null){
            ToastUtils.showToast(getActivity(), "按键\""+key+"\"未设置快捷键");
        }else{
            startApp(Umeng.OPEN_APP_BY_QUICK_DIAL, info);
        }
    }
    
}
