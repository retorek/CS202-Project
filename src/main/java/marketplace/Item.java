package marketplace;

import java.io.Serializable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    public static int idCounter = 0;
    public  int id = 0;
    public String name;
    public String description;
    public String type;
    public double price;
    public boolean isAvailable;
    public byte[] image;





    private static final String INSERT_ITEM_SQL = "INSERT INTO Items (id, name, description, type, price, isAvailable, image) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ITEM_SQL = "SELECT * FROM Items WHERE id = ?";
    private static final String UPDATE_ITEM_AVAILABILITY_SQL = "UPDATE Items SET isAvailable = ? WHERE id = ?";
    private static final String SELECT_MAX_ID_SQL = "SELECT MAX(id) FROM Items";

    public static void initializeIdCounter(Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(SELECT_MAX_ID_SQL)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    idCounter = rs.getInt(1);
                }
            }
        }
    }

    public Item(String name, String description, String type, double price, byte[] image) {
        idCounter += 1;
        this.id = idCounter;
        this.name = name;
        this.description = description;
        this.type = type;
        this.price = price;
        this.isAvailable = true;
        this.image = image;
    }

    public Item() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public byte[] getImage() {
        return image;
    }

    public String isSold(){
        return isAvailable ? "No" : "Yes";
    }

    public void setImage(byte[] image) {
        this.image = image;
    }


    public void saveToDatabase(Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_ITEM_SQL)) {
            pstmt.setInt(1, this.id);
            pstmt.setString(2, this.name);
            pstmt.setString(3, this.description);
            pstmt.setString(4, this.type);
            pstmt.setDouble(5, this.price);
            pstmt.setBoolean(6, this.isAvailable);
            pstmt.setBytes(7, this.image);
            pstmt.executeUpdate();
        }
    }

    public static Item loadFromDatabase(int id, Connection conn) throws SQLException {
        Item item = null;
        try (PreparedStatement pstmt = conn.prepareStatement(SELECT_ITEM_SQL)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    item = new Item();
                    item.id = rs.getInt("id");
                    item.name = rs.getString("name");
                    item.description = rs.getString("description");
                    item.type = rs.getString("type");
                    item.price = rs.getDouble("price");
                    item.isAvailable = rs.getBoolean("isAvailable");
                    item.image = rs.getBytes("image");
                }
            }
        }
        return item;
    }

    public void markAsUnavailable(Connection conn) throws SQLException {
        this.isAvailable = false;
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_ITEM_AVAILABILITY_SQL)) {
            pstmt.setBoolean(1, this.isAvailable);
            pstmt.setInt(2, this.id);
            pstmt.executeUpdate();
        }
    }
}