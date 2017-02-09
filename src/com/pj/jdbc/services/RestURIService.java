/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pj.jdbc.services;

import static com.pj.client.core.resolvers.RestURIResolver.REST_URI_DATABASE_CONFIG_KEY;
import com.pj.client.rest.RestURIBuilderDataSource;
import com.pj.client.rest.RestURIParser;
import com.pj.client.rest.RestURIPart;
import com.pj.client.rest.RestURISQLBuilder;
import com.pj.client.rest.Tuple;
import com.pj.jdbc.core.QueryFilter;
import com.pj.jdbc.core.ResultList;
import com.pj.jdbc.core.ResultRow;
import com.pj.jdbc.core.ResultRowImpl;
import com.pj.jdbc.core.ResultRowMap;
import com.pj.web.res.Config;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author luzhenwen
 */
public class RestURIService extends BaseService{
    private static final Pattern TO_CAMEL_CASE_PATTERN = Pattern.compile("((\\_)(\\w))");
    private static final QueryFilter<ResultRow> TO_CAMEL_CASE_QUERY_FILTER = new QueryFilter<ResultRow>(){
        @Override
        public ResultRow filterEach(int index, ResultList<ResultRow> resultList, ResultSet rs) throws SQLException{
            ResultRowImpl row=new ResultRowImpl(resultList);
            for(int i=0;i<resultList.getColumnCount();i++){
                String name=resultList.getColumnName(i);
                
                StringBuilder string = new StringBuilder(name);
                Matcher matcher = TO_CAMEL_CASE_PATTERN.matcher(string);
                while(matcher.find()){
                    string.replace(matcher.start(), matcher.end(), matcher.group(3).toUpperCase(Locale.ENGLISH));
                    matcher.reset(string);
                }
                row.put(string.toString(), rs.getObject(name));
            }
            return row;
        }
    };
    
    public int update(Tuple<CharSequence,Object> tuple){
        return getJdbcTemplate().executeUpdate(tuple.keyObject.toString(), tuple.valueObject.toArray());
    }
    
    public ResultList<ResultRow> get(Tuple<CharSequence,Object> tuple,int pageNumber, int pageSize){
        return getJdbcTemplate().executeQuery(tuple.keyObject.toString(), tuple.valueObject.toArray(), pageNumber, pageSize, TO_CAMEL_CASE_QUERY_FILTER);
    }
    
    /**
     * 通过返回的tuple.isUpdate判断是更新操作还是获取操作
     * @param restURI
     * @param dataSource
     * @param pageNumber
     * @param pageSize
     * @return tuple<操作影响的行数,结果列表>
     */
    public Tuple<Integer,ResultRow> execute(String restURI, RestURIBuilderDataSource dataSource, int pageNumber, int pageSize) throws Exception{
        List<RestURIPart> parts = RestURIParser.parse(restURI, Config.getConfig(Config.Key.CHARSET, Config.Value.CHARSET));
        RestURISQLBuilder.Database database = RestURISQLBuilder.Database.MySQL;
        try {
            database = RestURISQLBuilder.Database.valueOf(Config.getConfig(REST_URI_DATABASE_CONFIG_KEY, null));
        } catch (Exception e) {
        }
        
        RestURISQLBuilder restURISQLBuilder = new RestURISQLBuilder(database);
        if (dataSource != null) {
            restURISQLBuilder.addDataSource(dataSource);
        }
        
        restURISQLBuilder.append(parts);
        Tuple<CharSequence,Object> tuple = restURISQLBuilder.build();
        Tuple<Integer,ResultRow> result = null;
        if (tuple.isUpdate) {
            result = new Tuple<Integer, ResultRow>(1);
            int c = update(tuple);
            result.isUpdate = tuple.isUpdate;
            result.keyObject = c;
            ResultRowMap row = new ResultRowMap();
            row.setRow(0);
            row.putData("update", result.keyObject);
            result.valueObject.add(row);
        }else{
            int size = 0;
            ResultList<ResultRow> rs = get(tuple, pageNumber, pageSize);
            if (rs != null) {
                size = rs.getSize();
            }
            result = new Tuple<Integer, ResultRow>(size);
            result.keyObject = (size == 0?0:rs.getTotalRowsCount());
            if (rs != null) {
                result.valueObject.addAll(rs.toList());
            }
        }
        
        return result;
    }
    
    /**
     * 通过返回的tuple.isUpdate判断是更新操作还是获取操作
     * @param restURI
     * @param dataSource
     * @return tuple<操作影响的行数,结果列表>
     * @throws Exception 
     */
    public Tuple<Integer,ResultRow> execute(String restURI, RestURIBuilderDataSource dataSource) throws Exception{
        return execute(restURI, dataSource, 1, Integer.MAX_VALUE);
    }
}
