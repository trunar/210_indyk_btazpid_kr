module com.example.indyk {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.example.indyk to javafx.fxml;
    exports com.example.indyk;
}