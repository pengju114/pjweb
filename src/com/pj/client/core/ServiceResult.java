/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.client.core;

import java.util.LinkedList;
import java.util.List;

/**
 * @author PENGJU
 * email:pengju114@163.com
 * 时间:2012-9-20 13:33:14
 */
public class ServiceResult {
    private int pageCount;//总页数
    private int pageNumber;//当前页
    private long resultCount;//结果总数
    private final List<Object> data;
    
    public ServiceResult(){
        data=new LinkedList<Object>();
    }

    /**
     * @return the pageCount
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * @param pageCount the pageCount to set
     */
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    /**
     * @return the pageNumber
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * @param pageNumber the pageNumber to set
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * @return the resultCount
     */
    public long getResultCount() {
        return resultCount;
    }

    /**
     * @param resultCount the resultCount to set
     */
    public void setResultCount(long resultCount) {
        this.resultCount = resultCount;
    }

    /**
     * @return the data
     */
    public List<Object> getData() {
        return data;
    }
}
