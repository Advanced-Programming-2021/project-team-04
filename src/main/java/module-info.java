module YoGiOhGame {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires javafx.media;
    requires lombok;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.csv;

    opens yugioh.view to javafx.fxml;
    opens yugioh.model to com.google.gson;
    opens yugioh.model.cards to com.google.gson;
    exports yugioh.model.cards to com.google.gson;
    exports yugioh.view to javafx.graphics;
}