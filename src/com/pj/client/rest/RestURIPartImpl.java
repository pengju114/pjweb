/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pj.client.rest;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 *
 * @author luzhenwen
 */
class RestURIPartImpl implements RestURIPart{
    private final String target;
    private final String operation;
    private final RestURICondition condition;
    private final String targetAlias;
    private RestURIPart parent;
    private final ArrayList<String> selectVars;
    
    public RestURIPartImpl( String target,String targetAlias, String operation, String condition) throws Exception{
        this.target = target;
        this.operation = operation;
        this.condition = condition == null?null:new RestURICondition(condition);
        this.targetAlias = targetAlias;
        selectVars = new ArrayList<String>(2);
        checkTarget();
        checkOperation();
    }

    @Override
    public String toString() {
        return "/"+getOperation()+"/"+getTarget()+"/"+(getCondition()==null?"":getCondition()); //To change body of generated methods, choose Tools | Templates.
    }

    public String getTarget() {
        return target;
    }

    public String getTargetAlias() {
        return targetAlias;
    }

    /**
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    public RestURICondition getCondition() {
        return condition;
    }

    public RestURIPart getParent() {
        return parent;
    }

    public void setParent(RestURIPart parent) {
        this.parent = parent;
    }

    public ArrayList<String> getSelectVars() {
        return selectVars;
    }

    private void checkOperation() {
        if (operation == null) {
            throw new IllegalArgumentException("illegal operation, operation should not be null");
        }
        if (!OPERATIONS.contains(operation)) {
            throw new IllegalArgumentException("illegal operation,operation must be one of:"+OPERATIONS);
        }
        
        if (operation.equalsIgnoreCase(OPERATION_ADD) && condition!=null) {
            throw new IllegalArgumentException("illegal operation,add operation can't have any condition");
        }
    }

    private void checkTarget() {
        if (Pattern.compile("[^\\w]").matcher(target).find()) {
            throw new IllegalArgumentException("illegal target,target can only contains numbers and characters");
        }
    }
    
    
}
