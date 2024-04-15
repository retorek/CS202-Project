package com.example.servertest;

import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;

public class loginController implements Initializable{

    @FXML
    TextField idField;

    @FXML
    PasswordField passwordField;

    @FXML
    Label comment;

    @FXML
    Button login;

    Client client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.client = new Client();
    }

    public void login(ActionEvent event) throws InterruptedException, IOException {
        String id = idField.getText();
        String pass = passwordField.getText();

        StringBuilder sb = new StringBuilder("/connect " + id + " " + pass);
        String req = sb.toString();

        User user;

        try {
            user = this.client.requestConnection(req);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if(user == null){
            comment.setStyle("-fx-text-fill: red");
            comment.setText("Wrong credentials");
        }else{
            comment.setStyle("-fx-text-fill: green");
            comment.setText("Connected Successfully, Welcome "+ user.firstName + " " + user.lastName);

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.txt"));
            oos.writeObject(user);

            this.client.close();
            HelloApplication g = new HelloApplication();
            g.changeScene("items-view.fxml");
        }
    }
}
