package controller;

import exceptions.ItemNotAvailableException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import marketplace.Item;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import marketplace.User;
import server.Client;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ItemController implements Initializable {
    private User user;

    @FXML
    Label productName;

    @FXML
    Label productPrice;

    @FXML
    Label productDescription;

    @FXML
    Button buyButton;

    @FXML
    Label comment;

    @FXML
    Button prev;

    @FXML
    private ImageView itemImage;

    Client client;

    Item item;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.client = new Client();

        try {
            ObjectInputStream userOis = new ObjectInputStream(new FileInputStream("user.txt"));
            this.user = (User) userOis.readObject();

            ObjectInputStream itemOis = new ObjectInputStream(new FileInputStream("item.txt"));
            this.item = (Item) itemOis.readObject();
            this.client.requestItem(item.id);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            setItem();
        } catch (IOException | ItemNotAvailableException e) {
            System.out.println(e.getMessage());
        }
    }

    private void setItem() throws IOException, ItemNotAvailableException {
        if (item != null) {
            productName.setText(item.name);
            productPrice.setText(String.valueOf(item.price));
            productDescription.setText(item.description);
            if (item.getImage() != null) {
                System.out.println("Image found");
                InputStream is = new ByteArrayInputStream(item.getImage());
                Image image = new Image(is);
                itemImage.setImage(image);
            }

            buyButton.setText("Buy");
            buyButton.setStyle("-fx-background-color: #00ff00");

            if (!item.isAvailable) {
                buyButton.setDisable(true);
                comment.setText("This product is not available");
                comment.setStyle("-fx-text-fill: red");
                throw new ItemNotAvailableException();
            }

            buyButton.setOnAction(actionEvent -> {
                try {
                    this.client.buyItem(user, item);
                    comment.setText("Item bought");
                    comment.setStyle("-fx-text-fill: green");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    comment.setText("Error buying item");
                    comment.setStyle("-fx-text-fill: red");
                }
            });
        } else {
            comment.setText("Item could not be loaded");
            comment.setStyle("-fx-text-fill: red");
        }
    }

    public void previous(ActionEvent event) throws IOException {
        MainController h = new MainController();
        h.changeScene("items-view.fxml");
    }
}