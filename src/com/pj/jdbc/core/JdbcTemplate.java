/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.jdbc.core;

import java.util.List;
import org.apache.log4j.Logger;

/**
 * 数据库操作模板,对更新操作开启事务管理
 * @author PENGJU
 * email:pengju114@163.com
 * 时间:2012-7-15 21:42:41
 */
public class JdbcTemplate implements JDBCOperations{
    private String id;
    private static Logger logger=Logger.getLogger(JdbcTemplate.class);
    
    public JdbcTemplate(){
        this(SessionFactory.DEFAULT_SESSION);
    }
    public JdbcTemplate(String id){
        this.id=id;
    }
    
    private static void log(String err,Throwable e){
        logger.error(err, e);
    }
    
    private void closeSession(Session s){
        if (s!=null) {
            try {
                s.close();
            } catch (Exception e) {
                log(e.getMessage(), e);
            }
        }
    }
    
    /**
     * 批量执行 sql 脚本
     * @param sql 可带参数的SQL语句
     * @param values 参数值列表
     * @return 影响行数
     */
    @Override
    public int executeBatch(String sql,List<Object[]> values){
        int c=0;
        Session session=getSession();
        if (session!=null) {
            try {
                session.beginTransaction();
                c=session.executeBatch(sql, values);
                session.commit();
            } catch (Exception e) {
                log(e.getMessage(), e);
                try {
                    session.rollback();
                } catch (Exception ex) {
                }
            } finally{
                try {
                    session.endTransaction();
                } catch (Exception e) {
                }
                closeSession(session);
            }
        }
        
        return c;
    }
    
    /**
     * 执行更新操作
     * @param sql
     * @param paramVals 参数值
     * @return
     
     */
    @Override
    public int executeUpdate(String sql,Object[] paramVals){
        int c=0;
        Session session=getSession();
        if (session!=null) {
            try {
                session.beginTransaction();
                c=session.executeUpdate(sql, paramVals);
                session.commit();
            } catch (Exception e) {
                try {
                    session.rollback();
                } catch (Exception ex) {
                }
                log(e.getMessage(), e);
            } finally{
                try {
                    session.endTransaction();
                } catch (Exception e) {
                }
                closeSession(session);
            }
        }
        
        return c;
        
    }
    /**
     * 执行更新
     * @param sql
     * @return
     
     */
    @Override
    public int executeUpdate(String sql){
        int c=0;
        Session session=getSession();
        if (session!=null) {
            try {
                session.beginTransaction();
                c=session.executeUpdate(sql);
                session.commit();
            } catch (Exception e) {
                try {
                    session.rollback();
                } catch (Exception ex) {
                }
                log(e.getMessage(), e);
            } finally{
                try {
                    session.endTransaction();
                } catch (Exception ex) {
                }
                closeSession(session);
            }
        }
        
        return c;
    }
    /**
     * 按参数执行查询
     * @param sql
     * @param paramVals
     * @return
     
     */
    @Override
    public ResultList<ResultRow> executeQuery(String sql,Object[] paramVals){
        ResultList<ResultRow> rs=null;
        Session session=getSession();
        if (session!=null) {
            try {
                rs=session.executeQuery(sql, paramVals);
            } catch (Exception e) {
                log(e.getMessage(), e);
            } finally{
                closeSession(session);
            }
        }
        return rs;
    }
    /**
     * 执行查询
     * @param sql
     * @return
     
     */
    @Override
    public ResultList<ResultRow> executeQuery(String sql){
        ResultList<ResultRow> rs=null; 
        Session session=getSession();
        if (session!=null) {
            try {
                rs=session.executeQuery(sql);
            } catch (Exception e) {
                log(e.getMessage(), e);
            } finally{
                closeSession(session);
            }
        }
        
        return rs;
    }
    /**
     * 按参数执行有限数据行查询
     * @param sql
     * @param paramVals
     * @param pageNumber 起始页数,第一页是1,第二页是2,以此类推...
     * @param pageSize   每页记录数,也即需要的结果行数.
     * @return 结果列表
     
     */
    @Override
    public ResultList<ResultRow> executeQuery(String sql,Object[] paramVals,int pageNumber,int pageSize) {
        ResultList<ResultRow> rs=null;
        Session session=getSession();
        if (session!=null) {
            try {
                rs=session.executeQuery(sql, paramVals, pageNumber, pageSize);
            } catch (Exception e) {
                log(e.getMessage(), e);
            } finally{
                closeSession(session);
            }
        }
        
        return rs;
    }

    /**
     * 按参数执行有限数据行查询,并将每一行结果行交给filter包装
     * @param sql
     * @param paramVals
     * @param pageNumber 起始页数,第一页是1,第二页是2,以此类推...
     * @param pageSize   每页记录数,也即需要的结果行数.
     * @param filter 结果集过滤器,可以在这里对记录行进行包装,返回包装的对象即可
     * @return
     
     */
    @Override
    public <T> ResultList<T> executeQuery(String sql,Object[] paramVals,int pageNumber,int pageSize,QueryFilter<T> filter){
        ResultList<T> rs=null;
        Session session=getSession();
        if (session!=null) {
            try {
                rs=session.executeQuery(sql, paramVals, pageNumber, pageSize, filter);
            } catch (Exception e) {
                log(e.getMessage(), e);
            } finally{
                closeSession(session);
            }
        }
        
        return rs;
    }
    /**
     * 按参数执行数据查询,并将每一行结果行交给filter包装
     * @param <T> 动态类型
     * @param sql
     * @param paramVals
     * @param filter 结果集过滤器,可以在这里对记录行进行包装,返回包装的对象即可
     * @return
     
     */
    @Override
    public <T> ResultList<T> executeQuery(String sql,Object[] paramVals,QueryFilter<T> filter){
        ResultList<T> rs=null;
        Session session=getSession();
        if (session!=null) {
            try {
                rs=session.executeQuery(sql, paramVals, filter);
            } catch (Exception e) {
                log(e.getMessage(), e);
            } finally{
                closeSession(session);
            }
        }
        
        return rs;
    }
    /**
     * 按参查询,并将每一行结果行交给filter包装
     * @param <T> 动态类型
     * @param sql
     * @param filter 结果集过滤器,可以在这里对记录行进行包装,返回包装的对象即可
     * @return
     
     */
    @Override
    public <T> ResultList<T> executeQuery(String sql,QueryFilter<T> filter) {
        ResultList<T> rs=null;
        Session session=getSession();
        if (session!=null) {
            try {
                rs=session.executeQuery(sql, filter);
            } catch (Exception e) {
                log(e.getMessage(), e);
            } finally{
                closeSession(session);
            }
        }
        
        return rs;
    }
    /**
     * 返回会话对象
     * @return 
     */
    public Session getSession(){
        try {
            return SessionFactory.getSession(id);
        } catch (Exception e) {
            log(e.getMessage(), e);
        }
        return  null;
    }

    /**
     * 保存一行数据
     * 数据的类型必须要和数据库一一对应
     * @param table 表名
     * @param row 数据行
     * @return
     */
    @Override
    public int save(String table,ResultRow row){
        int c=0;
        Session session=getSession();
        if (session!=null) {
            try {
                session.beginTransaction();
                c=session.save(table, row);
                session.commit();
            } catch (Exception e) {
                try {
                    session.rollback();
                } catch (Exception ex) {
                }
                log(e.getMessage(), e);
            } finally{
                try {
                    session.endTransaction();
                } catch (Exception ex) {
                }
                closeSession(session);
            }
        }
        
        return c;
    }
    /**
     * 保存一个对象,必须用@Table对类注解,并且对每一个需要存到数据库的实例变量也要用@Column注解
     * 在此只能保存自己的属性,不能保存继承来的属性
     * 如果没有对类注解则用类名做表名,没有注解的实例变量将不存到数据库中
     * 变量的类型必须和数据库中的类型一一对应
     * @param object
     * @return
     
     */
    @Override
    public int save(Object object){
        int c=0;
        Session session=getSession();
        if (session!=null) {
            try {
                session.beginTransaction();
                c=session.save(object);
                session.commit();
            } catch (Exception e) {
                try {
                    session.rollback();
                } catch (Exception ex) {
                }
                log(e.getMessage(), e);
            } finally{
                try {
                    session.endTransaction();
                } catch (Exception ex) {
                }
                closeSession(session);
            }
        }
        
        return c;
    }
    /**
     * 查询数据库,并将结果行转换为指定的clazz对象
     * @param <T> 目标类型
     * @param sql sql语句
     * @param paramVals 参数值
     * @param pageNumber 起始页数,第一页是1,第二页是2,以此类推...
     * @param pageSize   每页记录数,也即需要的结果行数.
     * @param clazz 类型
     * @return
     
     */
    @Override
    public <T> ResultList<T> executeQuery(String sql,Object[] paramVals,int pageNumber,int pageSize,Class<T> clazz){
        ResultList<T> rs=null;
        Session session=getSession();
        if (session!=null) {
            try {
                rs=session.executeQuery(sql, paramVals, pageNumber, pageSize, clazz);
            } catch (Exception e) {
                log(e.getMessage(), e);
            } finally{
                closeSession(session);
            }
        }
       
        return rs;
    }
    /**
     * 查询数据库,并将结果行转换为指定的clazz对象
     * @param <T> 目标类型
     * @param sql sql语句
     * @param paramVals 参数值
     * @param clazz 类型
     * @return
     
     */
    @Override
    public <T> ResultList<T> executeQuery(String sql,Object[] paramVals,Class<T> clazz) {
        ResultList<T> rs=null;
        Session session=getSession();
        if (session!=null) {
            try {
                rs=session.executeQuery(sql, paramVals, clazz);
            } catch (Exception e) {
                log(e.getMessage(), e);
            } finally{
                closeSession(session);
            }
        }
        
        return rs;
    }
    /**
     * 查询数据库,并将结果行转换为指定的clazz对象
     * @param <T> 目标类型
     * @param sql sql语句
     * @param clazz 类型
     * @return
     
     */
    @Override
    public <T> ResultList<T> executeQuery(String sql,Class<T> clazz){
        ResultList<T> rs=null;
        Session session=getSession();
        if (session!=null) {
            try {
                rs=session.executeQuery(sql, clazz);
            } catch (Exception e) {
                log(e.getMessage(), e);
            } finally{
                closeSession(session);
            }
        }
        
        return rs;
    }
    
    
    /**
     * 执行查询，并返回一个结果(即使结果有多个)，无结果返回null
     * @param <T> 指定的返回类型
     * @param sql SQL语句
     * @param clazz 指定的类
     * @return T
     
     */
    @Override
    public <T> T querySingle(String sql,Class<T> clazz) {
        return querySingle(sql, null, clazz);
    }
    /**
     * 执行查询，并返回一个结果(即使结果有多个)，无结果返回null
     * @param <T> 指定的返回类型
     * @param sql SQL语句,参数用问号表示
     * @param paramVals 参数值
     * @param clazz 指定的类
     * @return T
     
     */
    @Override
    public <T> T querySingle(String sql,Object[] paramVals,Class<T> clazz) {
        T val=null;
        Session session=getSession();
        if (session!=null) {
            try {
                val=session.querySingle(sql, paramVals, clazz);
            } catch (Exception e) {
                log(e.getMessage(), e);
            } finally{
                closeSession(session);
            }
        }
        
        return val;
    }
    /**
     * 执行查询，并返回一个结果(即使结果有多个)，无结果返回null
     * @param sql SQL语句
     * @return ResultRow
     
     */
    @Override
    public ResultRow querySingle(String sql) {
        return querySingle(sql, new Object[0]);
    }
    /**
     * 执行查询，并返回一个结果(即使结果有多个)，无结果返回null
     * @param sql SQL语句,参数用问号表示
     * @param paramVals 参数值
     * @return ResultRow
     
     */
    @Override
    public ResultRow querySingle(String sql,Object[] paramVals) {
        ResultRow val=null;
        Session session=getSession();
        if (session!=null) {
            try {
                val=session.querySingle(sql, paramVals);
            } catch (Exception e) {
                log(e.getMessage(), e);
            } finally{
                closeSession(session);
            }
        }
        
        return val;
    }
    
    @Override
    public ResultList<ResultRow> list(String table){
        ResultList<ResultRow> val=null;
        Session session=getSession();
        if (session!=null) {
            try {
                val=session.list(table);
            } catch (Exception e) {
                log(e.getMessage(), e);
            } finally{
                closeSession(session);
            }
        }
        
        return val;
    }
    
    
    @Override
    public ResultList<ResultRow> list(String table, int pageNumber,int pageSize) {
        ResultList<ResultRow> val=null;
        Session session=getSession();
        if (session!=null) {
            try {
                val=session.list(table, pageNumber, pageSize);
            } catch (Exception e) {
                log(e.getMessage(), e);
            } finally{
                closeSession(session);
            }
        }
        
        return val;
    }
    
    @Override
    public <T> ResultList<T> list(Class<T> clazz) {
        ResultList<T> val=null;
        Session session=getSession();
        if (session!=null) {
            try {
                val=session.list(clazz);
            } catch (Exception e) {
                log(e.getMessage(), e);
            } finally{
                closeSession(session);
            }
        }
        
        return val;
    }

    @Override
    public <T> ResultList<T> list(Class<T> clazz, int pageNumber,int pageSize) {
        ResultList<T> val=null;
        Session session=getSession();
        if (session!=null) {
            try {
                val=session.list(clazz, pageNumber, pageSize);
            } catch (Exception e) {
                log(e.getMessage(), e);
            } finally{
                closeSession(session);
            }
        }
        
        return val;
    }
}
