module YoGiOhGame {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires javafx.media;
    requires lombok;

    opens view to javafx.fxml;
    opens model to com.google.gson;
    opens model.cards to com.google.gson;
    exports model.cards to com.google.gson;
    exports view to javafx.graphics;
}