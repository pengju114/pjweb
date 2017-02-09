/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pj.client.rest.datasource;

import com.pj.client.rest.RestURIBuilderDataSource;
import com.pj.client.rest.RestURIPart;
import com.pj.client.rest.RestURISQLBuilder;
import com.pj.json.JSONException;
import com.pj.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author luzhenwen
 */
public class JSONRestBuilderDataSource implements RestURIBuilderDataSource{
    public Map<String,Object> objects;
    
    public JSONRestBuilderDataSource(String jsonString)throws JSONException{
        this(new JSONObject(jsonString));
    }
    
    public JSONRestBuilderDataSource(JSONObject object) throws JSONException{
        objects = new HashMap<String, Object>(object.length());
        Iterator<String> keyStrings = object.keys();
        while (keyStrings.hasNext()) {
            String next = keyStrings.next();
            objects.put(next, object.get(next));
        }
    }
    
    public JSONRestBuilderDataSource(Map<String,Object> map){
        objects = map;
    }

    public String evaluateTarget(RestURISQLBuilder restURISQLBuilder, String target) {
        return null;
    }

    public String evaluateVarName(RestURISQLBuilder restURISQLBuilder, String varName) {
        return null;
    }

    public Map<String, Object> getKeysAndObjects(RestURISQLBuilder restURISQLBuilder, RestURIPart part) {
        return objects;
    }
    
}
