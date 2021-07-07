module YuGiOhGame {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires lombok;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.csv;
    requires java.desktop;

    opens yugioh.view to javafx.fxml;
    opens yugioh.model to com.fasterxml.jackson.databind;
    opens yugioh.model.cards to com.fasterxml.jackson.databind;
    opens yugioh.model.cards.specialcards to com.fasterxml.jackson.databind;
    exports yugioh.view to javafx.graphics;
    exports yugioh.utils to javafx.graphics;
    opens yugioh.utils to javafx.fxml;
}