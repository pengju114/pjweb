/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pj.client.rest;

import java.util.Map;

/**
 * RestURISQLBuilder的数据源,RestURISQLBuilder新建时内部有一个默认的数据源处理器(CamelCaseDataSource).
 * @author luzhenwen
 */
public interface RestURIBuilderDataSource {
    /**
     * 处理REST URI中的target字段,返回对应的表名.
     * @param restURISQLBuilder
     * @param target
     * @return 
     */
    public String evaluateTarget(RestURISQLBuilder restURISQLBuilder, String target);

    /**
     * 处理变量名
     * @param restURISQLBuilder
     * @param varName
     * @return 
     */
    public String evaluateVarName(RestURISQLBuilder restURISQLBuilder, String varName);

    /**
     * 根据part信息提供要更新或者插入的列名和对象
     *
     * @param restURISQLBuilder
     * @param part
     * @return
     */
    public Map<String, Object> getKeysAndObjects(RestURISQLBuilder restURISQLBuilder, RestURIPart part);
}