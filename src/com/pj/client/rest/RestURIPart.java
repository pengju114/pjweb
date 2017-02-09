/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pj.client.rest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author luzhenwen
 */
public interface RestURIPart {
    public static final String OPERATION_ADD = "add";
    public static final String OPERATION_GET = "get";
    public static final String OPERATION_UPDATE = "update";
    public static final String OPERATION_DELETE = "delete";
    public static final List<String> OPERATIONS = Collections.unmodifiableList(Arrays.asList(OPERATION_ADD,OPERATION_DELETE,OPERATION_GET,OPERATION_UPDATE));
    
    /**
     * 获取path别名
     * @return 
     */
    public String getTargetAlias();
    public String getTarget();
    public String getOperation();
    public RestURICondition getCondition();
    public RestURIPart getParent();
}
