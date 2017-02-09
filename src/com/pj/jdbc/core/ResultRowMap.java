/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.jdbc.core;

import com.pj.json.JSONObject;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author PENGJU
 * email:pengju114@163.com
 * 时间:2012-7-16 13:23:49
 */
public class ResultRowMap extends LinkedHashMap<String, Object> implements ResultRow{
    private int  row=0;
    @Override
    public int getRow() {
        return row;
    }
    public void setRow(int row){
        this.row=row;
    }
    
    private String getKeyByIndex(int index){
        int c=0;
        for (String key : keySet()) {
            if (c==index) {
                return key;
            }
            c++;
        }
        return null;
    }

    @Override
    public boolean getBoolean(int index) {
        return getBoolean(getKeyByIndex(index));
    }

    @Override
    public byte getByte(int index) {
        return getByte(getKeyByIndex(index));
    }

    @Override
    public Date getDate(int index) {
        return getDate(getKeyByIndex(index));
    }

    @Override
    public Time getTime(int index) {
        return getTime(getKeyByIndex(index));
    }

    @Override
    public Timestamp getTimestamp(int index) {
        return getTimestamp(getKeyByIndex(index));
    }

    @Override
    public long getLong(int index) {
        return getLong(getKeyByIndex(index));
    }

    @Override
    public double getDouble(int index) {
        return getDouble(getKeyByIndex(index));
    }

    @Override
    public float getFloat(int index) {
        return getFloat(getKeyByIndex(index));
    }

    @Override
    public int getInt(int index) {
        return getInt(getKeyByIndex(index));
    }

    @Override
    public Object getObject(int index) {
        return getObject(getKeyByIndex(index));
    }

    @Override
    public String getString(int index) {
        return getString(getKeyByIndex(index));
    }

    @Override
    public boolean getBoolean(String name) {
        Object object=get(name);
        if (object==null) {
            return  false;
        }
        if (object instanceof Number) {
            return ((Number)object).intValue()!=0;
        }
        
        if (object instanceof Boolean) {
            return (Boolean)object;
        }
        return Boolean.valueOf(object.toString());
    }

    @Override
    public byte getByte(String name) {
        Object object=get(name);
        if (object==null) {
            return 0;
        }
        if (object instanceof Number) {
            return ((Number)object).byteValue();
        }
        return Byte.valueOf(object.toString().trim());
    }

    @Override
    public Date getDate(String name) {
        Object object=get(name);
        if(object==null){
            return  null;
        }
        if (object instanceof Date) {
            return (Date)object;
        }
        return Date.valueOf(object.toString().trim());
    }

    @Override
    public Time getTime(String name) {
        Object object=get(name);
        if(object==null){
            return  null;
        }
        if (object instanceof Time) {
            return (Time)object;
        }
        return Time.valueOf(object.toString().trim());
    }

    @Override
    public Timestamp getTimestamp(String name) {
        Object object=get(name);
        if(object==null){
            return  null;
        }
        if (object instanceof Timestamp) {
            return (Timestamp)object;
        }
        return Timestamp.valueOf(object.toString().trim());
    }

    @Override
    public long getLong(String name) {
        Object object=get(name);
        if (object==null) {
            return 0L;
        }
        if (object instanceof Number) {
            return ((Number)object).longValue();
        }
        return Long.valueOf(object.toString().trim());
    }

    @Override
    public double getDouble(String name) {
        Object object=get(name);
        if (object==null) {
            return 0D;
        }
        if (object instanceof Number) {
            return ((Number)object).doubleValue();
        }
        return Double.valueOf(object.toString().trim());
    }

    @Override
    public float getFloat(String name) {
        Object object=get(name);
        if (object==null) {
            return 0F;
        }
        if (object instanceof Number) {
            return ((Number)object).floatValue();
        }
        return Float.valueOf(object.toString().trim());
    }

    @Override
    public int getInt(String name) {
        Object object=get(name);
        if (object==null) {
            return 0;
        }
        if (object instanceof Number) {
            return ((Number)object).intValue();
        }
        return Integer.valueOf(object.toString().trim());
    }

    @Override
    public Object getObject(String name) {
        return get(name);
    }

    @Override
    public String getString(String name) {
        Object o=get(name);
        if (o==null) {
            return "";
        }
        return String.valueOf(o);
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jSONObject=new JSONObject(this);
        return jSONObject;
    }

    @Override
    public void putData(String key, Object value) {
        put(key, value);
    }

    @Override
    public String[] keys() {
        return  keySet().toArray(new String[0]);
    }

    @Override
    public Map<String, Object> toMap() {
        return this;
    }
}
