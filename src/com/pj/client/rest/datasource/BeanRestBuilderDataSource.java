/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pj.client.rest.datasource;

import com.pj.client.rest.RestURIBuilderDataSource;
import com.pj.client.rest.RestURIPart;
import com.pj.client.rest.RestURISQLBuilder;
import com.pj.jdbc.annotation.Column;
import com.pj.jdbc.annotation.Table;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author luzhenwen
 */
public class BeanRestBuilderDataSource implements RestURIBuilderDataSource{
    
    private final Map<String,Object> map;
    private final Map<String,String> varMap;
    private final Map<String,String> tableMap;
    
    public BeanRestBuilderDataSource(Object object){
        map = new HashMap<String, Object>(15);
        varMap = new HashMap<String, String>(15);
        tableMap = new HashMap<String, String>(1);
        parseObject(object);
    }

    private void parseObject(Object object) {
        if (object != null) {
            Class clazz = object.getClass();
            boolean filter = false;
            
            Table table = (Table) clazz.getAnnotation(Table.class);
            if (table != null) {
                tableMap.put(clazz.getSimpleName().toLowerCase(), table.name());
                tableMap.put(clazz.getSimpleName(), table.name());
                filter = true;
            }
            Field[] fs = clazz.getDeclaredFields();
            for (Field f : fs) {
                f.setAccessible(true);
                if (filter) {
                    Column column = f.getAnnotation(Column.class);
                    if (column == null) {
                        continue;
                    }
                    String colName = column.name();
                    
                    if (column.save()) {
                        varMap.put(f.getName(), colName);
                        try {
                            map.put(f.getName(), f.get(object));
                        } catch (Exception e) {
                        }
                    }
                }else{
                    try {
                        map.put(f.getName(), f.get(object));
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public String evaluateTarget(RestURISQLBuilder restURISQLBuilder, String target) {
        return tableMap.get(target);
    }

    public String evaluateVarName(RestURISQLBuilder restURISQLBuilder, String varName) {
        return varMap.get(varName);
    }

    public Map<String, Object> getKeysAndObjects(RestURISQLBuilder restURISQLBuilder, RestURIPart part) {
        return map;
    }
}
