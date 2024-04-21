package controller;

import marketplace.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import server.Client;

import java.io.IOException;

public class SignupController {

    @FXML
    TextField firstName;

    @FXML
    TextField lastName;

    @FXML
    TextField username;

    @FXML
    PasswordField password;

    @FXML
    PasswordField confirmPassword;

    @FXML
    Text comment;

    Client client;

    @FXML
    private void register(ActionEvent event) throws IOException {
        String firstName = this.firstName.getText();
        String lastName = this.lastName.getText();
        String username = this.username.getText();
        String password1 = this.password.getText();
        String password2 = this.password.getText();

        if(firstName.isEmpty() | lastName.isEmpty() | username.isEmpty() | password1.isEmpty() | password2.isEmpty()){
            comment.setText("All fields must be filled!");
            comment.setStyle("-fx-text-fill: red");
            return;
        }else if(!password1.equals(password2)){
            comment.setText("Password confirmation is incorrect!");
            comment.setStyle("-fx-text-fill: red");
            return;
        }

        this.client = new Client();

        String result = client.requestAddUser(new User(username, password1, firstName, lastName));
        if(result.equals("Fail")){
            comment.setText("Existent username! Please try another username");
            comment.setStyle("-fx-text-fill: red");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration succeeded");
        alert.setHeaderText(null);

        alert.setContentText("Your registration was successful!");
        alert.showAndWait();

        this.client.close();
        MainController h = new MainController();
        h.changeScene("LoginView.fxml");
    }

}
