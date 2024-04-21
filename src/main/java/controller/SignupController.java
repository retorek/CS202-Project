package controller;

import exceptions.ExistentUsernameException;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import marketplace.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import server.Client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignupController implements Initializable {

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
    Label wrongField;

    Client client;

    @FXML
    HBox hBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hBox.setAlignment(Pos.CENTER);
    }

    @FXML
    private void register(ActionEvent event) throws IOException {
        String firstName = this.firstName.getText();
        String lastName = this.lastName.getText();
        String username = this.username.getText();
        String password1 = this.password.getText();
        String password2 = this.confirmPassword.getText();

        if(firstName.isEmpty() | lastName.isEmpty() | username.isEmpty() | password1.isEmpty() | password2.isEmpty()){
            wrongField.setText("All fields must be filled!");
            wrongField.setStyle("-fx-text-fill: red");
            return;
        }

        if(!password1.equals(password2)){
            wrongField.setText("Password confirmation is incorrect!");
            wrongField.setStyle("-fx-text-fill: red");
            return;
        }

        this.client = new Client();

        try {
            client.requestAddUser(new User(username, password1, firstName, lastName));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration succeeded");
            alert.setHeaderText(null);
            alert.setContentText("Your registration was successful!");
            alert.showAndWait();
            this.client.close();
            MainController h = new MainController();
            h.changeScene("LoginView.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExistentUsernameException e) {
            wrongField.setText(e.getMessage());
            wrongField.setStyle("-fx-text-fill: red");
        }
    }

}
