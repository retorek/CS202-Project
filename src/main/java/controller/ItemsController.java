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

    User user;

    ArrayList<Item> items;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.client = new Client();

        vbox.setPadding(new Insets(20));
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

        // Clear the existing items from the vbox
        vbox.getChildren().clear();

        // Iterate over the fetched items
        for (Item i : this.items) {
            // Create a new VBox for the item
            VBox v = new VBox();

            // Create a Buy button for the item
            Button test = new Button("Buy");
            test.setOnAction(actionEvent -> {
                try {
                    this.client.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    checkItem(i);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // Add the Buy button to the VBox
            v.getChildren().add(test);

            // Create labels for the item name and price, and add them to the VBox
            v.getChildren().add(new Label("Product name: " + i.name));
            v.getChildren().add(new Label("Product price: " + i.price));

            // Create a label for the item availability
            Label label = new Label();
            if (i.isAvailable) {
                label.setText("Product is available.");
                label.setTextFill(Color.color(0, 1, 0));
            } else {
                label.setText("Product is not available.");
                label.setTextFill(Color.color(1, 0, 0));
            }

            // Add the availability label to the VBox
            v.getChildren().add(label);

            // Add a line break to the VBox
            v.getChildren().add(lineBreak());

            // Add the VBox to the vbox
            vbox.getChildren().add(v);
        }
    }

    public void refresh(ActionEvent event){
        for(Object o : vbox.getChildren().toArray()){
            vbox.getChildren().remove(o);
        }
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