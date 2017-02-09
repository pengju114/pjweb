/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.jdbc.core;

import com.pj.json.JSONObject;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;

/**
 * 结果行对象
 * @author PENGJU
 * 时间:2012-7-14 10:08:33
 */
public interface ResultRow extends Serializable {
/**
     * 
     * @return 取当前行下标,从0开始
     */
    public int getRow();

    /**
     *
     * @param index 从0开始
     * @return 指定列的值
     */
    public boolean getBoolean(int index);

    /**
     *
     * @param index 从0开始
     * @return 指定列的值
     */
    public byte getByte(int index);

    /**
     *
     * @param index 从0开始
     * @return 指定列的值
     */
    public Date getDate(int index);

    /**
     *
     * @param index 从0开始
     * @return 指定列的值
     */
    public Time getTime(int index);

    /**
     *
     * @param index 从0开始
     * @return 指定列的值
     */
    public Timestamp getTimestamp(int index);

    /**
     *
     * @param index 从0开始
     * @return 指定列的值
     */
    public long getLong(int index);

    /**
     *
     * @param index 从0开始
     * @return 指定列的值
     */
    public double getDouble(int index);

    /**
     *
     * @param index 从0开始
     * @return 指定列的值
     */
    public float getFloat(int index);

    /**
     *
     * @param index 从0开始
     * @return 指定列的值
     */
    public int getInt(int index);

    /**
     *
     * @param index 从0开始
     * @return 指定列的值
     */
    public Object getObject(int index);

    /**
     *
     * @param index 从0开始
     * @return 指定列的值
     */
    public String getString(int index);


    /**
     * 取指定列name的值
     * @param name 列名,区分大小写
     * @return 指定列的值
     */
    public boolean getBoolean(String name);

    /**
     * 取指定列name的值
     * @param name 列名,区分大小写
     * @return 指定列的值
     */
    public byte getByte(String name);

    /**
     * 取指定列name的值
     * @param name 列名,区分大小写
     * @return 指定列的值
     */
    public Date getDate(String name);

    /**
     * 取指定列name的值
     * @param name 列名,区分大小写
     * @return 指定列的值
     */
    public Time getTime(String name);

    /**
     * 取指定列name的值
     * @param name 列名,区分大小写
     * @return 指定列的值
     */
    public Timestamp getTimestamp(String name);

    /**
     * 取指定列name的值
     * @param name 列名,区分大小写
     * @return 指定列的值
     */
    public long getLong(String name);

    /**
     * 取指定列name的值
     * @param name 列名,区分大小写
     * @return 指定列的值
     */
    public double getDouble(String name);

    /**
     * 取指定列name的值
     * @param name 列名,区分大小写
     * @return 指定列的值
     */
    public float getFloat(String name);

    /**
     * 取指定列name的值
     * @param name 列名,区分大小写
     * @return 指定列的值
     */
    public int getInt(String name);

    /**
     * 取指定列name的值
     * @param name 列名,区分大小写
     * @return 指定列的值
     */
    public Object getObject(String name);

    /**
     * 取指定列name的值
     * @param name 列名,区分大小写
     * @return 指定列的值
     */
    public String getString(String name);
    
    /**
     * 存入一个数据
     * @param key
     * @param value 
     */
    public void putData(String key,Object value);
    
    public String[] keys();
    
    public JSONObject toJSONObject();
    public Map<String,Object> toMap();
}
