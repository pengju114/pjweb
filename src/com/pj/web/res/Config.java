/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pj.web.res;

import com.pj.utilities.ConvertUtility;
import com.pj.utilities.StringUtility;
import java.io.InputStream;
import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 配置信息类
 * @author 陆振文[PENGJU]
 */
public class Config {
    private static final Logger logger=Logger.getLogger(Config.class);
    
    private static final String CONFIG_FILE="config.xml";
    private static final String CONFIG_ITEM="item";
    private static final String CONFIG_ATTR="name";
    
    private static final HashMap<String,String> CONFIG_MAP;
    static{
        CONFIG_MAP=new HashMap<String, String>();
        loadConfig();
    }
    
    public static interface Key{
        String DEBUG="debug";
        String CHARSET = "charset";
    }
    public static interface Value{
        String DEBUG="false";
        String CHARSET = "UTF-8";
    }
    
    private static void loadConfig(){
        InputStream inputStream=Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE);
        if (inputStream!=null) {
            try {
                Document document=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
                Element rootElement=document.getDocumentElement();
                if (rootElement!=null) {
                    NodeList childrenList=rootElement.getChildNodes();
                    if (childrenList!=null) {
                        for (int i = 0; i < childrenList.getLength(); i++) {
                            Node childNode=childrenList.item(i);
                            if(CONFIG_ITEM.equalsIgnoreCase(childNode.getNodeName()) && childNode.hasAttributes()){
                                Node attr=childNode.getAttributes().getNamedItem(CONFIG_ATTR);
                                if (attr!=null) {
                                    CONFIG_MAP.put(StringUtility.ensureAsString(attr.getNodeValue()).trim() , StringUtility.ensureAsString(childNode.getTextContent()).trim());
                                }
                            }
                        }
                    }
                }
                inputStream.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }else{
            logger.error("can't find config file at the context!");
        }
    }
    
    public static void reload(){
        CONFIG_MAP.clear();
        loadConfig();
    }
    
    public static int getConfig(String name, int defaultValue){
        return ConvertUtility.parseInt(CONFIG_MAP.get(name), defaultValue);
    }
    public static float getConfig(String name, float defaultValue){
        return ConvertUtility.parseFloat(CONFIG_MAP.get(name), defaultValue);
    }
    public static long getConfig(String name, long defaultValue){
        return ConvertUtility.parseLong(CONFIG_MAP.get(name), defaultValue);
    }
    public static double getConfig(String name, double defaultValue){
        return ConvertUtility.parseDouble(CONFIG_MAP.get(name), defaultValue);
    }
    public static boolean getConfig(String name, boolean defaultValue){
        String val=CONFIG_MAP.get(name);
        if(StringUtility.isEmpty(val)){
            return defaultValue;
        }
        return Boolean.valueOf(val);
    }
    public static String getConfig(String name, String defaultValue){
        String val=CONFIG_MAP.get(name);
        if(StringUtility.isEmpty(val)){
            return defaultValue;
        }
        return val;
    }
    
    public static void bindToContext(ServletContext context){
        context.setAttribute("cfg", CONFIG_MAP);
        context.setAttribute("config", CONFIG_MAP);
    }
    
    public static boolean debug(){
        return getConfig(Key.DEBUG,false);
    }
}
