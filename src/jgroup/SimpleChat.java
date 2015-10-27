/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgroup;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

/**
 *
 * @author gilang
 */
public class SimpleChat extends ReceiverAdapter{
    JChannel channel;
    
    private final List<String> state = new ArrayList<>();
    
    private void start() throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect("channel1");
        channel.getState(null, 0);
        loop();
        channel.close();
    }
    
    private void loop() throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            System.out.print("> "); System.out.flush();
            String line = reader.readLine().toLowerCase();
            if(line.equals("quit")){
                break;
            }
            Message msg = new Message(null, null, line);
            channel.send(msg);
        }
    }
    
    @Override
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    @Override
    public void receive(Message msg) {
        String line = msg.getSrc() + ": " + msg.getObject();
        System.out.println(line);
        synchronized(state){
            state.add(line);
        }
    }
    
    @Override
    public void getState(OutputStream out) throws Exception{
        synchronized(state){
            Util.objectToStream(state, new DataOutputStream(out));
        }
    }
    
    @Override
    public void setState(InputStream input) throws Exception{
        List<String> list;
        list = (List<String>)Util.objectFromStream(new DataInputStream(input));
        synchronized(state){
            state.clear();
            state.addAll(list);
        }
        System.out.println(state.size() + " Unread message");
        for(String s : state){
            System.out.println(s);
        }
    }
    
    public static void main(String[] args)throws Exception{
        new SimpleChat().start();
    }
    
}
