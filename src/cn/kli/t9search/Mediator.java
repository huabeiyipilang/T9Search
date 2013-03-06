package cn.kli.t9search;
import cn.kli.t9search.SearchManager.SearchMode;

public class Mediator {
	private static Mediator sInstance;
	
	private IDataList mDataList;
	private T9KeyBoard mKeyBoard;
	private DigitsEditText mDigits;
	private IViewsContainer mContainer;
	
	private Mediator(){
		
	}
	
	public static Mediator getInstance(){
		if(sInstance == null){
			sInstance = new Mediator();
		}
		return sInstance;
	}

	public void setContainer(IViewsContainer mContainer) {
		this.mContainer = mContainer;
	}

	public void setDataList(IDataList mDataList) {
		this.mDataList = mDataList;
	}

	public void setKeyBoard(T9KeyBoard mKeyBoard) {
		this.mKeyBoard = mKeyBoard;
	}

	public void setDigits(DigitsEditText mDigits) {
		this.mDigits = mDigits;
	}
	
	
	public void swapSearchMode(SearchMode mode){
		mContainer.swapMode(mode);
	}
	
	public boolean isKeyboardShowing(){
		return mKeyBoard.isShowing();
	}
	
	public void hideKeyboard(){
		mKeyBoard.hide();
	}
	
	public void showKeyboard(){
		mKeyBoard.show();
	}
	
	public void selectFirstOne(){
		mDataList.selectTheFirst();
	}
	
	public void keyboardClear(){
		mKeyBoard.clear();
	}
}
