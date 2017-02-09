/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pj.client.core.resolvers;

import com.pj.client.core.ClientException;
import com.pj.client.core.ServiceResolver;
import com.pj.client.core.ServiceResult;
import com.pj.client.rest.RestURIBuilderDataSource;
import com.pj.client.rest.RestURIParser;
import com.pj.client.rest.RestURIPart;
import com.pj.client.rest.RestURISQLBuilder;
import com.pj.client.rest.Tuple;
import com.pj.client.rest.datasource.JSONRestBuilderDataSource;
import com.pj.jdbc.core.ResultList;
import com.pj.jdbc.core.ResultRow;
import com.pj.jdbc.services.RestURIService;
import com.pj.json.JSONException;
import com.pj.utilities.StringUtility;
import com.pj.web.res.Config;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author luzhenwen
 */
public class RestURIResolver extends ServiceResolver{
    public static final String REST_URI_PATH_CONFIG_KEY = "restURIPath";
    public static final String REST_URI_DATABASE_CONFIG_KEY = "database";
    public static final String REST_URI_PATH = "client.service";
    public static final String PARAMS_KEY = "params";
    
    

    @Override
    public ServiceResult execute() throws Exception {
        String uri = getRequest().getRequestURI();
        String path = Config.getConfig(REST_URI_PATH_CONFIG_KEY, REST_URI_PATH);
        int index = uri.indexOf(path);
        if (index != -1) {
            uri = uri.substring(index + path.length());
        }
        Tuple<CharSequence,Object> tuple = executeRestURI(uri, getBuilderDataSource());
        return handleRestResult(tuple);
    }
    
    public Tuple<CharSequence,Object> executeRestURI(String restURI,RestURIBuilderDataSource dataSource) throws Exception{
                
        List<RestURIPart> parts = RestURIParser.parse(restURI, Config.getConfig(Config.Key.CHARSET, Config.Value.CHARSET));
        if (!executeAuthorization(parts)) {
            throw new ClientException(ClientException.UNAUTHORIZED);
        }
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
        
        return tuple;
    }
    
    public ServiceResult handleRestResult(Tuple<CharSequence,Object> tuple) throws Exception{
        ServiceResult result = new ServiceResult();
        RestURIService service = new RestURIService();
        if (tuple.isUpdate) {
            int update = service.update(tuple);
            HashMap<String, Integer> ret = new HashMap<String, Integer>(1);
            ret.put("update", update);
            result.getData().add(ret);
        }else{
            ResultList<ResultRow> resultList = service.get(tuple, getPageNumber(), getPageSize());
            if (resultList == null) {
                throw new ClientException(ClientException.REQUEST_ERROR);
            }
            calculatePageProperties(resultList.getTotalRowsCount(), result);
            result.getData().addAll(resultList.toList());
        }
        return result;
    }
    
    protected boolean executeAuthorization(List<RestURIPart> parts){
        // 默认存在任何写操作都不允许
        for (RestURIPart part : parts) {
            if (!part.getOperation().equalsIgnoreCase(RestURIPart.OPERATION_GET)) {
                return false;
            }
        }
        return true;
    }
    
    protected RestURIBuilderDataSource getBuilderDataSource(){
        String json = getStringParameter(PARAMS_KEY);
        if (!StringUtility.isEmpty(json)) {
            try {
                return new JSONRestBuilderDataSource(json);
            } catch (JSONException ex) {
                Logger.getLogger(RestURIResolver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
