/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.jdbc.core;

import com.pj.json.JSONArray;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author PENGJU
 * 时间:2012-7-14 13:30:27
 */
class ResultListImpl<T> extends LinkedList<T> implements ResultList<T> {
    private ArrayList<String> columnNames;

    private int totalRowsCount;
    
    public ResultListImpl(){
        columnNames=new ArrayList<String>();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public String[] getColumnNames() {
        return columnNames.toArray(new String[]{});
    }

    @Override
    public String getColumnName(int index) {
        return columnNames.get(index);
    }

    @Override
    public int getSize() {
        return size();
    }

    @Override
    public int getTotalRowsCount() {
        return totalRowsCount;
    }

    /**
     * @param totalRowsCount the totalRowsCount to set
     */
    public void setTotalRowsCount(int totalRowsCount) {
        this.totalRowsCount = totalRowsCount;
    }
    
    public void addColumnName(String name){
        columnNames.add(name);
    }
    public void addRow(T o){
        add(o);
    }

    @Override
    public JSONArray toJSONArray() {
        JSONArray array=new JSONArray(this);
        return array;
    }
    
    @Override
    protected void finalize() throws Throwable{
        columnNames.clear();
        super.finalize();
    }

    @Override
    public List<T> toList() {
        return this;
    }
    
    @Override
    public boolean hasColumn(String name){
        return columnNames.contains(name);
    }
}
