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
        ObjectInputStream ois = null;
        try{
             in = new DataInputStream(this.socket.getInputStream());
             out = new DataOutputStream(this.socket.getOutputStream());
             oos = new ObjectOutputStream(out);
             ois = new ObjectInputStream(in);
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
                if(request.startsWith("/item ")){
                    String[] sp = request.split(" ");
                    String id = sp[1];
                    oos.writeObject(checkItem(id));
                }
                if(request.equals("/addUser")){
                    User user = (User)ois.readObject();
                    out.writeUTF(userExist(user));
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ArrayList<Item> fetchItems(){
        ArrayList<Item> res = new ArrayList<>();

        res.add(new Item("A", "I00A", 10, true));
        res.add(new Item("B", "I00B", 11, false));
        res.add(new Item("C", "I00C", 12, true));
        res.add(new Item("D", "I00D", 13, false));
        res.add(new Item("E", "I00E",14, true));

        return res;
    }

    private Item checkItem(String id){
        ArrayList<Item> items = fetchItems();
        for(Item item : items){
            if(item.id.equals(id)){
                return item;
            }
        }
        return null;
    }

    private User checkUser(String id, String password){
        ArrayList<User> users = new ArrayList<>();

        User user = new User("0001", "12345678", "A", "B");
        user.itemsBought.add(new Item("test1", "I001",25, false));
        user.itemsBought.add(new Item("test2", "I002",98, false));
        user.itemsBought.add(new Item("test3", "I003",54, false));
        user.itemsBought.add(new Item("test4", "I004",75, false));

        user.itemsPosted.add(new Item("test21", "I011", 101, false));
        user.itemsPosted.add(new Item("test22", "I012", 43, true));
        user.itemsPosted.add(new Item("test23", "I013", 32, false));
        user.itemsPosted.add(new Item("test24", "I014", 60, true));
        users.add(user);

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

    private String userExist(User user){
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("", "", "", ""));
        for(User u : users){
            if(u.id.equals(user.id)){
                return "Fail";
            }
        }
        return "Success";
    }
}
