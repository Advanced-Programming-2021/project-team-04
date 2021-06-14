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
    private static final ArrayList<String> allCards = new ArrayList<>(MainController.getInstance().getLoggedIn().getAllCardsHashMap().keySet());
    private static int count;
    private static ArrayList<String> mainCards = new ArrayList<>(allDecks.get(count).getMainDeckCards().keySet()); //TODO when a card count reaches 0 remove it
    private static LinkedHashMap<String, Short> mainCardsHashMap = allDecks.get(count).getMainDeckCards();
    private static ArrayList<String> sideCards = new ArrayList<>(allDecks.get(count).getSideDeckCards().keySet());
    private static LinkedHashMap<String, Short> sideCardsHashMap = allDecks.get(count).getSideDeckCards();
    private static int countForMain;
    private static int countForSide;
    private static int countForAdd;
    private static Card firstCard;
    private static Card secondCard;
    private static Scene sceneForAdding;
    private static Scene sceneForOneDeck;
    private static final Card emptyCard = new Card();

    public static void run() {
        countForSide = 0;
        countForMain = 0;
        countForAdd = 0;
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

    public void addCardToMain() {
        DeckController.getInstance().addCardToDeck(allDecks.get(count).getDeckName(), allCards.get(countForAdd), true);
    }

    public void addCardToSide() {
        DeckController.getInstance().addCardToDeck(allDecks.get(count).getDeckName(), allCards.get(countForAdd), false);
    }

    public void removeCardFromMain() {
        DeckController.getInstance().removeCardFromDeck(allDecks.get(count).getDeckName(), mainCards.get(countForMain), true);
        if (mainCardsHashMap.get(mainCards.get(countForMain)) == 0)
            mainCards.remove(countForMain);
        if (countForMain == mainCards.size())
            countForMain--;
        showDeckCards();
        handleButtonForCards();
    }

    public void removeCardFromSide() {
        DeckController.getInstance().removeCardFromDeck(allDecks.get(count).getDeckName(), sideCards.get(countForSide), false);
        if (sideCardsHashMap.get(sideCards.get(countForSide)) == 0)
            sideCards.remove(countForSide);
        if (countForSide == sideCards.size())
            countForSide--;
        showDeckCards();
        handleButtonForCards();
    }

    public void addScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("Add.fxml"));
        try {
            sceneForAdding = new Scene(fxmlLoader.load());
            showPlayerCards();
            LoginView.stage.setScene(sceneForAdding);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deckScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("Deck.fxml"));
        try {
            sceneForOneDeck = new Scene(fxmlLoader.load());
            showDeckCards();
            LoginView.stage.setScene(sceneForOneDeck);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPlayerCards() {
        firstCard = Card.getCardByName(allCards.get(countForAdd));
        if (countForAdd + 1 == allCards.size()) secondCard = emptyCard;
        else secondCard = Card.getCardByName(allCards.get(countForAdd + 1));
        ImageView first = (ImageView) sceneForAdding.lookup("#first");
        ImageView second = (ImageView) sceneForAdding.lookup("#second");
        Image firstImage = new Image(DeckView.class.getResourceAsStream("cardimages/" + firstCard.getName() + ".jpg"));
        Image secondImage;
        if (countForAdd + 1 == allCards.size()) {
            secondImage = new Image(DeckView.class.getResourceAsStream("cardimages/" + "empty.jpg"));
        }
        else {
            secondImage = new Image(DeckView.class.getResourceAsStream("cardimages/" + secondCard.getName() + ".jpg"));
        }
        first.setImage(firstImage);
        second.setImage(secondImage);
    }

    private void showDeckCards() {
        firstCard = Card.getCardByName(mainCards.get(countForMain));
        if (!sideCards.isEmpty()) {
            secondCard = Card.getCardByName(sideCards.get(countForSide));
            ImageView second = (ImageView) sceneForOneDeck.lookup("#second");
            Image secondImage = new Image(DeckView.class.getResourceAsStream("cardimages/" + secondCard.getName() + ".jpg"));
            second.setImage(secondImage);
        } else {
            secondCard = emptyCard;
            ImageView second = (ImageView) sceneForOneDeck.lookup("#second");
            Image secondImage = new Image(DeckView.class.getResourceAsStream("cardimages/" + "empty.jpg"));
            second.setImage(secondImage);
        }
        ImageView first = (ImageView) sceneForOneDeck.lookup("#first");
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

    public void handleButtonsForAdd() {
        sceneForAdding.lookup("#back").setDisable(countForAdd == 0);
        sceneForAdding.lookup("#next").setDisable(countForAdd == allDecks.size() - 1);
        if (countForAdd == 0 && allDecks.isEmpty()) sceneForAdding.lookup("#next").setDisable(true);
    }

    public void nextForAdd() {
        countForAdd++;
        handleButtonsForAdd();
        showPlayerCards();
    }

    public void backForAdd() {
        countForAdd--;
        handleButtonsForAdd();
        showPlayerCards();
    }

}