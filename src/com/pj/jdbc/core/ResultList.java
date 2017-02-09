/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.jdbc.core;

import com.pj.json.JSONArray;
import java.util.List;

/**
 *
 * @author PENGJU
 * 时间:2012-7-14 0:34:59
 */
public interface ResultList<T> extends Iterable<T> {
/**
     * 取指定行
     * @param index 行下标,从0开始
     * @return 指定行的结果对象
     */
    public T get(int index);

    /**
     *
     * @return 列数
     */
    public int getColumnCount();

    /**
     *
     * @return 列名数组
     */
    public String[] getColumnNames();

    /**
     * 取指定列的列名
     * @param index 列下标,从0开始
     * @return 列名
     */
    public String getColumnName(int index);
    
    /**
     * 是否存在列
     * @param name 列名
     * @return 
     */
    public boolean hasColumn(String name);

    /**
     *当前列表中的结果数
     * @return 结果总数
     */
    public int getSize();
    /**
     * 获取查询结果的总记录数,和getSize()不一样,如果是分页数据就不一样,分页数据一般是getSize返回当前页的记录数
     * @return 
     */
    public int getTotalRowsCount();
    public JSONArray toJSONArray();
    public List<T> toList();
}
