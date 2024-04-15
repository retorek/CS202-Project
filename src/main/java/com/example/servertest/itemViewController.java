package com.example.servertest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class itemViewController implements Initializable {

    @FXML
    Label productName;

    @FXML
    Label productPrice;

    @FXML
    Button buyButton;

    @FXML
    TextField textField;

    @FXML
    Label comment;

    @FXML
    Button prev;

    boolean isSet;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        try{
            setItem();
        }catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private void setItem() throws IOException, ClassNotFoundException {

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("item.txt"));

        Object test;

        try {
            test = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Item item = (Item)test;

        System.out.println(item.isAvailable);


        productName.setText(item.name);
        productPrice.setText(String.valueOf(item.price));

        buyButton.setText("Bid");
        buyButton.setStyle("-fx-background-color: #00ff00");

        if(!item.isAvailable){

            buyButton.setDisable(true);
            comment.setText("This product is not available");
            comment.setStyle("-fx-text-fill: red");

            textField.setDisable(true);
        }

        buyButton.setOnAction(actionEvent -> {
            String s = textField.getText();

            int sugg;

            try{
                sugg = Integer.parseInt(s);
                isSet = true;
            }catch (NumberFormatException e){
                comment.setText("Not a valid bid: numerical value required");
                comment.setStyle("-fx-text-fill: red");
                isSet = false;
            }

            if(isSet){
                comment.setText("Bid placed");
                comment.setStyle("-fx-text-fill: green");
            }

        });
    }

    public void previous(ActionEvent event) throws IOException {
        HelloApplication h = new HelloApplication();
        h.changeScene("items-view.fxml");
    }

}