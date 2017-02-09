/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.jdbc.core;

import com.pj.jdbc.annotation.Column;
import com.pj.jdbc.annotation.Table;
import com.pj.utilities.ArrayUtility;
import com.pj.web.res.Config;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author PENGJU
 * 时间:2012-7-14 11:06:45
 */
class SessionImpl implements Session{
    private final Connection connection;
    private final boolean logenable;
    private static Logger logger=Logger.getLogger(SessionImpl.class);
    
    private static final QueryFilter DEFAULT_FILTER= new QueryFilter<ResultRow>(){
        @Override
        public ResultRow filterEach(int index, ResultList<ResultRow> resultList, ResultSet rs) throws SQLException{
            ResultRowImpl row=new ResultRowImpl(resultList);
            for(int i=0;i<resultList.getColumnCount();i++){
                String name=resultList.getColumnName(i);
                row.put(name, rs.getObject(name));
            }
            return row;
        }
    };
    
    public SessionImpl(Connection connection){
        this.connection=connection;
        this.logenable= Config.getConfig(Config.Key.DEBUG, false);
    }
    
    private static void setParameterValue(PreparedStatement ps,int index,Object val) throws SQLException{
        ps.setObject(index, val);
    }
    
    private void log(String str){
        if (logenable) {
            logger.info("执行SQL："+str);
        }
    }

    @Override
    public int executeBatch(String sql, List<Object[]> values) throws SQLException {
        PreparedStatement ps=connection.prepareStatement(sql);
        for (Object[] objects : values) {
            for (int i = 0; i < objects.length; i++) {
                setParameterValue(ps, i+1, objects[i]);
            }
            ps.addBatch();
        }
        log(sql);
        int[] r=ps.executeBatch();
        ps.close();
        
        int c=0;
        for (int i = 0; i < r.length; i++) {
            c+=r[i];
        }
        return c;
    }

    @Override
    public int executeUpdate(String sql, Object[] paramVals) throws SQLException {
        PreparedStatement ps=connection.prepareStatement(sql);
        if (paramVals!=null) {
            for (int i = 0; i < paramVals.length; i++) {
                Object object = paramVals[i];
                setParameterValue(ps, i+1, object);
            }
        }
        log(sql);
        int c=ps.executeUpdate();
        ps.close();
        return c;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return executeUpdate(sql, null);
    }

    @Override
    public ResultList<ResultRow> executeQuery(String sql, Object[] paramVals) throws SQLException {
        return executeQuery(sql, paramVals, DEFAULT_FILTER);
    }

    @Override
    public ResultList<ResultRow> executeQuery(String sql) throws SQLException {
        return executeQuery(sql, DEFAULT_FILTER);
    }

    @Override
    public ResultList<ResultRow> executeQuery(String sql, Object[] paramVals, int pageNumber, int pageSize) throws SQLException {
        return executeQuery(sql, paramVals, pageNumber, pageSize, DEFAULT_FILTER);
    }

    @Override
    public <T> ResultList<T> executeQuery(String sql, Object[] paramVals, int pageNumber, int pageSize, QueryFilter<T> filter) throws SQLException {
        log(sql);
        return query(connection, sql, paramVals, pageNumber, pageSize, filter);
    }
    
    private static <T> ResultList<T> query(Connection conn,String sql, Object[] paramVals, int pageNumber, int pageSize, QueryFilter<T> filter) throws SQLException{
        PreparedStatement ps=conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        if (paramVals!=null) {
            for (int i = 0; i < paramVals.length; i++) {
                Object object = paramVals[i];
                setParameterValue(ps, i+1, object);
            }
        }
        
        ResultSet r=ps.executeQuery();
        ResultSetMetaData metaData=r.getMetaData();
        ResultListImpl<T> rslist=new ResultListImpl<T>();
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            String n=metaData.getColumnName(i+1);
            rslist.addColumnName(n);
        }
        
        int count=0;
        int startRow = calculateStartRow(pageNumber, pageSize);
        
        if (r.last()) {
            rslist.setTotalRowsCount(r.getRow());
        }
        if (startRow < 1) {
            r.beforeFirst();
        }
        else if(startRow < rslist.getTotalRowsCount()){
            r.absolute(startRow); // the first row is 1
        }
        
        while (count < pageSize && r.next()) {
            T row=filter.filterEach(count,rslist, r);
            if (row!=null) {
                rslist.addRow(row);
            }
            count++;
        }
        
        r.close();
        ps.close();
        return rslist;
    }

    @Override
    public <T> ResultList<T> executeQuery(String sql, Object[] paramVals, QueryFilter<T> filter) throws SQLException {
        return executeQuery(sql, paramVals, 1, Integer.MAX_VALUE, filter);
    }

    @Override
    public <T> ResultList<T> executeQuery(String sql, QueryFilter<T> filter) throws SQLException {
        return executeQuery(sql, null, filter);
    }

    @Override
    public void beginTransaction()  throws SQLException{
        connection.setAutoCommit(false);
    }

    @Override
    public void rollback()  throws SQLException{
        connection.rollback();
    }

    @Override
    public void commit()  throws SQLException{
        connection.commit();
    }

    @Override
    public void endTransaction() throws SQLException {
        connection.setAutoCommit(true);
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    @Override
    public int save(String table, ResultRow row) throws SQLException {
        if (row==null) {
            return 0;
        }
        String[] names=row.keys();
        Object[] vals=new Object[names.length];
        StringBuilder sql=new StringBuilder();
        sql.append("insert into ").append(table).append('(');
        
        for (int i = 0; i < names.length; i++) {
            String n = names[i];
            vals[i]=row.getObject(n);
            sql.append(n).append(',');
        }
        if (names.length>0) {
            sql.deleteCharAt(sql.length()-1);
        }
        
        sql.append(") values(").append(ArrayUtility.join("?", names.length, ",")).append(')');
        
        return executeUpdate(sql.toString(), vals);
    }

    @Override
    public int save(Object object) throws SQLException {
        if (object==null) {
            return 0;
        }
        Class c=object.getClass();
        String table=c.getSimpleName();
        Table t= (Table) c.getAnnotation(Table.class);
        if (t!=null) {
            table=t.name();
        }
        
        Field[] fs=c.getDeclaredFields();
        StringBuilder sql=new StringBuilder();
        sql.append("insert into ").append(table).append('(');
        ArrayList<Object> vals=new ArrayList<Object>();
        
        for (int i = 0; i < fs.length; i++) {
            Field field = fs[i];
            field.setAccessible(true);
            
            Column cName=field.getAnnotation(Column.class);
            String name=field.getName();
            if (cName!=null) {
                if (!cName.save()) {
                    continue;
                }
                name=cName.name();
            }
            try {
                vals.add(field.get(object));
                sql.append(name).append(',');
            } catch (Exception ex) {}
        }
        
        if (vals.size()>0) {
            sql.deleteCharAt(sql.length()-1);
        }
        sql.append(") values(").append(ArrayUtility.join("?", vals.size(), ",")).append(')');
        
        return executeUpdate(sql.toString(), vals.toArray());
    }

    @Override
    public <T> ResultList<T> executeQuery(String sql, Object[] paramVals, int pageNumber, int pageSize, Class<T> clazz) throws SQLException {
        ObjectQueryFilter<T> filter=new ObjectQueryFilter<T>(clazz);
        return executeQuery(sql, paramVals, pageNumber, pageSize, filter);
    }

    @Override
    public <T> ResultList<T> executeQuery(String sql, Object[] paramVals, Class<T> clazz) throws SQLException {
        ObjectQueryFilter<T> filter=new ObjectQueryFilter<T>(clazz);
        return executeQuery(sql, paramVals, filter);
    }

    @Override
    public <T> ResultList<T> executeQuery(String sql, Class<T> clazz) throws SQLException {
        ObjectQueryFilter<T> filter=new ObjectQueryFilter<T>(clazz);
        return executeQuery(sql, filter);
    }

    @Override
    public <T> T querySingle(String sql, Class<T> clazz)  throws SQLException {
        return querySingle(sql, null, clazz);
    }

    @Override
    public <T> T querySingle(String sql, Object[] paramVals, Class<T> clazz)  throws SQLException {
        ResultList<T> rs=executeQuery(sql, paramVals, 1, 1, clazz);
        if (rs.getSize()>0) {
            return rs.get(0);
        }
        return null;
    }

    @Override
    public ResultRow querySingle(String sql)  throws SQLException {
        return querySingle(sql, new Object[0]);
    }

    @Override
    public ResultRow querySingle(String sql, Object[] paramVals)  throws SQLException {
        ResultList<ResultRow> rs=executeQuery(sql, paramVals, 1, 1);
        if (rs.getSize()>0) {
            return rs.get(0);
        }
        return null;
    }

    public ResultList<ResultRow> list(String table) throws SQLException{
        return executeQuery("select * from "+table);
    }

    public ResultList<ResultRow> list(String table, int pageNumber, int pageSize) throws SQLException {
        return executeQuery("select * from "+table, null, pageNumber, pageSize);
    }

    public <T> ResultList<T> list(Class<T> clazz) throws SQLException {
        if (clazz == null) {
            throw new SQLException("Illegal argument exception. class should not be null.");
        }
        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null || annotation.name() == null) {
            throw new SQLException("the clazz require a Table Annotation.");
        }
        
        return executeQuery("select * from "+annotation.name(), clazz);
    }

    public <T> ResultList<T> list(Class<T> clazz, int pageNumber, int pageSize) throws SQLException {
        if (clazz == null) {
            throw new SQLException("Illegal argument exception. class should not be null.");
        }
        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null || annotation.name() == null) {
            throw new SQLException("the clazz require a Table Annotation.");
        }
        
        return executeQuery("select * from "+annotation.name(), null, pageNumber, pageSize, clazz);
    }
    
    public static int calculateStartRow(int pageNumber, int pageSize){
        int startRow = (pageNumber - 1) * pageSize;
        return startRow;
    }
}
