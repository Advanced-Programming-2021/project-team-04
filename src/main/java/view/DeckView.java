package view;


import controller.DeckController;
import controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.PlayerDeck;
import model.cards.Card;

import java.io.IOException;
import java.util.ArrayList;

public class DeckView {

    public static Label deckName = new Label();
    private static final ArrayList<PlayerDeck> allDecks = MainController.getInstance().getLoggedIn().getAllPlayerDecks();
    private static int count;
    private static int countForMain;
    private static int countForSide;

    public static TextField textField = new TextField();
    private static Card firstCard;
    private static Card secondCard;
    private static Scene scene;

    public static void run() {
        showDetails();
        LoginView.deckScene.lookup("#back").setDisable(true);
    }

    private static void showDetails() {
        deckName.setText(allDecks.get(count).getDeckName() + " " + allDecks.get(count).getMainDeckSize());
        if (allDecks.get(count).equals(MainController.getInstance().getLoggedIn().getActiveDeck()))
            deckName.setStyle("-fx-text-fill: #0040ff");
        else deckName.setStyle("-fx-text-fill: #ffffff");
    }

    public void next() {
        if (count == 0) LoginView.deckScene.lookup("#back").setDisable(false);
        if (count == allDecks.size() - 1) LoginView.deckScene.lookup("#next").setDisable(true);
        count++;
        showDetails();
    }

    public void mainMenu() {
        LoginView.stage.setScene(LoginView.mainScene);
    }

    public void back() {
        if (count == 0) LoginView.deckScene.lookup("#back").setDisable(false);
        if (count == allDecks.size() - 1) LoginView.deckScene.lookup("#next").setDisable(true);
        count--;
        showDetails();
    }

    public void edit() {
        //TODO create fxml for new scene
    }

    public void delete() {
        DeckController.getInstance().deleteDeck(allDecks.get(count).getDeckName());
        count++;
        showDetails();
    }

    public void activate() {
        DeckController.getInstance().activateDeck(allDecks.get(count).getDeckName());
        showDetails();
    }

    public void create() {
        String deckName = textField.getText();
        DeckController.getInstance().createDeck(deckName);
    }

    public void details() {
        //TODO show deck scene
    }

    public void addCard() {

    }

    public void removeCard() {

    }

    private void deckScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("Deck.fxml"));
        try {
            DeckView.scene = new Scene(fxmlLoader.load());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDeckCards() {
        PlayerDeck playerDeck = allDecks.get(count);


        ImageView first = (ImageView) scene.lookup("#first");
        ImageView second = (ImageView) scene.lookup("#second");
        Image firstImage = new Image(DeckView.class.getResourceAsStream("cardimages/" + firstCard.getName() + ".jpg"));
        Image secondImage = new Image(DeckView.class.getResourceAsStream("cardimages/" + secondCard.getName() + ".jpg"));
        first.setImage(firstImage);
        second.setImage(secondImage);
    }

    public void nextForDeck() {
        if (countForMain == allDecks.get(count).getMainDeckSize()) LoginView.shopScene.lookup("#next").setDisable(false);
        if (countForMain == 0) LoginView.shopScene.lookup("#back").setDisable(true);
        countForMain++;
        countForSide++;
        showDeckCards();
    }

    public void backForDeck() {
        if (countForMain == allDecks.get(count).getMainDeckSize()) LoginView.shopScene.lookup("#next").setDisable(false);
        if (countForMain == 0) LoginView.shopScene.lookup("#back").setDisable(true);
        countForMain--;
        countForSide--;
        showDeckCards();
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