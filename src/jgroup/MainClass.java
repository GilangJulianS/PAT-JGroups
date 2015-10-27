/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgroup;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gilang
 */
public class MainClass {
    
    public static void main(String[] args){
        String input;
        Scanner s = new Scanner(System.in);
        System.out.println("1. stack");
        System.out.println("2. set");
        input = s.nextLine();
        if(input.equals("1")){
            try {
                stackLoop(s);
            } catch (Exception ex) {
                Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else if(input.equals("2")){
            try{
                setLoop(s);
            }catch(Exception ex){
                Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void setLoop(Scanner s) throws Exception{
        String input;
        System.out.println("Input channel name:");
        input = s.nextLine();
        ReplSet<String> set = new ReplSet<>(input);
        input = "";
        while(!input.equals("4")){
            System.out.println("1. add");
            System.out.println("2. remove");
            System.out.println("3. contains");
            System.out.println("4. exit");
            input = s.nextLine();
            if(input.equals("1")){
                System.out.println("Item to add:");
                input = s.nextLine();
                set.add(input);
            }else if(input.equals("2")){
                System.out.println("Item to remove:");
                input = s.nextLine();
                set.remove(input);
            }else if(input.equals("3")){
                System.out.println("Search item:");
                input = s.nextLine();
                if(set.contains(input)){
                    System.out.println(input + " found");
                }else{
                    System.out.println(input + " not found");
                }
            }
        }
        set.close();
    }
    
    public static void stackLoop(Scanner s) throws Exception{
        String input;
        System.out.println("Input channel name:");
        input = s.nextLine();
        ReplStack<String> stack = new ReplStack<>(input);
        input = "";
        while(!input.equals("4")){
            System.out.println("1. push");
            System.out.println("2. pop");
            System.out.println("3. top");
            System.out.println("4. exit");
            input = s.nextLine();
            if(input.equals("1")){
                System.out.println("Item to push:");
                input = s.nextLine();
                stack.push(input);
            }else if(input.equals("2")){
                System.out.println("Item popped: " + stack.pop());
            }else if(input.equals("3")){
                System.out.println("Top item: " + stack.top());
            }
        }
        stack.close();
    }
}
