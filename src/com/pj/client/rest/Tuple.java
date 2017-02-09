/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pj.client.rest;

import java.util.ArrayList;
import java.util.List;

/**
 * 元组
 * @author luzhenwen
 * @param <K> 
 * @param <T> 
 */
public class Tuple<K,T extends Object> {

    public K keyObject;
    public boolean isUpdate = false;
    public final List<T> valueObject;
    
    public Tuple(){
        valueObject = new ArrayList<T>(3);
    }
    
    public Tuple(int size){
        valueObject = new ArrayList<T>(size);
    }
}
