package com.example.servertest;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements Serializable {


    private DataInputStream in;
    private DataOutputStream out;

    private ObjectInputStream ois;

    private Socket socket;

    Client(){
        this.in = null;
        this.out = null;
        this.ois = null;

        try{
            this.socket = new Socket("localhost", 8888);
        }catch(IOException e){
            e.printStackTrace();
        }

        try{
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(in);
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

    public User requestConnection(String request) throws IOException, ClassNotFoundException {
        this.out.writeUTF(request);
        Object test = this.ois.readObject();

        if(test == null){
            return null;
        }else{
            return (User)test;
        }
    }

    public void close() throws IOException {
        this.socket.close();
    }
}
