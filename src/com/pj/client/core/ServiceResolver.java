/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.client.core;

import com.pj.utilities.ConvertUtility;
import com.pj.utilities.StringUtility;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 服务类，可以实现execute处理业务
 * 并返回数据ServiceResult
 * @author PENGJU
 * email:pengju114@163.com
 * 时间:2012-9-18 22:29:58
 */
public abstract class ServiceResolver {
    public static final String CONF_CLASS_PATTERN="pattern.service.resolver";
    public static final String CONF_CLASS_REST="rest.resolver";
    
    private static final String SQL_INJECT_PATTERN = "((\\%3D)|(=))|((\\%27)|(\\')|(\\-\\-)|(\\%3B)|(:))";
    
    private static final int DEFAULT_PAGESIZE = Integer.MAX_VALUE; // 默认返回所有数据
    
    public HttpServletRequest getRequest(){
        return ServiceInvoker.getRequest();
    }
    public HttpServletResponse getResponse(){
        return ServiceInvoker.getResponse();
    }
    
    /*******************  快捷方法  ******************/
    
    /**
     * 获取string类型的参数
     * @param key
     * @return 参数值，无则返回空字符串
     */
    public String getStringParameter(String key){
        return StringUtility.ensureAsString(getRequest().getParameter(key));
    }
    /**
     * 获取string类型的参数，并且进行了SQL注入攻击和跨站脚本攻击安全过滤
     * @param key
     * @return 处理后的参数值，无则返回空字符串
     */
    public String getSafeStringParameter(String key){
        String val = getStringParameter(key);
        return val.replaceAll(SQL_INJECT_PATTERN, "");
    }
    
    /**
     * 获取int型参数值
     * @param key
     * @return 参数值，无则返回0
     */
    public Integer getIntParameter(String key){
        return ConvertUtility.parseInt(getStringParameter(key));
    }
    
    /**
     * 获取float型参数值
     * @param key
     * @return 参数值，无则返回0.0
     */
    public Float getFloatParameter(String key){
        return ConvertUtility.parseFloat(getStringParameter(key));
    }
    
    /**
     * 获取long型参数值
     * @param key
     * @return 参数值，无则返回0
     */
    public Long getLongParameter(String key){
        return ConvertUtility.parseLong(getStringParameter(key));
    }
    
    /**
     * 根据传进来的参数对生成一个map，map的内容将是map.put(keyAndValues[0],keyAndValues[1]),
     * map.put(keyAndValues[2],keyAndValues[3]) ...
     * @param keyAndValues
     * @return 包含参数对的map
     */
    public Map<String,Object> makeMapByKeyAndValues(Object... keyAndValues){
        
        if (keyAndValues != null) {
            HashMap<String,Object> map = new HashMap<String, Object>(keyAndValues.length/2+1);
            int keyIndex = 0;
            int valIndex = 1;
            
            while (valIndex < keyAndValues.length) {                
                map.put(StringUtility.ensureAsString(keyAndValues[keyIndex]), keyAndValues[valIndex]);
                keyIndex += 2;
                valIndex += 2;
            }
            
            return map;
        }
        return null;
    }
    
    /**
     * 获取客户端指定的每页多少个数据
     * @return 每页数据条数，未指定则返回10
     */
    public int getPageSize(){
        int pageSize = ConvertUtility.parseInt(getRequest().getParameter(ServiceInvoker.KEY_HEADER_PAGESIZE), DEFAULT_PAGESIZE);
        if (pageSize<1) {
            pageSize = DEFAULT_PAGESIZE;
        }
        return pageSize;
    }
    /**
     * 获取客户端指定的页
     * @return 指定页，未指定则返回1
     */
    public int getPageNumber(){
        int pageNumber = ConvertUtility.parseInt(getRequest().getParameter(ServiceInvoker.KEY_HEADER_PAGENUMBER), 1);
        if (pageNumber<1) {
            pageNumber = 1;
        }
        return pageNumber;
    }
    
    /**
     * 计算分页属性
     * @param total 总结果数
     * @param result 服务结果
     */
    public void calculatePageProperties(long total,ServiceResult result){
        result.setResultCount(total);
        int pn = getPageNumber();
        result.setPageNumber(pn);
        
        int pageSize = getPageSize();
        // 这里一般是没指定pageSize的情况
        if (result.getData().size()>pageSize) {
            pageSize = result.getData().size();
        }
        
        if (result.getResultCount()>0) {
            int pg = (int)(result.getResultCount()/pageSize);
            if (result.getResultCount()%pageSize>0) {
                pg++;
            }
            result.setPageCount(pg);
        }
    }
    
    /**
     * 在调用{@link #execute()}方法之前调用，此方法抛错将忽略
     */
    public void executePrepare(){}
    /**
     * 业务实现方法，Resolver要实现的功能在这个方法实现
     * @return 服务结果
     * @throws Exception 
     */
    public abstract ServiceResult execute() throws Exception;
    /**
     * 在调用{@link #execute() }方法完成后调用此方法
     * 当{@link #execute() }方法抛错时此方法依然调用
     * 方法抛错将忽略
     * @param result 
     */
    public void executeComplete(ServiceResult result) {}
}
