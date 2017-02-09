/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.jdbc.core;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *搜索过滤器,过滤查询结果,返回包装了一个记录行的对象,返回null则抛弃该行记录
 * @author PENGJU
 * 时间:2012-7-14 9:59:12
 */
public interface QueryFilter<T> {
    public T filterEach(int index,ResultList<T> resultList ,ResultSet rs) throws SQLException;
}
