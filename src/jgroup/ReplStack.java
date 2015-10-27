/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgroup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

/**
 *
 * @author gilang
 */
public class ReplStack<T> extends ReceiverAdapter{
    
    private final Stack<T> stack = new Stack<>();
    private JChannel channel;
    
    public ReplStack() throws Exception{
        channel = new JChannel();
        channel.setReceiver(this);
    }
    
    public ReplStack(String channelName) throws Exception{
        channel = new JChannel();
        channel.connect(channelName);
        channel.setReceiver(this);
        channel.getState(null, 0);
    }
    
    public void connect(String channelName) throws Exception{
        channel.connect(channelName);
        channel.getState(null, 0);
    }
    
    public void close(){
        channel.close();
    }
    
    public void push(T object) throws Exception{
//        synchronized(stack){
//            stack.push(object);
//        }
        MsgObject<T> mo = new MsgObject<>(object, MsgObject.PUSH);
        Message msg = new Message(null, null, serialize(mo));
        channel.send(msg);
    }
    
    public T pop() throws Exception{
        T obj = null;
        synchronized(stack){
            obj = stack.peek();
        }
        MsgObject<T> mo = new MsgObject<>(MsgObject.POP);
        Message msg = new Message(null, null, serialize(mo));
        channel.send(msg);
        return obj;
    }
    
    public T top(){
        T obj = null;
        synchronized(stack){
            obj = stack.peek();
        }
        return obj;
    }
    
     @Override
    public void viewAccepted(View new_view) {
        System.out.println("View: " + new_view);
    }
    
    @Override
    public void receive(Message message) {
        byte[] msgBuffer = message.getBuffer();
        synchronized(stack){
            try {
                MsgObject<T> msg = (MsgObject<T>) deserialize(msgBuffer);
                if(msg.getMessage().equals("push")){
                    stack.push(msg.getObject());
                    System.out.println(msg.getObject().toString() + " added to stack");
                }else if(msg.getMessage().equals("pop")){
                    T obj = stack.pop();
                    System.out.println(obj.toString() + " popped from stack");
                }
            } catch (IOException ex) {
                Logger.getLogger(ReplStack.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ReplStack.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void getState(OutputStream out) throws Exception{
        synchronized(stack){
            Util.objectToStream(stack, new DataOutputStream(out));
        }
    }
    
    @Override
    public void setState(InputStream input) throws Exception{
        Stack<T> s, reverseStack;
        reverseStack = new Stack<T>();
        s = (Stack<T>) Util.objectFromStream(new DataInputStream(input));
        while(!s.empty()){
            reverseStack.push(s.pop());
        }
        synchronized(stack){
            while(!reverseStack.empty()){
                T obj = reverseStack.pop();
                stack.push(obj);
                System.out.println(obj.toString() + " added to stack");
            }
        }
    }
    
    protected Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException{
        ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectStream = new ObjectInputStream(byteStream);
        return objectStream.readObject();
    }
    
    protected byte[] serialize(Object obj) throws IOException{
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
        objectStream.writeObject(obj);
        return byteStream.toByteArray();
    }
}
