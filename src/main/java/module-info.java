module com.batallanaval.batallanaval {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.batallanaval to javafx.fxml;
    exports com.batallanaval;
}