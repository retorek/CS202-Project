package com.example.servertest;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread{

    Socket socket;

    ServerThread(Socket s){
        this.socket = s;
    }

    @Override
    public void start() {
        DataInputStream in = null;
        DataOutputStream out = null;
        ObjectOutputStream oos = null;
        try{
             in = new DataInputStream(this.socket.getInputStream());
             out = new DataOutputStream(this.socket.getOutputStream());
             oos = new ObjectOutputStream(out);
        }catch(IOException e){
            e.printStackTrace();
        }
        while(true){
            if(socket.isClosed()){
                break;
            }
            String request;
            try {
                request = in.readUTF();
                if(request.startsWith("/connect")){
                    String[] sp = request.split(" ");
                    String id = sp[1];
                    String pass = sp[2];
                    oos.writeObject(checkUser(id, pass));
                }
                if(request.equals("/items")){
                    oos.writeObject(fetchItems());
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private ArrayList<Item> fetchItems(){
        ArrayList<Item> res = new ArrayList<>();

        res.add(new Item("A", 10, true));
        res.add(new Item("B", 11, false));
        res.add(new Item("C", 12, true));
        res.add(new Item("D", 13, false));
        res.add(new Item("E", 14, true));

        return res;
    }

    private User checkUser(String id, String password){
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("0001", "12345678", "A", "B"));
        users.add(new User("0002", "12345679", "C", "D"));
        users.add(new User("0003", "12345680", "E", "F"));
        users.add(new User("0004", "12345681", "G", "H"));

        for(User u : users){
            if(u.id.equals(id) & u.password.equals(password)){
                return u;
            }
        }
        return null;
    }
}
