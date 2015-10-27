/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgroup;

import java.io.Serializable;

/**
 *
 * @author gilang
 */
public class MsgObject<T> implements Serializable{
    
    private static final long serialVersionUID = 84819741;
    
    public static final String POP = "pop";
    public static final String PUSH = "push";
    public static final String TOP = "top";
    public static final String ADD = "add";
    public static final String CONTAINS = "contains";
    public static final String REMOVE = "remove";
    
    private T object;
    private String message;
    
    public MsgObject(String message){
        object = null;
        this.message = message;
    }
    
    public MsgObject(T obj, String message){
        object = obj;
        this.message = message;
    }
    
    public T getObject(){
        return object;
    }
    
    public String getMessage(){
        return message;
    }
}
