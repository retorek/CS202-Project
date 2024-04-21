module com.example.servertest {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    exports server;
    opens server to javafx.fxml;
    exports controller;
    opens controller to javafx.fxml;
    exports marketplace;
    opens marketplace to javafx.fxml;
}