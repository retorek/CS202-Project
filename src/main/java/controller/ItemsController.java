package controller;

import marketplace.Item;
import marketplace.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import server.Client;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class ItemsController implements Initializable{

    Client client;

    @FXML
    Button updateButton;

    @FXML
    AnchorPane pane;

    @FXML
    VBox vbox;

    @FXML
    ScrollPane scroll;

    @FXML
    Button credentials;
    
    @FXML
    Button logOutButton;

    @FXML
    TextField searchField;

    @FXML
    Button searchButton;

    @FXML
    GridPane grid;

    User user;

    ArrayList<Item> items;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.client = new Client();
        updateButton.setText("Update");

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("user.txt"));
            this.user = (User)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        StringBuilder sb = new StringBuilder(this.user.username);
        credentials.setText(sb.toString());
        credentials.setStyle("-fx-text-fill: #5c9eda");
        credentials.setStyle("-fx-background-color: white");

        updateButton.setStyle("-fx-background-color: #5c9eda");
        logOutButton.setStyle("-fx-background-color: #5c9eda");
        searchButton.setStyle("-fx-background-color: #5c9eda");

        try {
            this.items = this.client.requestItems();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        loadItems();
    }

    private void loadItems() {
        // Fetch the items from the server
        try {
            this.items = this.client.requestItems();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Append the items to the vbox
        appendItems();
    }

    private void appendItems(){
        // Clear the existing items from the grid
        grid.getChildren().clear();

        // Set the horizontal and vertical gaps between the cells
        grid.setHgap(60);
        grid.setVgap(20);

        // Iterate over the fetched items
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            // Create a new VBox for the item
            VBox v = new VBox();

            // Set the padding around the edges of the VBox
            v.setPadding(new Insets(10));

            // Set the vertical spacing between the children of the VBox
            v.setSpacing(2);

            // Create a new ImageView for each item
            ImageView imageView = new ImageView();
            InputStream is = new ByteArrayInputStream(item.getImage());
            Image image = new Image(is);
            imageView.setImage(image);
            imageView.setFitHeight(100); // Set the height
            imageView.setFitWidth(100);  // Set the width

            // Add the ImageView to the VBox
            v.getChildren().add(imageView);

            // Create labels for the item name, price, and type, and add them to the VBox
            v.getChildren().add(new Label("Product name: " + item.name));
            v.getChildren().add(new Label("Product price: " + item.price));
            v.getChildren().add(new Label("Product type: " + item.type));

            // Add the buy button to each item
            Button buyButton = new Button("Buy");
            buyButton.setOnAction(actionEvent -> {
                try {
                    // Save the item to a file
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("item.txt"));
                    oos.writeObject(item);

                    // Change the scene to the item view
                    MainController h = new MainController();
                    h.changeScene("item-view.fxml");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    System.out.println("Error buying item");
                }
            });
            v.getChildren().add(buyButton);

            // Add the VBox to the grid
            grid.add(v, i % 3, i / 3);
        }
    }


    public void searchItems(ActionEvent event) {
        String searchTerm = searchField.getText();
        this.items = this.client.searchItems(searchTerm);
        appendItems();
    }

    public void refresh(ActionEvent event){
        // Clear the grid
        grid.getChildren().clear();

        // Load the items
        loadItems();

        System.out.println("Refreshed");
        System.out.println("Items: " + items.size());
    }

    private void checkItem(Item item) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("item.txt"));
        oos.writeObject(item);

        this.client.close();

        MainController g = new MainController();
        g.changeScene("item-view.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) throws IOException {

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.txt"));
        oos.writeObject(null);

        this.client.close();

        MainController h = new MainController();
        h.changeScene("LoginView.fxml");
    }

   @FXML
   private void checkProfile(ActionEvent event) throws IOException {
       // Refresh the items list
       refresh(event);

       this.client.close();

        MainController h = new MainController();
        h.changeScene("profile-view.fxml");
   }

    public Region lineBreak(){
        return new Region(){{
            setPrefSize(Double.MAX_VALUE, 20.0);
            setMinHeight(20.0);
        }};
    }
}