package com.carl.t9search.utils;

import java.util.Collection;
import java.util.List;

import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.ModelList;
import se.emilsjolander.sprinkles.Query;
import se.emilsjolander.sprinkles.QueryResult;


public class DbUtils {

    /**
     * 清空表数据
     * @Title: clearDb
     * @param pClass
     * @return void
     * @date 2014-6-12 上午10:56:49
     */
    public static void clearDb(Class<? extends Model> pClass){
        CursorList<? extends Model> list = Query.all(pClass).get();
        ModelList.from(list).deleteAll();
        list.close();
    }
    
    /**
     * 删除list中的数据
     * @Title: deleteList
     * @param list
     * @return void
     * @date 2014-6-12 上午11:04:41
     */
    public static <T extends Model> void deleteList(List<T> list){
        ModelList<T> mList = new ModelList<T>(list);
        mList.deleteAll();
    }
    
    /**
     * 查询表数据
     * @Title: getAllData
     * @param clazz
     * @return
     * @return List<T>
     * @date 2014-6-12 上午10:56:40
     */
    public static <T extends Model> List<T> getAllData(Class<T> clazz){
        CursorList cursorList = Query.all(clazz).get();
        List<T> list = cursorList.asList();
        cursorList.close();
        return list;
    }
    
    /**
     * 获取表中所有数据
     * @Title: getDataList
     * @param clazz
     * @param sql
     * @param sqlArgs
     * @return
     * @return List<T>
     * @date 2014-6-12 上午10:55:56
     */
    @SuppressWarnings("unchecked")
    public static <T extends QueryResult> List<T> getDataList(Class<T> clazz, String sql, Object... sqlArgs){
        @SuppressWarnings("rawtypes")
        CursorList cursorList = Query.many(clazz, sql, sqlArgs).get();
        List<T> list = cursorList.asList();
        cursorList.close();
        return list;
    }
    
    /**
     * 保存数据集合
     * @Title: saveAll
     * @param list
     * @return
     * @return boolean
     * @date 2014-6-22 上午10:21:11
     */
    public static boolean saveAll(Collection<? extends Model> collection){
        ModelList<Model> modelList = new ModelList<Model>(collection);
        return modelList.saveAll();
    }
}
