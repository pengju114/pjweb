/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.jdbc.services;

import com.pj.jdbc.core.JdbcTemplate;
import com.pj.jdbc.core.Session;
import com.pj.jdbc.core.SessionFactory;

/**
 *
 * @author PENGJU
 * email:pengju114@163.com
 * 时间:2012-7-16 21:22:20
 */
public class BaseService {
    protected JdbcTemplate getJdbcTemplate(){
        return  getJdbcTemplate(SessionFactory.DEFAULT_SESSION);
    }
    protected JdbcTemplate getJdbcTemplate(String id){
        return new JdbcTemplate(id);
    }
    /**
     * 用完记得关闭
     * @param id
     * @return 
     */
    protected Session getSession(String id){
        try {
            return SessionFactory.getSession(id);
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 用完记得关闭
     * @return 
     */
    protected Session getSession(){
        try {
            return SessionFactory.getDefaultSession();
        } catch (Exception e) {
            return null;
        }
    }
}
