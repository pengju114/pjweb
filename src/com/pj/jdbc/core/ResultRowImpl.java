/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.jdbc.core;

import com.pj.json.JSONObject;
import com.pj.utilities.ConvertUtility;
import com.pj.utilities.StringUtility;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author PENGJU
 * 时间:2012-7-14 14:20:46
 */
public class ResultRowImpl extends HashMap<String, Object> implements ResultRow{
    private int row;
    private final ResultList rslist;
    
    public ResultRowImpl(ResultList rslist){
        this.rslist=rslist;
    }

    @Override
    public int getRow() {
        return row;
    }
    public void setRow(int row){
        this.row=row;
    }

    @Override
    public boolean getBoolean(int index) {
        return getBoolean(rslist.getColumnName(index));
    }

    @Override
    public byte getByte(int index) {
        return getByte(rslist.getColumnName(index));
    }

    @Override
    public Date getDate(int index) {
        return getDate(rslist.getColumnName(index));
    }

    @Override
    public Time getTime(int index) {
        return getTime(rslist.getColumnName(index));
    }

    @Override
    public Timestamp getTimestamp(int index) {
        return getTimestamp(rslist.getColumnName(index));
    }

    @Override
    public long getLong(int index) {
        return getLong(rslist.getColumnName(index));
    }

    @Override
    public double getDouble(int index) {
        return getDouble(rslist.getColumnName(index));
    }

    @Override
    public float getFloat(int index) {
        return getFloat(rslist.getColumnName(index));
    }

    @Override
    public int getInt(int index) {
        return getInt(rslist.getColumnName(index));
    }

    @Override
    public Object getObject(int index) {
        return getObject(rslist.getColumnName(index));
    }

    @Override
    public String getString(int index) {
        return getString(rslist.getColumnName(index));
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
        return ConvertUtility.parseInt(object.toString()).byteValue();
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
        return ConvertUtility.parseLong(object.toString());
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
        return ConvertUtility.parseDouble(object.toString());
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
        return ConvertUtility.parseFloat(object.toString());
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
        return ConvertUtility.parseInt(object.toString());
    }

    @Override
    public Object getObject(String name) {
        return get(name);
    }

    @Override
    public String getString(String name) {
        return StringUtility.ensureAsString(get(name));
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
        return  rslist.getColumnNames();
    }
    
    @Override
    public String toString(){
        return super.toString();
    }
    
    @Override
    protected void finalize() throws Throwable{
        clear();
        super.finalize();
    }

    @Override
    public Map<String, Object> toMap() {
        return this;
    }
}
