/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.utilities;

import java.util.Iterator;

/**
 *
 * @author PENGJU
 * 时间:2012-7-15 13:40:54
 */
public class ArrayUtility {
    /**
     * 将数组内容以指定字符串组合返回，如果是null则替换为空字符串
     * @param array 要组合的数组
     * @param separator 分隔符
     * @return 组合后的字符串
     */
    public static String join(Object[] array,String separator){
        StringBuilder sb=new StringBuilder();
        if (array!=null) {
            for (Object object : array) {
                sb.append(object==null?"":object).append(separator);
            }
            
            if (array.length>0) {
                sb.delete(sb.length()-separator.length(), sb.length());
            }
        }
        return  sb.toString();
    }
    
    /**
     * 对一个对象用指定组合分隔符重复组合指定次数，如果val为null，直接返回null。
     * @param val 要组合的对象
     * @param count 组合次数
     * @param separator 分隔符
     * @return 组合后的数据
     */
    public static String join(Object val,int count ,String separator){
        
        if (val == null) {
            return null;
        }
        
        StringBuilder sb=new StringBuilder();
        for (int i = 0; i <count; i++) {
            sb.append(val).append(separator);
        }
        if (count>0) {
            sb.delete(sb.length()-separator.length(), sb.length());
        }
        return  sb.toString();
    }
    
    /**
     * 用指定分隔符组合列表里面的数据，如果是null将替换为空字符串
     * @param values
     * @param seperator
     * @return 
     */
    public static String join(Iterator values,String seperator){
        StringBuilder buf=new StringBuilder();
        int count=0;
        while(values.hasNext()){
            Object obj = values.next();
            buf.append(obj == null? "":obj).append(seperator);
            count++;
        }
        if(count>0)buf.replace(buf.length()- seperator.length(),buf.length(),"");
        return buf.toString();
    }
    
    public static Number sum(Number[] v){
        double s=0;
        int b=0,e=v.length-1;
        if(v.length<=0)return 0;
        if(v.length%2!=0)s+=v[v.length/2].doubleValue();
        while(b<e){
            s+=(v[b].doubleValue()+v[e].doubleValue());
            b++;
            e--;
        }
        return s;
    }

    public static boolean contains(Object[] set,Object value){
        if(set!=null){
            boolean match = false;
            int b=0,e=set.length-1;
            if(set.length <= 0){
                return false;
            }
            if(set.length%2 != 0){
                match = set[set.length/2].equals(value);
            }
            while(!match && b<e){
                
                match = set[b].equals(value);
                if (match) {
                    break;
                }
                match = set[e].equals(value);
                
                b++;
                e--;
            }
            
            return match;
        }
        return false;
    }
}
