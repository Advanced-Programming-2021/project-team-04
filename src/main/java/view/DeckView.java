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
import java.util.LinkedHashMap;

public class DeckView {

    private static final ArrayList<PlayerDeck> allDecks = MainController.getInstance().getLoggedIn().getAllPlayerDecks();
    private static int count;
    private static ArrayList<String> mainCards = new ArrayList<>(allDecks.get(count).getMainDeckCards().keySet()); //TODO when a card count reaches 0 remove it
    private static LinkedHashMap<String, Short> mainCardsHashMap = allDecks.get(count).getMainDeckCards();
    private static ArrayList<String> sideCards = new ArrayList<>(allDecks.get(count).getSideDeckCards().keySet());
    private static LinkedHashMap<String, Short> sideCardsHashMap = allDecks.get(count).getSideDeckCards();
    private static int countForMain;
    private static int countForSide;
    private static Card firstCard;
    private static Card secondCard;
    private static Scene scene;
    private static final Card emptyCard = new Card();

    public static void run() {
        count = 0;
        showDetails();
        handleButtons();
    }

    private static void handleButtons() {
        LoginView.deckScene.lookup("#back").setDisable(count == 0);
        LoginView.deckScene.lookup("#next").setDisable(count == allDecks.size() - 1);
        if (count == 0 && allDecks.isEmpty()) LoginView.deckScene.lookup("#next").setDisable(true);
    }

    private static void showDetails() {
        Label deckName = ((Label) LoginView.deckScene.lookup("#deckName"));
        if (allDecks.isEmpty()) {
            deckName.setText("Empty");
            count = 0;
            return;
        }
        deckName.setText(allDecks.get(count).getDeckName() + " " + allDecks.get(count).getMainDeckSize());
        if (allDecks.get(count).equals(MainController.getInstance().getLoggedIn().getActiveDeck()))
            deckName.setStyle("-fx-text-fill: #4a78ff");
        else deckName.setStyle("-fx-text-fill: #ffffff");
    }

    public void next() {
        count++;
        handleButtons();
        showDetails();
    }

    public void mainMenu() {
        LoginView.stage.setScene(LoginView.mainScene);
        LoginView.stage.centerOnScreen();
    }

    public void back() {
        count--;
        handleButtons();
        showDetails();
    }

    public void edit() {
        //TODO create fxml for new scene
    }

    public void delete() {
        DeckController.getInstance().deleteDeck(allDecks.get(count).getDeckName());
        if (count == allDecks.size())
            count--;
        showDetails();
        handleButtons();
    }

    public void activate() {
        DeckController.getInstance().activateDeck(allDecks.get(count).getDeckName());
        showDetails();
    }

    public void create() {
        TextField textField = ((TextField) LoginView.deckScene.lookup("#textField"));
        String deckName = textField.getText();
        DeckController.getInstance().createDeck(deckName);
        textField.clear();
        showDetails();
        handleButtons();
    }

    public void addCard() {

    }

    public void removeCardFromMain() {
        DeckController.getInstance().removeCardFromDeck(allDecks.get(count).getDeckName(), mainCards.get(countForMain), true);
        if (countForMain == mainCards.size())
            countForMain--;
        showDeckCards();
        handleButtonForCards();
    }

    public void removeCardFromSide() {
        DeckController.getInstance().removeCardFromDeck(allDecks.get(count).getDeckName(), sideCards.get(countForSide), false);
        if (countForSide == sideCards.size())
            countForSide--;
        showDeckCards();
        handleButtonForCards();
    }

    public void deckScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("Deck.fxml"));
        try {
            scene = new Scene(fxmlLoader.load());
            showDeckCards();
            LoginView.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDeckCards() {
        firstCard = Card.getCardByName(mainCards.get(countForMain));
        if (!sideCards.isEmpty()) {
            secondCard = Card.getCardByName(sideCards.get(countForSide));
            ImageView second = (ImageView) scene.lookup("#second");
            Image secondImage = new Image(DeckView.class.getResourceAsStream("cardimages/" + secondCard.getName() + ".jpg"));
            second.setImage(secondImage);
        } else {
            secondCard = emptyCard;
            ImageView second = (ImageView) scene.lookup("#second");
            Image secondImage = new Image(DeckView.class.getResourceAsStream("cardimages/" + "empty.jpg"));
            second.setImage(secondImage);
        }
        ImageView first = (ImageView) scene.lookup("#first");
        Image firstImage = new Image(DeckView.class.getResourceAsStream("cardimages/" + firstCard.getName() + ".jpg"));
        first.setImage(firstImage);
    }

    private static void handleButtonForCards() {
        LoginView.deckScene.lookup("#back").setDisable(countForMain == 0);
        LoginView.deckScene.lookup("#next").setDisable(countForMain == allDecks.size() - 1);
        if (countForMain == 0 && allDecks.isEmpty()) LoginView.deckScene.lookup("#next").setDisable(true);
    }

    public void nextForDeck() {
        countForMain++;
        countForSide++;
        handleButtonForCards();
        showDeckCards();
    }

    public void backForDeck() {
        countForMain--;
        countForSide--;
        handleButtonForCards();
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