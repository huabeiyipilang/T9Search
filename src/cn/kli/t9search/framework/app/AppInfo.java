package cn.kli.t9search.framework.app;

import java.util.List;

import cn.kli.t9search.utils.PinYinUtils;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.AutoIncrementPrimaryKey;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Table;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

@Table("app_info")
public class AppInfo extends Model {
    @AutoIncrementPrimaryKey
    @Column("id")
    public long id;
    
    @Column("title")
    public String title;
    
    @Column("icon")
    public Bitmap icon;
    
    @Column("package_name")
    public String packageName;
    
    @Column("intent")
    public Intent intent;
    
    @Column("count")
    public long count;
    
    @Column("keyword_quanpin")
    public String keyword_quanpin;
    
    @Column("keyword_quanpin_t9")
    public String keyword_quanpin_t9;

    @Column("keyword_shouzimu")
    public String keyword_shouzimu;

    @Column("keyword_shouzimu_t9")
    public String keyword_shouzimu_t9;
    
    
    @Column("installed")
    public boolean installed = true;

    public boolean equals(AppInfo appInfo) {
        return intent!= null && intent.equals(intent) 
                && !TextUtils.isEmpty(packageName) && packageName.equals(appInfo.packageName);
    }
    
    public void setQuanpin(List<String> pinyins){
        StringBuilder sb = new StringBuilder();
        for(String pinyin : pinyins){
            sb.append(pinyin);
            sb.append(",");
        }
        keyword_quanpin = sb.toString();
        keyword_quanpin_t9 = PinYinUtils.pinyin2T9(keyword_quanpin);
    }
    
    public void merge(AppInfo appInfo){
        if(appInfo == null){
            return;
        }
        title = appInfo.title;
        icon = appInfo.icon;
        keyword_quanpin = appInfo.keyword_quanpin;
        keyword_shouzimu = appInfo.keyword_shouzimu;
        keyword_quanpin_t9 = appInfo.keyword_quanpin_t9;
        keyword_shouzimu_t9 = appInfo.keyword_shouzimu_t9;
        installed = appInfo.installed;
    }
}
