/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.jdbc.core;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.pj.utilities.ConvertUtility;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author PENGJU
 * 时间:2012-7-14 10:49:12
 */
public class SessionFactory {
    private static final HashMap<String,BoneCP> POOL_MAP=new HashMap<String, BoneCP>();
    public static final String DEFAULT_SESSION="default";
    static {
        loadXMLConfig();
    }
    
    /**
     * 获取默认的连接会话
     * @return
     */
    public static final Session getDefaultSession() throws SQLException{
        return getSession(DEFAULT_SESSION);
    }
    public static final Session getSession(String name) throws SQLException{
        BoneCP pool=POOL_MAP.get(name);
        if (pool==null) {
            return null;
        }
        return new SessionImpl(pool.getConnection());
    }

    private static void loadXMLConfig() {
        InputStream in=Thread.currentThread().getContextClassLoader().getResourceAsStream("jdbc.cfg.xml");
        if (in!=null) {
            try {
                Document document=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
                Element root=document.getDocumentElement();
                NodeList items=root.getElementsByTagName("item");
                for (int i = 0; i < items.getLength(); i++) {
                    Node node = items.item(i);
                    String name=node.getAttributes().getNamedItem("name").getNodeValue();
                    BoneCPConfig config=new BoneCPConfig();
                    
                    NodeList paramList=node.getChildNodes();
                    for (int j = 0; j < paramList.getLength(); j++) {
                        Node n=paramList.item(j);
                        if (n.getNodeType()!=Node.ELEMENT_NODE) {
                            continue;
                        }
                        String nodeName=n.getNodeName();
                        String val=n.getTextContent();
                        
                        if ("driver-class".equals(nodeName)) {
                            try {
                                Class.forName(val);
                            } catch (Exception e) {
                                Logger.getLogger(SessionFactory.class).error("Class "+val+" Not Found", e);
                            }
                        }else if("url".equals(nodeName)){
                            config.setJdbcUrl(val);
                        }else if("username".equals(nodeName)){
                            config.setUsername(val);
                        }else if("password".equals(nodeName)){
                            config.setPassword(val);
                        }else if("partition-count".equals(nodeName)){
                            config.setPartitionCount(ConvertUtility.parseInt(val, 3));
                        }else if("max-connections-perpartition".equals(nodeName)){
                            config.setMaxConnectionsPerPartition(ConvertUtility.parseInt(val, 30));
                        }else if("min-connections-perpartition".equals(nodeName)){
                            config.setMinConnectionsPerPartition(ConvertUtility.parseInt(val, 10));
                        }else if("acquire-increment".equals(nodeName)){
                            config.setMaxConnectionsPerPartition(ConvertUtility.parseInt(val, 10));
                        }
                    }
                    
                    POOL_MAP.put(name, new BoneCP(config));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally{
                if (in!=null) {
                    try {
                        in.close();
                    } catch (IOException ex) {
                    }
                }
            }
        }
    }
    
    
    /** 仅仅用来加载此类，以此调用 {@link #loadXMLConfig} 方法 */
    public static void initialize(){
        
    }
    
    public static void release(){
        for (BoneCP object : POOL_MAP.values()) {
            object.shutdown();
        }
        POOL_MAP.clear();
    }
}
