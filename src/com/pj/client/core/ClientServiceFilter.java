/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pj.client.core;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * 客户端服务接口过滤器
 * @author luzhenwen
 */
public class ClientServiceFilter implements Filter{
    private static final String CONFIG_SERVLET_NAME = "servletName";
    private static final String VALUE_SERVLET_NAME = "client.service";
    private String servletName;

    public void init(FilterConfig filterConfig) throws ServletException {
        servletName = filterConfig.getInitParameter(CONFIG_SERVLET_NAME);
        if (servletName != null) {
            servletName = servletName.trim();
        }else{
            servletName = VALUE_SERVLET_NAME;
        }
        
        if (servletName.length() < 1) {
            servletName = VALUE_SERVLET_NAME;
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        Logger.getLogger(getClass().getName()).info(req.getRequestURI());
        
        if (req.getRequestURI().startsWith(servletName)) {
            ServiceDispatcher.dispatch(req, res);
        }
        
        chain.doFilter(request, response);
    }

    public void destroy() {
        
    }
    
}
