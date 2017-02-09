/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pj.client.rest.test;

import com.pj.client.rest.RestURIBuilderDataSource;
import com.pj.client.rest.RestURIParser;
import com.pj.client.rest.RestURIPart;
import com.pj.client.rest.RestURISQLBuilder;
import com.pj.client.rest.Tuple;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author luzhenwen
 */
public class RestURITester {
    private static final Pattern TO_CAMEL_CASE_PATTERN = Pattern.compile("((\\_)(\\w))");
    public static void main(String[] args) throws Exception{
        
        StringBuilder string = new StringBuilder("admin_create_date");
        Matcher matcher = TO_CAMEL_CASE_PATTERN.matcher(string);//.replaceAll("$3"));
        while(matcher.find()){
            string.replace(matcher.start(), matcher.end(), matcher.group(3).toUpperCase(Locale.ENGLISH));
            matcher.reset(string);
        }
        
        System.out.println(string);
        System.out.println("adminCreateDate".equals(string));
        
        RestURIBuilderDataSource dataSource = new RestURIBuilderDataSource() {
            public String evaluateTarget(RestURISQLBuilder restURISQLBuilder, String path) {
                return null;
            }

            public String evaluateVarName(RestURISQLBuilder restURISQLBuilder, String path) {
                return null;
            }

            public Map<String, Object> getKeysAndObjects(RestURISQLBuilder restURISQLBuilder, RestURIPart part) {
                HashMap<String,Object> map = new HashMap<String, Object>(3);
                map.put("id", 123);
                map.put("atc", "tycit");
                map.put("iou", "哈哈");
                
                return map;
            }
        };

        long start = System.currentTimeMillis();
        List<RestURIPart> parts = RestURIParser.parse("/get/user/12/get/role/roleId.eq(roleId)&roleId.gt(12)",null);
        RestURISQLBuilder builder = new RestURISQLBuilder(RestURISQLBuilder.Database.MySQL);
        builder.addDataSource(dataSource);
        builder.append(parts);
        Tuple<CharSequence,Object> result = builder.build();
        long end = System.currentTimeMillis();
        System.out.println(parts);
        System.out.println(result.keyObject);
        System.out.println(result.valueObject);
        System.out.println(result.isUpdate);
        System.out.println(end - start);
    }
}
