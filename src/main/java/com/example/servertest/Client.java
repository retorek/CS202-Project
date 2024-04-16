package com.example.servertest;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements Serializable {


    private DataInputStream in;
    private DataOutputStream out;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private Socket socket;

    Client(){
        this.in = null;
        this.out = null;
        this.ois = null;
        this.oos = null;

        try{
            this.socket = new Socket("localhost", 8888);
        }catch(IOException e){
            e.printStackTrace();
        }

        try{
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(in);
            this.oos = new ObjectOutputStream(out);
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public ArrayList<Item> requestItems() throws IOException {
        this.out.writeUTF("/items");

        Object test;
        try {
            test = this.ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return ((ArrayList<Item>) test);
    }

    public Item requestItem(String id) throws IOException {
        this.out.writeUTF("/item " + id);

        Object test = null;
        try{
            test = this.ois.readObject();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return (Item)test;
    }

    public User requestConnection(String request) throws IOException, ClassNotFoundException {
        this.out.writeUTF(request);
        Object test = this.ois.readObject();

        if(test == null){
            return null;
        }else{
            return (User)test;
        }
    }

    public String requestAddUser(User user) throws IOException, InterruptedException {
        this.out.writeUTF("/addUser");
        Thread.sleep(1000);
        this.oos.writeObject(user);
        String result = this.in.readUTF();
        return result;
    }

    public void close() throws IOException {
        this.socket.close();
    }
}
