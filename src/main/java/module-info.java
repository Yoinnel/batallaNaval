module com.batallaNaval.batallanaval {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.batallaNaval to javafx.fxml;
    exports com.batallaNaval;
}