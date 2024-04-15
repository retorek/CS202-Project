package com.example.servertest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class itemsViewController implements Initializable{

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

        StringBuilder sb = new StringBuilder(this.user.firstName + " " + this.user.lastName);
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

        setItems();

    }

    public void setItems(){
        for(Item i : this.items){
            VBox v = new VBox();
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

            v.getChildren().add(test);
            v.getChildren().add(new Label("Product name: " + i.name));
            v.getChildren().add(new Label("Product price: " + i.price));

            Label label = new Label();
            if(i.isAvailable){
                label.setText("Product is available.");
                label.setTextFill(Color.color(0, 1, 0));
            }else{
                label.setText("Product is not available.");
                label.setTextFill(Color.color(1, 0, 0));
            }
            v.getChildren().add(label);

            v.getChildren().add(lineBreak());
            vbox.getChildren().add(v);
        }
    }
    public void refresh(ActionEvent event){
        for(Object o : vbox.getChildren().toArray()){
            vbox.getChildren().remove(o);
        }

        setItems();
    }

    private void checkItem(Item item) throws IOException {

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("item.txt"));
        oos.writeObject(item);

        HelloApplication g = new HelloApplication();
        g.changeScene("item-view.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) throws IOException {

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.txt"));
        oos.writeObject(null);

        this.client.close();

        HelloApplication h = new HelloApplication();
        h.changeScene("login.fxml");
   }

   @FXML
   private void checkProfile(ActionEvent event) throws IOException {
        this.client.close();

        HelloApplication h = new HelloApplication();
        h.changeScene("profile-view.fxml");
   }

    public Region lineBreak(){
        return new Region(){{
            setPrefSize(Double.MAX_VALUE, 20.0);
            setMinHeight(20.0);
        }};
    }
}