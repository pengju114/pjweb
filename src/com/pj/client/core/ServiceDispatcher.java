/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.client.core;

import com.pj.client.core.invokers.JSONRestURIInvoker;
import com.pj.client.core.invokers.XMLRestURIInvoker;
import com.pj.utilities.StringUtility;
import com.pj.web.res.Config;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * 服务分发器,每一个客户端请求经过这个转发器转发
 * @author PENGJU
 * email:pengju114@163.com
 * 时间:2012-9-18 22:42:41
 */
public class ServiceDispatcher {
    private static final Logger LOGGER=Logger.getLogger(ServiceDispatcher.class);
    
    public static void dispatch(HttpServletRequest request,HttpServletResponse response){
        ServiceInvoker.setRequest(request);
        ServiceInvoker.setResponse(response);

        //在这里根据参数module和数据类型判断要调用哪个 Invoker
        String type=StringUtility.ensureAsString(request.getParameter(ServiceInvoker.KEY_HEADER_DATA_TYPE));
        if (StringUtility.isEmpty(type)) {
            type="JSON";
        }
        
        String module = StringUtility.ensureAsString(request.getParameter(ServiceInvoker.KEY_HEADER_MODULE));
        
        String clazz = null;
        
        String invokerPattern = Config.getConfig(ServiceInvoker.CONF_CLASS_PATTERN, null);
        if (StringUtility.isEmpty(invokerPattern)) {
            String pkg=ServiceDispatcher.class.getPackage().getName();
            module = module+".";
            clazz=pkg+".invokers"+module+type+"Invoker";
        }else{
            // 配置文件设置了模式
            clazz = String.format(invokerPattern, module, type);
        }
        
        
        try {
            clazz = clazz.replaceAll("\\.{2,}", ".");// 去掉多余的点
            Class<ServiceInvoker> invokerClass=(Class<ServiceInvoker>) Class.forName(clazz);
            ServiceInvoker invoker=invokerClass.newInstance();
            invoker.invoke();
        } catch (Exception e) {
            // handle as REST URI
            ServiceInvoker invoker = null;
            
            if (type.equalsIgnoreCase("xml")) {
                    invoker = new XMLRestURIInvoker();
                }else{
                    invoker = new JSONRestURIInvoker();
                }
            
            try {
                invoker.invoke();
            } catch (Exception e1) {
            }
        }
        
        ServiceInvoker.clear();
    }
}
