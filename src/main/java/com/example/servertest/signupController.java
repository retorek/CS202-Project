package com.example.servertest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class signupController {

    @FXML
    TextField firstNameField;

    @FXML
    TextField lastNameField;

    @FXML
    TextField usernameField;

    @FXML
    PasswordField passwordField1;

    @FXML
    PasswordField passwordField2;

    @FXML
    Label commentLabel;

    Client client;

    @FXML
    private void register(ActionEvent event) throws IOException, InterruptedException {
        String firstName = this.firstNameField.getText();
        String lastName = this.lastNameField.getText();
        String username = this.usernameField.getText();
        String password1 = this.passwordField1.getText();
        String password2 = this.passwordField2.getText();

        if(firstName.isEmpty() | lastName.isEmpty() | username.isEmpty() | password1.isEmpty() | password2.isEmpty()){
            commentLabel.setText("All fields must be filled!");
            commentLabel.setStyle("-fx-text-fill: red");
            return;
        }else if(!password1.equals(password2)){
            commentLabel.setText("Password confirmation is incorrect!");
            commentLabel.setStyle("-fx-text-fill: red");
            return;
        }

        this.client = new Client();

        String result = client.requestAddUser(new User(username, password1, firstName, lastName));
        if(result.equals("Fail")){
            commentLabel.setText("Existent username! Please try another username");
            commentLabel.setStyle("-fx-text-fill: red");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration succeeded");
        alert.setHeaderText(null);

        alert.setContentText("Your registration was successful!");
        alert.showAndWait();


        this.client.close();
        HelloApplication h = new HelloApplication();
        h.changeScene("login.fxml");
    }

}
