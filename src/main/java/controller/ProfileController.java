package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.control.cell.PropertyValueFactory;
import marketplace.Item;
import marketplace.Marketplace;
import marketplace.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import server.Database;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    private User user;
    private Database db = new Database();

    @FXML
    Button homeButton;

    @FXML
    Button logOutButton;

    @FXML
    ScrollPane scroll;
    @FXML
    VBox vbox;

    // Profile information
    @FXML
    Text firstName;
    @FXML
    Text lastName;
    @FXML
    Text username;
    @FXML
    Text password;

    //Order Logs
    @FXML
    private TableView<Item> orderLogTable;
    @FXML
    private TableColumn<Item, String> orderIdColumn;
    @FXML
    private TableColumn<Item, String> orderNameColumn;
    @FXML
    private TableColumn<Item, String> orderDescriptionColumn;
    @FXML
    private TableColumn<Item, Double> orderPriceColumn;

    //Items Posted
    @FXML
    private TableView<Item> itemsPostedTable;
    @FXML
    private TableColumn<Item, String> itemIdColumn;
    @FXML
    private TableColumn<Item, String> itemNameColumn;
    @FXML
    private TableColumn<Item, String> itemDescriptionColumn;
    @FXML
    private TableColumn<Item, Double> itemPriceColumn;
    @FXML
    private TableColumn<Item, String> itemSoldColumn;

    //Add Item
    @FXML
    private TextField itemNameField;
    @FXML
    private TextField itemDescriptionField;
    @FXML
    private TextField itemPriceField;
    @FXML
    private Button addItemButton;

    public ProfileController() throws SQLException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("user.txt"));
            this.user = (User)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        homeButton.setStyle("-fx-background-color: #5c9eda");
        logOutButton.setStyle("-fx-background-color: #5c9eda");

        setProfileInformation();
        initializeOrderLogTable();
        initializeItemsPostedTable();

    }

    private void setProfileInformation(){
        firstName.setText(user.firstName);
        lastName.setText(user.lastName);
        username.setText(user.username);
        password.setText("*********");
    }

    private void initializeOrderLogTable() {
        // Set up the columns in the table
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        orderDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        orderPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Load the data
        updateOrderLogTable();
    }

    private void initializeItemsPostedTable() {
        // Set up the columns in the table
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<Item, String>("id"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<Item, String>("name"));
        itemDescriptionColumn.setCellValueFactory(new PropertyValueFactory<Item, String>("description"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<Item, Double>("price"));
        itemSoldColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isSold()));
        // Load the data
        updateItemsPostedTable();
    }

    private void updateOrderLogTable() {
        // Get the data for the table
        ObservableList<Item> data = FXCollections.observableArrayList(user.itemsBought);

        // Assign the data to the table
        orderLogTable.setItems(data);
    }

    private void updateItemsPostedTable() {
        // Get the data for the table
        ObservableList<Item> data = FXCollections.observableArrayList(user.itemsPosted);

        // Assign the data to the table
        itemsPostedTable.setItems(data);
    }

    @FXML
    private void addItem() {
        // Get the input values
        String name = itemNameField.getText();
        String description = itemDescriptionField.getText();
        double price = Double.parseDouble(itemPriceField.getText());

        // Create a new Item object
        Item newItem = new Item(name, description, price);

        // Add the new item to the marketplace
        try {
            db.connect();
            Marketplace.addItem(newItem, user, db.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Clear the input fields
        itemNameField.clear();
        itemDescriptionField.clear();
        itemPriceField.clear();
    }

    @FXML
    private void goToHomepage(ActionEvent event) throws IOException {
        MainController h = new MainController();
        h.changeScene("items-view.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.txt"));
        oos.writeObject(null);

        MainController h = new MainController();
        h.changeScene("LoginView.fxml");
    }
}
