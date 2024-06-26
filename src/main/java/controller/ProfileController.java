package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
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
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Arrays;
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
    private TableColumn<Item, String> itemTypeColumn;
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
    @FXML
    private ChoiceBox<String> itemTypeField;
    @FXML
    private Button browseImageButton;
    @FXML
    private Label wrongField;

    private byte[] image;

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
        itemTypeField.getItems().add("Electronics");
        itemTypeField.getItems().add("Sport");
        itemTypeField.getItems().add("Home");

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
        itemTypeColumn.setCellValueFactory(new PropertyValueFactory<Item, String>("type"));
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
    private void browseImage() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                byte[] image = Files.readAllBytes(file.toPath());
                // Store the image data somewhere to be used when creating the Item
                this.browseImageButton.setText(file.getName());
                this.image = image;
                System.out.println("Image loaded");
                System.out.println(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void addItem() {
        // Get the input values
        String name = itemNameField.getText();
        String description = itemDescriptionField.getText();
        String type = itemTypeField.getValue();
        double price = Double.parseDouble(itemPriceField.getText());
        System.out.println("Image: " + image);
        // Create a new Item object
        Item newItem = new Item(name, description, type,price, image);

        // Add the new item to the marketplace
        try {
            db.connect();
            Marketplace.addItem(newItem, user, db.getConnection());
            // Refresh the items list
            updateItemsPostedTable();
        } catch (SQLException e) {
            wrongField.setText("Please fill all the fields");
            wrongField.setVisible(true);
            wrongField.setStyle("-fx-text-fill: red");
            e.printStackTrace();
        }

        // Clear the input fields
        itemNameField.clear();
        itemDescriptionField.clear();
        itemPriceField.clear();
        itemTypeField.setValue(null);
        browseImageButton.setText("Browse");

        wrongField.setText("Item added successfully");
        wrongField.setVisible(true);
        wrongField.setStyle("-fx-text-fill: green");

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
