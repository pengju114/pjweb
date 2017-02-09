/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.client.core;

import com.pj.json.JSONObject;
import com.pj.utilities.StringUtility;
import com.pj.web.res.Config;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * 服务触发器，负责调用{@link ServiceResolver}
 * 具体调用哪个服务触发器，在{@link ServiceDispatcher}可以根据参数|URL等判断
 * 不同的服务触发器可以调用不同包下的ServiceResolver
 * 比如JSONInvoker触发器将输出JSON数据，而XMLInvoker将输出XML数据
 * ServiceInvoker相当于请求进口和出口，负责数据分发传递和输出处理
 * @author PENGJU
 * email:pengju114@163.com
 * 时间:2012-9-18 22:39:08
 */
public abstract class ServiceInvoker {
    public static final String CONF_CLASS_PATTERN="pattern.service.invoker";
    
    public static final String KEY_REQUEST="com.pj.client.core.request";
    public static final String KEY_RESPONSE="com.pj.client.core.response";
    public static final String KEY_RESULT="result";//结果键
    public static final String KEY_HEADER="header";//响应头部
    
    public static final String KEY_HEADER_STATUS_CODE="statusCode";//状态码
    public static final String KEY_HEADER_STATUS_TEXT="statusText";//状态说明
    public static final String KEY_HEADER_PAGECOUNT="pageCount";//页数
    public static final String KEY_HEADER_PAGENUMBER="pageNumber";//当前页
    public static final String KEY_HEADER_TOTALRESULTS="totalResults";//总结果数
    
    public static final String KEY_HEADER_PAGESIZE="pageSize";//每页记录数
    public static final String KEY_HEADER_DATA_TYPE="dataType";//数据类型
    public static final String KEY_HEADER_MODULE="module";//模块
    public static final String KEY_HEADER_SERVICE="service";//要访问的服务ID

    /**
     * 默认编码
     */
    protected static final String DEFAULT_CHARSET="UTF-8";
    
    protected static final Logger LOGGER= Logger.getLogger(ServiceInvoker.class);
    /**
     * 用ThreadLocal维护本线程的HttpServletRequest和HttpServletResponse接口
     * 在ServiceResolver中就可以通过 {@link ServiceInvoker#getRequest()}/{@link ServiceInvoker#getResponse()}获取request/response
     */
    private static final ThreadLocal<Map<String,Object>> CONTEXT_THREAD_LOCAL=new ThreadLocal<Map<String, Object>>(){
        @Override 
        protected Map<String,Object> initialValue() {
            return new HashMap<String, Object>();
        }
    };
    

    public static void setRequest(HttpServletRequest request){
        put(KEY_REQUEST, request);
    }
    
    public static void setResponse(HttpServletResponse response){
        put(KEY_RESPONSE, response);
    }
    
    /**
     * @return the request
     */
    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) get(KEY_REQUEST);
    }

    /**
     * @return the response
     */
    public static HttpServletResponse getResponse() {
        return (HttpServletResponse) get(KEY_RESPONSE);
    }
    
    public static void put(String key,Object value){
        CONTEXT_THREAD_LOCAL.get().put(key, value);
    }
    
    public static Object get(String key){
        return CONTEXT_THREAD_LOCAL.get().get(key);
    }
    
    /**
     * 编码格式
     * @return 
     */
    protected String getCharset(){
        return DEFAULT_CHARSET;
    }
    
    protected String getContentType(){
        return "text/json;charset="+getCharset();// 默认
    }
    
    /**
     * 通过这个方法可以获取类{@link ServiceResolver}的实现类全路径
     * 然后就可以通过反射查找类并调用服务
     * 如"com.pj.client.core.resolvers.JSONResolver10001"
     * 默认实现为去配置文件里面获取路径模式，无配置则返回null
     * @return ServiceResolver类全路径
     */
    public String getResolverClassName(){
        String service = getRequest().getParameter(KEY_HEADER_SERVICE);
        String pattern = Config.getConfig(ServiceResolver.CONF_CLASS_PATTERN, null);
        String module = StringUtility.ensureAsString(getRequest().getParameter(KEY_HEADER_MODULE));
        
        if (StringUtility.isEmpty(service) || StringUtility.isEmpty(pattern)) {
            return null;
        }
        
        return String.format(pattern, module ,service).replaceAll("\\.{2,}", ".");
    }
    
    /**
     * 在调用{@link ServiceResolver#execute()}方法前调用
     * 若无错误将继续执行，否则将不执行{@link ServiceResolver#execute()}方法
     * @throws Exception 
     */
    public void invokePrepare() throws Exception{
        
    }
    
    /**
     * 在调用{@link ServiceResolver#execute()}后调用
     * 不管是否执行{@link ServiceResolver#execute()}方法或是否抛出异常
     * @param result 调用{@link ServiceResolver#execute()}返回的结果,may be null
     * @throws Exception 
     */
    public void invokeComplete(ServiceResult result) throws Exception{
        
    }
    
    /**
     * 此方法将调用{@link ServiceResolver#execute()}方法
     * 通过{@link #getResolverClassName() }查找对应的ServiceResolver
     * 并调用服务
     */
    public final void invoke(){
        try {
            getRequest().setCharacterEncoding(getCharset());
            getResponse().setCharacterEncoding(getCharset());
            getResponse().setContentType(getContentType());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        
        //结果Map
        Map<String,Object> resultWrapper=new HashMap<String, Object>();
        //头部信息map
        Map<String,Object> header=new HashMap<String, Object>();
        List<Object> result=null;
        //错误信息
        String error=null;
        ServiceResult serviceResult=null;
        int errorCode=ClientException.REQUEST_OK;
        try {
            invokePrepare();

            String clazz=getResolverClassName();
            Class<ServiceResolver> resolverClass=(Class<ServiceResolver>) Class.forName(clazz);
            ServiceResolver resolver=resolverClass.newInstance();
            
            try {
                resolver.executePrepare();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            
            try {
                serviceResult=resolver.execute();
            } catch (Exception e) {
                throw e;
            } finally{
                try {
                    resolver.executeComplete(serviceResult);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (e instanceof ClientException) {
                //处理发给客户端的错误信息
                error=e.getMessage();
                errorCode=((ClientException)e).getErrorCode();
            }else{
                errorCode=ClientException.REQUEST_ERROR;
            }
        }

        try {
            invokeComplete(serviceResult);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (e instanceof ClientException) {
                //处理发给客户端的错误信息
                error=e.getMessage();
                errorCode=((ClientException)e).getErrorCode();
            } else{
                errorCode=ClientException.REQUEST_ERROR;
            }
        }
        
        //包装数据
        header.put(KEY_HEADER_STATUS_CODE, errorCode);
        header.put(KEY_HEADER_STATUS_TEXT, StringUtility.ensureAsString(error));
        if (serviceResult!=null) {
            header.put(KEY_HEADER_PAGECOUNT, serviceResult.getPageCount());
            header.put(KEY_HEADER_PAGENUMBER, serviceResult.getPageNumber());
            header.put(KEY_HEADER_TOTALRESULTS, serviceResult.getResultCount());
            result=serviceResult.getData();
        }
        resultWrapper.put(KEY_HEADER, header);
        resultWrapper.put(KEY_RESULT, result);
        
        HttpServletResponse response=getResponse();
        PrintWriter writer=null;
        try {
            writer=response.getWriter();
            writeResult(writer,resultWrapper);
            writer.flush();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally{
            if (writer!=null) {
                try { writer.close(); } catch (Exception e) {
                }
            }
        }
        
    }
    
    /**
     * 把结果转换成JSON数据然后输出
     * 所有输出在此完成
     * 可重写
     * @param writer
     * @param rMap
     * @throws IOException 
     */
    protected void writeResult(PrintWriter writer,Map<String,Object> rMap) throws IOException{
        JSONObject object=new JSONObject(rMap);
        writer.print(object);
    }

    public static void clear(){
        CONTEXT_THREAD_LOCAL.remove();
    }
}
