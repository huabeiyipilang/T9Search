package cn.kli.t9search;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

public class T9KeyBoard extends LinearLayout implements OnClickListener, OnLongClickListener {
	
    private static final int TONE_LENGTH_MS = 150;
    private static final int TONE_LENGTH_INFINITE = -1;
    private static final int TONE_RELATIVE_VOLUME = 80;
    private static final int DIAL_TONE_STREAM_TYPE = AudioManager.STREAM_DTMF;
    
    private static final String EMPTY_NUMBER = "";
    
	private Context mContext;
	private EditText mDigits;
    private final HapticFeedback mHaptic = new HapticFeedback();
    private final Object mToneGeneratorLock = new Object();
    private ToneGenerator mToneGenerator = new ToneGenerator(DIAL_TONE_STREAM_TYPE,
			TONE_RELATIVE_VOLUME);
    
    private View mKeyboard;
    private View mKeyboardShowButton;

	public T9KeyBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View root = inflater.inflate(R.layout.keyboard_layout, this, true);
		setupKeypad(root);
	}
	
    private void setupKeypad(View root) {
        int[] buttonIds = new int[] { R.id.one, R.id.two, R.id.three, R.id.four, R.id.five,
                R.id.six, R.id.seven, R.id.eight, R.id.nine, R.id.zero,
                R.id.del, R.id.hide, R.id.ok};
        for (int id : buttonIds) {
            root.findViewById(id).setOnClickListener(this);
        }

        root.findViewById(R.id.del).setOnLongClickListener(this);

		
		mKeyboard = root.findViewById(R.id.ll_keyboard);
		mKeyboardShowButton = root.findViewById(R.id.btn_keyboard_show);
		mKeyboardShowButton.setOnClickListener(this);
    }
    
    public void setDigitsView(EditText digits){
    	mDigits = digits;
    }

	@Override
	public boolean onLongClick(View arg0) {
		switch(arg0.getId()){
		case R.id.del:
			mDigits.getText().clear();
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View key) {
		switch (key.getId()) {
		case R.id.one:
			keyPressed(KeyEvent.KEYCODE_1);
			break;
		case R.id.two:
			keyPressed(KeyEvent.KEYCODE_2);
			break;
		case R.id.three:
			keyPressed(KeyEvent.KEYCODE_3);
			break;
		case R.id.four:
			keyPressed(KeyEvent.KEYCODE_4);
			break;
		case R.id.five:
			keyPressed(KeyEvent.KEYCODE_5);
			break;
		case R.id.six:
			keyPressed(KeyEvent.KEYCODE_6);
			break;
		case R.id.seven:
			keyPressed(KeyEvent.KEYCODE_7);
			break;
		case R.id.eight:
			keyPressed(KeyEvent.KEYCODE_8);
			break;
		case R.id.nine:
			keyPressed(KeyEvent.KEYCODE_9);
			break;
		case R.id.zero:
			keyPressed(KeyEvent.KEYCODE_0);
			break;
		case R.id.del:
			keyPressed(KeyEvent.KEYCODE_DEL);
			break;
		case R.id.hide:
			hide();
			break;
		case R.id.btn_keyboard_show:
			show();
			break;
		case R.id.ok:
			Mediator.getInstance().selectFirstOne();
			break;
		}

	}
	

    private void keyPressed(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_1:
                playTone(ToneGenerator.TONE_DTMF_1);
                break;
            case KeyEvent.KEYCODE_2:
                playTone(ToneGenerator.TONE_DTMF_2);
                break;
            case KeyEvent.KEYCODE_3:
                playTone(ToneGenerator.TONE_DTMF_3);
                break;
            case KeyEvent.KEYCODE_4:
                playTone(ToneGenerator.TONE_DTMF_4);
                break;
            case KeyEvent.KEYCODE_5:
                playTone(ToneGenerator.TONE_DTMF_5);
                break;
            case KeyEvent.KEYCODE_6:
                playTone(ToneGenerator.TONE_DTMF_6);
                break;
            case KeyEvent.KEYCODE_7:
                playTone(ToneGenerator.TONE_DTMF_7);
                break;
            case KeyEvent.KEYCODE_8:
                playTone(ToneGenerator.TONE_DTMF_8);
                break;
            case KeyEvent.KEYCODE_9:
                playTone(ToneGenerator.TONE_DTMF_9);
                break;
            case KeyEvent.KEYCODE_0:
                playTone(ToneGenerator.TONE_DTMF_0);
                break;
            case KeyEvent.KEYCODE_POUND:
                playTone(ToneGenerator.TONE_DTMF_P);
                break;
            case KeyEvent.KEYCODE_STAR:
                playTone(ToneGenerator.TONE_DTMF_S);
                break;
            default:
                break;
        }

        mHaptic.vibrate();
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        mDigits.onKeyDown(keyCode, event);

    }

	
	private void playTone(int tone) {
    	playTone(tone, TONE_LENGTH_MS);
    }
    
    private void playTone(int tone, int durationMs) {
        if(!SettingsHelper.getInstance().getBoolean(SettingsHelper.KEY_HAS_KEY_TONE, false)){
        	return;
        }
        AudioManager audioManager =
                (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();
        if ((ringerMode == AudioManager.RINGER_MODE_SILENT)
            || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
            return;
        }
   
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                Log.w("T9KeyBoard", "playTone: mToneGenerator == null, tone: " + tone);
                return;
            }

            // Start the new tone (will stop any playing tone)
            mToneGenerator.startTone(tone, durationMs);
        }
    }
    
    public boolean isShowing(){
    	return mKeyboard.getVisibility() == View.VISIBLE;
    }
    
    public void show(){
    	mKeyboardShowButton.setVisibility(View.GONE);
    	mKeyboard.setVisibility(View.VISIBLE);
    }
    
    public void hide(){
    	mKeyboard.setVisibility(View.GONE);
    	mKeyboardShowButton.setVisibility(View.VISIBLE);
    }
    
    public void clear(){
    	mDigits.getText().clear();
    }
}
