/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.web.listeners;

import com.pj.jdbc.core.SessionFactory;
import com.pj.web.res.Config;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author PENGJU
 * 时间:2012-7-14 19:48:08
 */
public class ContextListener implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //加载配置到Application Scope
        Config.bindToContext(sce.getServletContext());
        sce.getServletContext().setAttribute("contextPath", sce.getServletContext().getContextPath());
        
        SessionFactory.initialize();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        SessionFactory.release();
    }
}
