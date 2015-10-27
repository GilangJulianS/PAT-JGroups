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
import java.util.HashSet;
import java.util.Set;
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
public class ReplSet<T> extends ReceiverAdapter{
    
    private final Set<T> set = new HashSet<>();
    private JChannel channel;
    
    public ReplSet() throws Exception{
        channel = new JChannel();
        channel.setReceiver(this);
    }
    
    public ReplSet(String channelName) throws Exception{
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
    
    public boolean add(T object) throws Exception{
        MsgObject<T> mo = new MsgObject<>(object, MsgObject.ADD);
        Message msg = new Message(null, null, serialize(mo));
        channel.send(msg);
        return !set.contains(object);
    }
    
    public boolean contains(T object){
        return set.contains(object);
    }
    
    public boolean remove(T object) throws Exception{
        MsgObject<T> mo = new MsgObject<>(object, MsgObject.REMOVE);
        Message msg = new Message(null, null, serialize(mo));
        channel.send(msg);
        return set.contains(object);
    }
    
     @Override
    public void viewAccepted(View new_view) {
        System.out.println("View: " + new_view);
    }
    
    @Override
    public void receive(Message message) {
        byte[] msgBuffer = message.getBuffer();
        synchronized(set){
            try {
                MsgObject<T> msg = (MsgObject<T>) deserialize(msgBuffer);
                if(msg.getMessage().equals(MsgObject.ADD)){
                    if(!set.contains(msg.getObject())){
                        set.add(msg.getObject());
                        System.out.println(msg.getObject().toString() + " added to set");
                    }else{
                        System.out.println(msg.getObject().toString() + " already in set");
                    }
                    
                }else if(msg.getMessage().equals(MsgObject.REMOVE)){
                    if(set.contains(msg.getObject())){
                        set.remove(msg.getObject());
                        System.out.println(msg.getObject().toString() + " removed from set");
                    }else{
                        System.out.println(msg.getObject().toString() + " not found");
                    }
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
        synchronized(set){
            Util.objectToStream(set, new DataOutputStream(out));
        }
    }
    
    @Override
    public void setState(InputStream input) throws Exception{
        Set<T> s;
        s = (Set<T>) Util.objectFromStream(new DataInputStream(input));
        synchronized(set){
            for(T obj : s){
                set.add(obj);
                System.out.println(obj.toString() + " added to set");
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
