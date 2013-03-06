/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.kli.t9search;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.System;
import android.util.Log;

public class HapticFeedback {
    private static final int[] VIBRATION_PATTERN ={0,10,20,30};
    /** If no pattern was found, vibrate for a small amount of time. */
    private static final long DURATION = 10;  // millisec.
    /** Play the haptic pattern only once. */
    private static final int NO_REPEAT = -1;

    private static final String TAG = "HapticFeedback";
    private Context mContext;
    private long[] mHapticPattern;
    private Vibrator mVibrator;

    private boolean mEnabled;
    private Settings.System mSystemSettings;
    private ContentResolver mContentResolver;
    private boolean mSettingEnabled;

    public void init(Context context, boolean enabled) {
    	mContext = context;
        mEnabled = enabled;
        if (enabled) {
        	mVibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
            if (!loadHapticSystemPattern(context.getResources())) {
                mHapticPattern = new long[] {0, DURATION, 2 * DURATION, 3 * DURATION};
            }
            mSystemSettings = new Settings.System();
            mContentResolver = context.getContentResolver();
        }
    }


    /**
     * Reload the system settings to check if the user enabled the
     * haptic feedback.
     */
    public void checkSystemSetting() {
        if (!mEnabled) {
            return;
        }
        try {
            int val = mSystemSettings.getInt(mContentResolver, System.HAPTIC_FEEDBACK_ENABLED, 0);
            mSettingEnabled = val != 0;
        } catch (Resources.NotFoundException nfe) {
            Log.e(TAG, "Could not retrieve system setting.", nfe);
            mSettingEnabled = false;
        }

    }


    /**
     * Generate the haptic feedback vibration. Only one thread can
     * request it. If the phone is already in a middle of an haptic
     * feedback sequence, the request is ignored.
     */
    public void vibrate() {
        if (!mEnabled || !mSettingEnabled) {
            return;
        }
        // System-wide configuration may return different styles of haptic feedback pattern.
        // - an array with one value implies "one-shot vibration"
        // - an array with multiple values implies "pattern vibration"
        // We need to switch methods to call depending on the difference.
        // See also PhoneWindowManager#performHapticFeedbackLw() for another example.
        if (mHapticPattern != null && mHapticPattern.length == 1) {
            mVibrator.vibrate(mHapticPattern[0]);
        } else {
            mVibrator.vibrate(mHapticPattern, NO_REPEAT);
        }
    }

    /**
     * @return true If the system haptic pattern was found.
     */
    private boolean loadHapticSystemPattern(Resources r) {
        int[] pattern;

        mHapticPattern = null;
        try {
            pattern = VIBRATION_PATTERN;
        } catch (Resources.NotFoundException nfe) {
            Log.e(TAG, "Vibrate pattern missing.", nfe);
            return false;
        }

        if (null == pattern || pattern.length == 0) {
            Log.e(TAG, "Haptic pattern is null or empty.");
            return false;
        }

        // int[] to long[] conversion.
        mHapticPattern = new long[pattern.length];
        for (int i = 0; i < pattern.length; i++) {
            mHapticPattern[i] = pattern[i];
        }
        return true;
    }
}
