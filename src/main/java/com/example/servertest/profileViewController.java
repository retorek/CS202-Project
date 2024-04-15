package com.example.servertest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class profileViewController implements Initializable {

    private User user;

    @FXML
    Button homeButton;

    @FXML
    Button logOutButton;

    @FXML
    ScrollPane scroll;
    @FXML
    VBox vbox;

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

        this.vbox.setPadding(new Insets(20));

        setInformation();
    }

    private void setInformation(){

        StringBuilder stringBuilder;

        this.vbox.getChildren().add(new Label("Name:\t" +this.user.firstName + " " + this.user.lastName));
        this.vbox.getChildren().add(lineBreak(20));

        this.vbox.getChildren().add(new Label("Username:\t" + this.user.id));
        this.vbox.getChildren().add(lineBreak(20));

        this.vbox.getChildren().add(new Label("Password:    ****************"));
        this.vbox.getChildren().add(lineBreak(20));

        Button button = new Button();
        button.setText("Change Password");
        button.setStyle("-fx-background-color: #5c9eda");
        this.vbox.getChildren().add(button);
        this.vbox.getChildren().add(lineBreak(20));

        this.vbox.getChildren().add(new Label("Items bought:\t"));
        this.vbox.getChildren().add(lineBreak(10));

        if(this.user.itemsBought.isEmpty()){
            Label label = new Label();

            label.setText("\tYou haven't posted anything.");
            label.setStyle("-fx-text-fill: red");

            this.vbox.getChildren().add(label);
            this.vbox.getChildren().add(lineBreak(10));
        }else{
            for(Item item : this.user.itemsBought){
                Label label = new Label();

                stringBuilder = new StringBuilder("\tItem name:\t" + item.name +
                                                ", Price:\t" + item.price);
                label.setText(stringBuilder.toString());

                this.vbox.getChildren().add(label);
                this.vbox.getChildren().add(lineBreak(10));
            }
        }
        this.vbox.getChildren().add(lineBreak(10));

        this.vbox.getChildren().add(new Label("Items posted:\t"));
        this.vbox.getChildren().add(lineBreak(10));

        if(this.user.itemsPosted.isEmpty()){
            Label label = new Label();

            label.setText("\tYou haven't bought anything.");
            label.setStyle("-fx-text-fill: red");

            this.vbox.getChildren().add(label);
            this.vbox.getChildren().add(lineBreak(10));
        }else{
            for(Item item : this.user.itemsPosted){
                Label label = new Label();

                stringBuilder = new StringBuilder("\tItem name:\t" + item.name +
                                                ", Price:\t" + item.price);
                label.setText(stringBuilder.toString());

                this.vbox.getChildren().add(label);
                this.vbox.getChildren().add(lineBreak(10));
            }
        }
        this.vbox.getChildren().add(lineBreak(10));
    }
    @FXML
    private void goToHomepage(ActionEvent event) throws IOException {
        HelloApplication h = new HelloApplication();
        h.changeScene("items-view.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.txt"));
        oos.writeObject(null);

        HelloApplication h = new HelloApplication();
        h.changeScene("login.fxml");
    }

    public Region lineBreak(double size){
        return new Region(){{
            setPrefSize(Double.MAX_VALUE, size);
            setMinHeight(size);
        }};
    }
}
