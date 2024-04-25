package server;

import exceptions.ExistentUsernameException;
import exceptions.UserNotFoundException;
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

    public User requestConnection(String request) throws IOException, UserNotFoundException {
        String[] sp = request.split(" ");
        String username = sp[1];
        String pass = sp[2];
        this.out.writeUTF("/connect " + username + " " + pass);
        Object user = null;
        try {
            user = this.ois.readObject();
        } catch (EOFException e) {
            System.err.println("End of stream reached before object could be read");
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if(user == null){
            throw new UserNotFoundException();
        }else{
            System.out.println("User found");
            return (User)user;
        }
    }

    public String requestAddUser(User user) throws IOException, ExistentUsernameException {
        this.out.writeUTF("/addUser");
        this.oos.writeObject(user);
        String result = this.in.readUTF();
        if(result.equals("Failure")){
            throw new ExistentUsernameException();
        }
        return result;
    }

    public void buyItem(User user,Item item) throws IOException {
        this.out.writeUTF("/buy " +user.id +" "+ item.id);
    }

    public void close() throws IOException {
        this.socket.close();
    }

    public ArrayList<Item> searchItems(String searchTerm) {
        try {
            this.out.writeUTF("/search " + searchTerm);
            Object items = this.ois.readObject();
            return (ArrayList<Item>) items;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}