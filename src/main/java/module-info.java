module com.batallaNaval {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.batallaNaval to javafx.fxml;
    opens com.batallaNaval.controller to javafx.fxml;
    opens com.batallaNaval.model to javafx.base;

    exports com.batallaNaval;
    exports com.batallaNaval.model;
    exports com.batallaNaval.controller;
}
