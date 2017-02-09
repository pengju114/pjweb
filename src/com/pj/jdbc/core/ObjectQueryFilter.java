/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.jdbc.core;

import com.pj.jdbc.annotation.Column;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author PENGJU
 * email:pengju114@163.com
 * 时间:2012-7-15 17:02:15
 */
class ObjectQueryFilter<T> implements QueryFilter<T> {
    private  static final Logger logger=Logger.getLogger(ObjectQueryFilter.class);
    private Class<T> clazz;
    
    private HashMap<String,Field> nameMap=new HashMap<String, Field>();
    public ObjectQueryFilter(Class<T> c){
        clazz=c;
        Field[] fs=clazz.getDeclaredFields();
        for (Field field : fs) {
            
            String columnName=field.getName();
            Column col=field.getAnnotation(Column.class);
            if (col!=null) {
                columnName=col.name();
            }
            field.setAccessible(true);
            nameMap.put(columnName, field);
        }
    }

    @Override
    public T filterEach(int index, ResultList<T> resultList, ResultSet rs) throws SQLException {
        T target=null;
        try {
            target=clazz.newInstance();
        } catch (Exception e) {
        }
        if (target!=null) {
            for (Map.Entry<String, Field> entry : nameMap.entrySet()) {
                String string = entry.getKey();
                Field field = entry.getValue();
                
                if (!resultList.hasColumn(string)) {
                    continue;
                }
                
                Object val=null;
                try {
                    val=rs.getObject(string);
                } catch (Exception e) {
                    logger.error(string, e);
                }
                if (val!=null) {
                    try {
                        field.set(target, val);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        try {
							String md = field.getName();
							md = "set" + md.substring(0, 1).toUpperCase() + md.substring(1);
							Class<?> type = field.getType();
							if(!val.getClass().isAssignableFrom(type)){
								val = type.cast(val);
							}
							Method method = clazz.getMethod(md, field.getType());
							method.invoke(target, val);
						} catch (Exception e2) {
						}
                    }
                }
            }
        }
        return target;
    }
    
    @Override
    protected void finalize() throws Throwable{
        nameMap.clear();
        super.finalize();
    }
}
