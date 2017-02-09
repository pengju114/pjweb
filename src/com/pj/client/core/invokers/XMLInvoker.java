/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.client.core.invokers;

import com.pj.client.core.ServiceInvoker;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 *
 * @author PENGJU
 * email:pengju114@163.com
 * 时间:2012-9-20 16:45:21
 */
public class XMLInvoker extends ServiceInvoker{

    
    public XMLInvoker(){
        
    }

    @Override
    protected String getContentType() {
        return "text/xml; charset="+getCharset();
    }
    
    
    
    
    @Override
    protected void writeResult(PrintWriter writer,Map<String,Object> rMap) throws IOException{
        StringBuilder builder=new StringBuilder("<response>");
        genXML(builder, rMap);
        builder.append("</response>");
        writer.print(builder);
    }
    
    private void genXML(StringBuilder builder,Object data){
        if (data instanceof Map) {
            wrapMap(builder, (Map<String, Object>)data);
        }else if(data instanceof List){
            wrapList(builder, (List<Object>)data);
        }
    }
    
    private void wrapMap(StringBuilder builder,Map<String,Object> data){
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String string = entry.getKey();
            Object object = entry.getValue();
            
            builder.append('<').append(string).append('>');
            if (object instanceof Map) {
                wrapMap(builder, (Map<String,Object>)object);
            } else if(object instanceof List) {
                wrapList(builder, (List<Object>)object);
            } else{
                builder.append(object);
            }
            builder.append('<').append('/').append(string).append('>');
        }
    }
    private void wrapList(StringBuilder builder,List<Object> data){
        
        for (Object object : data) {
            builder.append("<item>");
            if (object instanceof Map) {
                wrapMap(builder, (Map<String, Object>)object);
            } else if(object instanceof List){
                wrapList(builder, (List<Object>)object);
            }
            builder.append("</item>");
        }
    }
}
