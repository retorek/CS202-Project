package controller;

import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import marketplace.User;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import server.Client;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;

public class LoginController implements Initializable{

    @FXML
    TextField username;

    @FXML
    PasswordField password;

    @FXML
    Text comment;

    @FXML
    Button loginButton;

    Client client;

    @FXML
    Label signUp;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.client = new Client();
    }

    public void login(ActionEvent event) throws InterruptedException, IOException {
        String username = this.username.getText();
        String pass = password.getText();

        String req = "/connect " + username + " " + pass;

        User user;

        user = this.client.requestConnection(req);

        if(user == null){
            comment.setStyle("-fx-text-fill: red");
            comment.setText("Wrong credentials");
        }else{
            comment.setStyle("-fx-text-fill: green");
            comment.setText("Connected Successfully, Welcome "+ user.firstName + " " + user.lastName);

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.txt"));
            oos.writeObject(user);

            this.client.close();
            MainController g = new MainController();
            g.changeScene("items-view.fxml");
        }
    }

    @FXML
    private void signUp(MouseEvent event) throws IOException {
        this.client.close();

        MainController h = new MainController();
        h.changeScene("RegisterView.fxml");
    }
}
