/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pj.jdbc.core;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库会话接口
 * @author PENGJU
 */
public interface Session extends JDBCOperations{
    
    /**
     * 开始事务
     * @throws SQLException 
     */
    public void beginTransaction() throws SQLException;
    /**
     * 回滚
     * @throws SQLException 
     */
    public void rollback() throws SQLException;
    /**
     * 提交更改
     * @throws SQLException 
     */
    public void commit() throws SQLException;
    /**
     * 停止事务
     * @throws SQLException 
     */
    public void endTransaction() throws SQLException;
    /**
     * 返回当前会话的连接对象
     * @return 
     */
    public Connection getConnection();
    /**
     * 关闭会话
     * @throws SQLException 
     */
    public void close() throws SQLException;
}
