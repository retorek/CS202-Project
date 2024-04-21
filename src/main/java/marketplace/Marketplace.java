package marketplace;

import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Marketplace {
    public static ArrayList<Item> items=new ArrayList<>();
    public static ArrayList<User> users=new ArrayList<>();
    public static Connection conn;

    private static final String SELECT_ALL_ITEMS_SQL = "SELECT * FROM Items";
    private static final String SELECT_ALL_USERS_SQL = "SELECT * FROM Users";

    public Marketplace(Connection conn) throws SQLException {
        Marketplace.conn = conn;
        items = loadAllItems(conn);
        users = loadAllUsers(conn);
        User.idCounter = users.size();
        Item.idCounter = items.size();
    }

    public static ArrayList<Item> loadAllItems(Connection conn) throws SQLException {
        ArrayList<Item> items = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_ITEMS_SQL)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Item item = Item.loadFromDatabase(rs.getInt("id"), conn);
                    if (item != null) {
                        items.add(item);
                    }
                }
            }
        }
        return items;
    }

    public static ArrayList<User> loadAllUsers(Connection conn) throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_USERS_SQL)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = User.loadFromDatabase(rs.getInt("id"), conn);
                    if (user != null) {
                        users.add(user);
                    }
                }
            }
        }
        return users;
    }

    public static void addItem(Item item,User user, Connection conn) throws SQLException {
        items.add(item);
        item.saveToDatabase(conn);

        user.itemsPosted.add(item);
        user.updateUserInDatabase(conn);
        user.updateSerializedUser(user);
    }

    public static void addUser(User user, Connection conn) throws SQLException {
        users.add(user);
        user.saveToDatabase(conn);
    }

    public static ArrayList<Item> getAvailableItems() throws SQLException {
        items = loadAllItems(conn);
        ArrayList<Item> availableItems = new ArrayList<>();
        for (Item item : items) {
            if (item.isAvailable) {
                availableItems.add(item);
            }
        }
        return availableItems;
    }

    public static ArrayList<Item> searchItems(String searchTerm) throws SQLException {
        items = loadAllItems(conn);
        ArrayList<Item> searchResults = new ArrayList<>();
        System.out.println("Search term: " + searchTerm);
        for (Item item : items) {
            if (item.name.contains(searchTerm) || item.description.contains(searchTerm)) {
                if(item.isAvailable){
                    searchResults.add(item);
                    System.out.println("Item found: " + item.name);
                }
            }
        }
        System.out.println(searchResults.size());
        return searchResults;
    }

    public static User getUserById(int id) {
        for (User user : users) {
            if (user.id == id) {
                return user;
            }
        }
        return null;
    }

    public static User getUserByUsername(String username) throws SQLException {
        users = loadAllUsers(conn);
        for (User user : users) {
            if (user.username.equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static Item getItemById(int id) throws SQLException {
        items=loadAllItems(conn);
        for (Item item : items) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }
}