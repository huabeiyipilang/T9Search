
package cn.kli.t9search;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Rect;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * EditText which suppresses IME show up.
 */
public class DigitsEditText extends EditText {
    public DigitsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setInputType(getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        setInputType(InputType.TYPE_NULL);
//        setShowSoftInputOnFocus(false);

    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        final InputMethodManager imm = ((InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE));
        if (imm != null && imm.isActive(this)) {
            imm.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final boolean ret = super.onTouchEvent(event);
        // Must be done after super.onTouchEvent()
        final InputMethodManager imm = ((InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE));
        if (imm != null && imm.isActive(this)) {
            imm.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
        }
        return ret;
    }

    @Override
    public void sendAccessibilityEventUnchecked(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            // Since we're replacing the text every time we add or remove a
            // character, only read the difference. (issue 5337550)
            final int added = event.getAddedCount();
            final int removed = event.getRemovedCount();
            final int length = event.getBeforeText().length();
            if (added > removed) {
                event.setRemovedCount(0);
                event.setAddedCount(1);
                event.setFromIndex(length);
            } else if (removed > added) {
                event.setRemovedCount(1);
                event.setAddedCount(0);
                event.setFromIndex(length - 1);
            } else {
                return;
            }
        } else if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_FOCUSED) {
            // The parent EditText class lets tts read "edit box" when this View has a focus, which
            // confuses users on app launch (issue 5275935).
            return;
        }
        super.sendAccessibilityEventUnchecked(event);
    }
}