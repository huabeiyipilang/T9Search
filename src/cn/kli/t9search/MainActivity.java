package cn.kli.t9search;

import com.baidu.mobstat.StatService;
import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import cn.kli.t9search.IDataList.OnItemSelectListener;
import cn.kli.t9search.SearchManager.SearchMode;

public class MainActivity extends Activity implements TextWatcher, IViewsContainer{
	private DigitsEditText input;
	private IDataList mList;
	private ViewGroup mListContainer;
	private T9KeyBoard mKeyBoard;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		EasyTracker.getInstance().setContext(this);
		//views
		mListContainer = (ViewGroup)findViewById(R.id.list_container);
		input = (DigitsEditText)findViewById(R.id.input);
		input.addTextChangedListener(this);
		mKeyBoard = (T9KeyBoard)findViewById(R.id.keyboard);
		mKeyBoard.setDigitsView(input);
		Mediator.getInstance().setContainer(this);
		Mediator.getInstance().setDigits(input);
		Mediator.getInstance().setKeyBoard(mKeyBoard);
		swapToMode(SearchMode.APP_MODE);
	}

	
	
	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this); 
	}



	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(this);
		Mediator.getInstance().showKeyboard();
	}



	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPause(this);
	}

	

/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_settings:
			startActivity(new Intent(this, Settings.class));
			break;
		}
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		return super.onMenuItemSelected(featureId, item);
	}
*/

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this); 
	}



	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1,
			int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		mList.updateQueryString(arg0.toString());
	}
	
	@Override
	public void onBackPressed() {
		if (isTaskRoot()) {
			if(Mediator.getInstance().isKeyboardShowing()){
				Mediator.getInstance().hideKeyboard();
			}else{
				moveTaskToBack(false);
			}
		}else{
			super.onBackPressed();
		}
	}

	private SearchMode mMode = SearchMode.APP_MODE;
	
	public void swapMode(SearchMode mode) {
		if(mMode == SearchMode.APP_MODE){
			mMode = SearchMode.CONTACTS_MODE;
		}else{
			mMode = SearchMode.APP_MODE;
		}
		swapToMode(mMode);
	}

	public void swapToMode(SearchMode mode){
		mList = DataListFactory.create(this, mMode);
		mList.setOnItemSelectListener(new OnItemSelectListener(){
			@Override
			public void onItemSelect(Object o) {
				input.getText().clear();
			}
		});
		mListContainer.removeAllViews();
		input.getText().clear();
		mListContainer.addView((View) mList);
		Mediator.getInstance().setDataList(mList);
	}

}
