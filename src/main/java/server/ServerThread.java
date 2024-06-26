package server;

import exceptions.ExistentUsernameException;
import exceptions.ItemNotAvailableException;
import exceptions.UserNotFoundException;
import marketplace.Item;
import marketplace.User;
import marketplace.Marketplace;
import server.Database;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class ServerThread extends Thread {

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Database db;

    ServerThread(Socket s, Database db) {
        this.socket = s;
        this.db = db;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(this.socket.getInputStream());
            DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
            oos = new ObjectOutputStream(out);
            ois = new ObjectInputStream(in);
            while (true) {
                if (socket.isClosed()) {
                    break;
                }
                String request = in.readUTF();
                if (request.startsWith("/connect")) {
                    String[] sp = request.split(" ");
                    String username = sp[1];
                    String pass = sp[2];
                    oos.writeObject(checkUser(username, pass));
                }
                if (request.startsWith("/search ")) {
                    String[] args = request.split(" ");
                    if(args.length < 2){
                        oos.writeObject(Marketplace.getAvailableItems());
                        continue;
                    }else{
                        String searchTerm = args[1];
                        ArrayList<Item> items = Marketplace.searchItems(searchTerm);
                        // send items back to client
                        oos.writeObject(items);
                    }
                }
                if (request.equals("/items")) {
                    oos.writeObject(Marketplace.getAvailableItems());
                }
                if (request.startsWith("/item ")) {
                    String[] sp = request.split(" ");
                    String id = sp[1];
                    oos.writeObject(checkItem(id));
                }
                if (request.equals("/addUser")) {
                    User user = (User) ois.readObject();
                    out.writeUTF(addUser(user));
                }
                if (request.startsWith("/item ")) {
                    String[] sp = request.split(" ");
                    String id = sp[1];
                    oos.writeObject(checkItem(id));
                }
                if (request.startsWith("/buy ")) {
                    String[] sp = request.split(" ");
                    String userId = sp[1];
                    String itemId = sp[2];
                    buyItem(userId,itemId);
                }
            }
        } catch (EOFException e) {
            //System.err.println("Client closed connection: " + e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (SQLException | ItemNotAvailableException | ExistentUsernameException e) {
            throw new RuntimeException(e);
        }
    }

    private Item checkItem(String id) throws SQLException {
        for (Item item : Marketplace.getAvailableItems()) {
            if (item.id == Integer.parseInt(id)) {
                return item;
            }
        }
        return null;
    }

    private void buyItem(String userId, String itemId) throws ItemNotAvailableException {
        try {
            User user = Marketplace.getUserById(Integer.parseInt(userId));
            assert user != null;
            Item item = Objects.requireNonNull(Marketplace.getItemById(Integer.parseInt(itemId)));
            if(!item.isAvailable){
                throw new ItemNotAvailableException();
            }
            user.buyItem(item, db.getConnection());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String addUser(User user) throws ExistentUsernameException {
        try {
            if(Marketplace.getUserByUsername(user.username) != null){
                throw new ExistentUsernameException();
            }
            Marketplace.addUser(user, db.getConnection());
            return "Success";
        } catch (SQLException e) {
            return "Failure";
        }
    }


    private User checkUser(String username, String password) throws SQLException {
        User user = Marketplace.getUserByUsername(username);
        if (user != null && user.password.equals(password)) {
            System.out.println("User found");
            return user;
        }
        return null;
    }
}