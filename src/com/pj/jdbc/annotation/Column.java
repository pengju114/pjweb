/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.jdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author PENGJU
 * 时间:2012-7-15 11:38:08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    /**
     * 列名
     * @return 
     */
    String name();
    
    /**
     * 是否要保存到数据库
     * 默认为保存
     * @return 
     */
    boolean save() default true;
}
