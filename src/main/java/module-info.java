module YuGiOhGame {
    requires com.google.gson;
    requires lombok;

    opens yugioh.view to javafx.fxml;
    opens yugioh.model to com.google.gson;
    opens yugioh.model.cards to com.google.gson;
    opens yugioh.model.cards.specialcards to com.google.gson;
    exports yugioh.model.cards to com.google.gson;
    exports yugioh.view to javafx.graphics;
}