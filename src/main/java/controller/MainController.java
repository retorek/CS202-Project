package controller;

import marketplace.Item;
import marketplace.User;
import server.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

public class MainController extends Application {

    private static Stage stg;

    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException {
        String pageToOpen = "/view/LoginView.fxml";

        //Initialize the database
        Database db = new Database();
        try {
            db.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Initialize th id counters
        try (Connection conn = db.getConnection()) {
            User.initializeIdCounter(conn);
            Item.initializeIdCounter(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        User user = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("user.txt"))) {
            user = (User) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (user == null) {
            user = new User();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.txt"))) {
                oos.writeObject(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(user != null && user.firstName != null){
            pageToOpen = "/view/items-view.fxml";
        }

        this.stg = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource(pageToOpen));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("AlSouk Marketplace");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void changeScene(String fxml) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/view/"+fxml));
        stg.getScene().setRoot(root);
    }
}