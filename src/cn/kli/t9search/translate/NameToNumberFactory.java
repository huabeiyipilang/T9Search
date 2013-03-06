/*
 * Copyright (C) 2011 The CyanogenMod Project
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

package cn.kli.t9search.translate;

import java.util.Locale;

import android.content.Context;


/**
 * @author barami
 * Return a instance of NameToNumber class or inherited classes.
 * 
 * Default instance is NameToNumber class that acts like previous nameToNumber function of T9Search class.
 * This will be added to support complex T9 search. (ex: In a hangul, '臧�倶雼�?will be searched by '銊�, '銊�, '銊便�?, etc.)
 */
public class NameToNumberFactory {
    public static NameToNumber create(Context context, final String t9Chars, final String t9Digits) {
        Locale lc = context.getResources().getConfiguration().locale;

        // Check locale and returns matched class inherited from NameToNumber class.
        NameToNumber instance;
        if (lc.equals(Locale.CHINA)) {
            instance = new NameToNumberChinese(t9Chars, t9Digits);
        } else {
            instance = new NameToNumber(t9Chars, t9Digits);
        }

        return instance;
    }
    
    public static NameToNumber createChineseTranslate(){
    	StringBuilder bT9Chars = new StringBuilder();
		StringBuilder bT9Digits = new StringBuilder();
		String[] chars = {"1","2abc","3def","4ghi","5jkl","6mno","7pqrs","8tuv","9wxyz","0","#","*","+"};
		for(String c:chars){
			bT9Chars.append(c);
			for(int i = 0; i < c.length(); i++){
				bT9Digits.append(c.charAt(0));
			}
		}
		return new NameToNumberChinese(bT9Chars.toString(), bT9Digits.toString());
    }
}
