package view;


import controller.DeckController;
import controller.MainController;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import javafx.scene.control.TextField;
import model.PlayerDeck;

import java.util.ArrayList;

public class DeckView {

    public static Label deckName;
    private static final ArrayList<PlayerDeck> allDecks = MainController.getInstance().getLoggedIn().getAllPlayerDecks();
    private static int count;
    public static TextField textField;
    public static Scene scene;

    public static void run() {
        showDetails();
        LoginView.deckScene.lookup("#back").setDisable(true);
    }

    private static void showDetails() {
        deckName.setText(allDecks.get(count).getDeckName() + " " + allDecks.get(0).getMainDeckSize());
        if (allDecks.get(count).equals(MainController.getInstance().getLoggedIn().getActiveDeck()))
            deckName.setStyle("-fx-text-fill: #0040ff");
        else deckName.setStyle("-fx-text-fill: #ffffff");
    }

    public static void next() {
        if (count == 0) LoginView.deckScene.lookup("#back").setDisable(false);
        if (count == allDecks.size() - 1) LoginView.deckScene.lookup("#next").setDisable(true);
        count++;
        showDetails();
    }

    public static void back() {
        if (count == 0) LoginView.deckScene.lookup("#back").setDisable(false);
        if (count == allDecks.size() - 1) LoginView.deckScene.lookup("#next").setDisable(true);
        count--;
        showDetails();
    }

    public static void edit() {
        //TODO create fxml for new scene
    }

    public static void delete() {
        DeckController.getInstance().deleteDeck(allDecks.get(count).getDeckName());
        count++;
        showDetails();
    }

    public static void activate() {
        DeckController.getInstance().activateDeck(allDecks.get(count).getDeckName());
        showDetails();
    }

    public static void create() {
        String deckName = textField.getText();
        DeckController.getInstance().createDeck(deckName);
    }

    public static void details() {
        //TODO show deck scene
    }

    public static void addCard() {

    }

    public static void removeCard() {

    }

//    private void createDeck(Matcher matcher) {
//        DeckController.getInstance().createDeck(matcher.group("name").trim());
//    }
//
//    private void deleteDeck(Matcher matcher) {
//        DeckController.getInstance().deleteDeck(matcher.group("name").trim());
//    }
//
//    private void activateDeck(Matcher matcher) {
//        DeckController.getInstance().activateDeck(matcher.group("name").trim());
//    }
//
//    private void addCardToDeck(Matcher matcher, boolean isMainDeck) {
//        DeckController.getInstance().addCardToDeck(matcher.group("deckName").trim(), matcher.group("cardName").trim(), isMainDeck);
//    }
//
//    private void removeCardFromDeck(Matcher matcher, boolean isMainDeck) {
//        DeckController.getInstance().removeCardFromDeck(matcher.group("deckName").trim(), matcher.group("cardName").trim(), isMainDeck);
//    }
//
//    private void printAllDecks() {
//        DeckController.getInstance().printAllDecks();
//    }
//
//    private void printDeck(Matcher matcher, boolean isMainDeck) {
//        DeckController.getInstance().printDeck(matcher.group("deckName").trim(), isMainDeck);
//    }
//
//    private void printAllCards() {
//        DeckController.getInstance().printAllCards();
//    }

}