package server;

import marketplace.Item;
import marketplace.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements Serializable {

    private DataInputStream in;
    private DataOutputStream out;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private Socket socket;

    public Client(){
        this.in = null;
        this.out = null;
        this.ois = null;
        this.oos = null;

        try{
            this.socket = new Socket("localhost", 8880);
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

    public Item requestItem(int id) throws IOException {
        this.out.writeUTF("/item " + id);

        Object test = null;
        try{
            test = this.ois.readObject();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return (Item)test;
    }

    public User requestConnection(String request) throws IOException {
        String[] sp = request.split(" ");
        String username = sp[1];
        String pass = sp[2];
        this.out.writeUTF("/connect " + username + " " + pass);
        Object test = null;
        try {
            test = this.ois.readObject();
        } catch (EOFException e) {
            System.err.println("End of stream reached before object could be read");
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if(test == null){
            return null;
        }else{
            return (User)test;
        }
    }

    public String requestAddUser(User user) throws IOException {
        this.out.writeUTF("/addUser");
        this.oos.writeObject(user);
        String result = this.in.readUTF();
        return result;
    }

    public void buyItem(User user,Item item) throws IOException {
        this.out.writeUTF("/buy " +user.id +" "+ item.id);
    }

    public void close() throws IOException {
        this.socket.close();
    }

}