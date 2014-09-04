package com.carl.t9search.utils;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.text.TextUtils;

public class PinYinUtils {
    private static HanyuPinyinOutputFormat sPinYinFormat = new HanyuPinyinOutputFormat();
    
    static{
        sPinYinFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        sPinYinFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }
    
    public static List<String> string2PinYin(String str) {
        //存储所以字的拼音
        List<List<String>> pinyinStrings = new ArrayList<List<String>>();
        
        for (int i = 0; i < str.length(); ++i) {
            //单个字的拼音
            String[] pinyins = getCharacterPinYin(str.charAt(i));
            List<String> pinyinList = new ArrayList<String>();
            for(String pinyin : pinyins){
                if(!pinyinList.contains(pinyin)){
                    pinyinList.add(pinyin);
                }
            }
            pinyinStrings.add(pinyinList);
        }
        
        List<String> allStrings = getAllCollections(pinyinStrings, null, 0);
        
        return allStrings;
    }
    
    private static List<String> getAllCollections(List<List<String>> origin, List<String> resList, int n){
        if(n >= origin.size()){
            return resList;
        }
        
        if(resList == null || resList.size() == 0){
            resList = new ArrayList<String>();
            resList.add("");
        }
        
        List<String> newResList = new ArrayList<String>();
        
        for(String str : resList){
            for(String c : origin.get(n)){
                newResList.add(str+c);
            }
        }
        
        return getAllCollections(origin, newResList, ++n);
    }
    
    private static String[] getCharacterPinYin(char c) {
        String[] pinyins = null;
        try {
            pinyins = PinyinHelper.toHanyuPinyinStringArray(c, sPinYinFormat);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }

        if (pinyins == null || pinyins.length == 0) {
            pinyins = new String[1];
            pinyins[0] = String.valueOf(c);
        }
        return pinyins;
    }
    
    public static String pinyin2T9(String pinyin) {
        pinyin = pinyin.toLowerCase();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pinyin.length(); ++i){
            char c = pinyin.charAt(i);
            if(c == ',' || (c >= '0' && c <= '9')){
                sb.append(c);
            }else{
                String t9 = getT9ByChar(c);
                if(!TextUtils.isEmpty(t9)){
                    sb.append(t9);
                }
            }
        }
        return sb.toString();
    }
    
    private static String getT9ByChar(char c){
        if(c >= 'a' && c <= 'c'){
            return "2";
        }else if(c >= 'd' && c <= 'f'){
            return "3";
        }else if(c >= 'g' && c <= 'i'){
            return "4";
        }else if(c >= 'j' && c <= 'l'){
            return "5";
        }else if(c >= 'm' && c <= 'o'){
            return "6";
        }else if(c >= 'p' && c <= 's'){
            return "7";
        }else if(c >= 't' && c <= 'v'){
            return "8";
        }else if(c >= 'w' && c <= 'z'){
            return "9";
        }else{
            return null;
        }
    }
}
