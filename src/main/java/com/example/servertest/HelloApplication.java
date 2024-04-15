package com.example.servertest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class HelloApplication extends Application {


    private static Stage stg;

    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException {

        String pageToOpen = "login.fxml";

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("user.txt"));
        Object test = ois.readObject();

        if(test != null){
            pageToOpen = "items-view.fxml";
        }

        this.stg = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(pageToOpen));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void changeScene(String fxml) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        stg.getScene().setRoot(root);
    }
}