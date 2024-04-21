package controller;

import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
    Label comment;

    @FXML
    Button loginButton;

    Client client;

    @FXML
    Hyperlink signUp;

    @FXML
    HBox hBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hBox.setAlignment(Pos.CENTER);
        this.client = new Client();
    }

    public void login(ActionEvent event) throws InterruptedException, IOException {
        String username = this.username.getText();
        String pass = password.getText();

        String req = "/connect " + username + " " + pass;

        User user;

        user = this.client.requestConnection(req);

        if(user == null){
            comment.setText("Wrong Credentials!");
            comment.setStyle("-fx-text-fill: red");
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
    private void signUp(ActionEvent event) throws IOException {
        this.client.close();

        MainController h = new MainController();
        h.changeScene("RegisterView.fxml");
    }
}
