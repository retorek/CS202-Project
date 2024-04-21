package marketplace;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public static int idCounter = 0;
    public int id = 0;
    public String username;
    public String password;
    public String firstName;
    public String lastName;
    public ArrayList<Item> itemsPosted = new ArrayList<>();
    public ArrayList<Item> itemsBought = new ArrayList<>();
    public ArrayList<Item> cart;


    private static final String INSERT_USER_SQL = "INSERT INTO Users VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_USER_SQL = "UPDATE Users SET username = ?, password = ?, firstName = ?, lastName = ?, itemsPosted = ?, itemsBought = ? WHERE id = ?";
    private static final String SELECT_USER_SQL = "SELECT * FROM Users WHERE id = ?";
    private static final String SELECT_MAX_ID_SQL = "SELECT MAX(id) FROM Users";

    public static void initializeIdCounter(Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(SELECT_MAX_ID_SQL)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    idCounter = rs.getInt(1);
                }
            }
        }
    }

    public void updateSerializedUser(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.txt"))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User(String username, String password, String firstName, String lastName){
        idCounter += 1;
        this.id += idCounter;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cart = new ArrayList<>();
    }

    public User(){

    }

    public void addToCart(Item item) {
        this.cart.add(item);
    }

    public void checkout(Connection conn) throws SQLException {
        for (Item item : cart) {
            buyItem(item, conn);
        }
        cart.clear();
    }

    public void buyItem(Item item, Connection conn) throws SQLException {
        item.markAsUnavailable(conn);
        this.itemsBought.add(item);
        updateUserInDatabase(conn);
        updateSerializedUser(this);
    }

    public String itemsPostedToString() {
        return this.itemsPosted.stream().map(item -> String.valueOf(item.getId())).collect(Collectors.joining(","));
    }

    public String itemsBoughtToString() {
        return this.itemsBought.stream().map(item -> String.valueOf(item.getId())).collect(Collectors.joining(","));
    }

    public void stringToItemsPosted(String itemsPosted) throws SQLException {
        if(itemsPosted.isEmpty()){
            updateSerializedUser(this);
            return;
        }
        List<Integer> itemIds = Arrays.stream(itemsPosted.split(",")).map(Integer::valueOf).toList();
        this.itemsPosted = new ArrayList<>();
        for (Integer id : itemIds) {
            this.itemsPosted.add(Marketplace.getItemById(id));
        }
        updateSerializedUser(this);
    }

    public void stringToItemsBought(String itemsBought) throws SQLException {
        if(itemsBought.isEmpty()){
            updateSerializedUser(this);
            return;
        }
        List<Integer> itemIds = Arrays.stream(itemsBought.split(",")).map(Integer::valueOf).toList();
        this.itemsBought = new ArrayList<>();
        for (Integer id : itemIds) {
            this.itemsBought.add(Marketplace.getItemById(id));
        }
        updateSerializedUser(this);
    }

    public void updateUserInDatabase(Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_USER_SQL)) {
            pstmt.setString(1, this.username);
            pstmt.setString(2, this.password);
            pstmt.setString(3, this.firstName);
            pstmt.setString(4, this.lastName);
            pstmt.setString(5, this.itemsPostedToString());
            pstmt.setString(6, this.itemsBoughtToString());
            pstmt.setInt(7, id);
            pstmt.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveToDatabase(Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_USER_SQL)) {
            System.out.println(this.id);
            pstmt.setInt(1, id);
            pstmt.setString(2, this.username);
            pstmt.setString(3, this.password);
            pstmt.setString(4, this.firstName);
            pstmt.setString(5, this.lastName);
            pstmt.setString(6, this.itemsPostedToString());
            pstmt.setString(7, this.itemsBoughtToString());
            pstmt.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static User loadFromDatabase(int id, Connection conn) throws SQLException {
        User user = null;
        try (PreparedStatement pstmt = conn.prepareStatement(SELECT_USER_SQL)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.id = rs.getInt("id");
                    user.username = rs.getString("username");
                    user.password = rs.getString("password");
                    user.firstName = rs.getString("firstName");
                    user.lastName = rs.getString("lastName");
                    user.stringToItemsPosted(rs.getString("itemsPosted"));
                    user.stringToItemsBought(rs.getString("itemsBought"));
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }
}