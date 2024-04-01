module com.example.servertest {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.servertest to javafx.fxml;
    exports com.example.servertest;
}