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
    private static ArrayList<String> mainCards = new ArrayList<>(allDecks.get(count).getMainDeckCards().keySet());
    private static LinkedHashMap<String, Short> mainCardsHashMap = allDecks.get(count).getMainDeckCards();
    private static ArrayList<String> sideCards = new ArrayList<>(allDecks.get(count).getSideDeckCards().keySet());
    private static LinkedHashMap<String, Short> sideCardsHashMap = allDecks.get(count).getSideDeckCards();
    private static int countForDeck;
    private static int countForAdd;
    private static Card firstCard;
    private static Card secondCard;
    private static Scene sceneForAdding;
    private static Scene sceneForOneDeck;
    private static final Card emptyCard = new Card();

    //TODO labels for number of cards
    //TODO test with side deck not empty
    //TODO add card scene
    //TODO profile menu
    //TODO back button in add scene

    public static void run() {
        countForDeck = 0;
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
        DeckController.getInstance().removeCardFromDeck(allDecks.get(count).getDeckName(), mainCards.get(countForDeck), true);
        if (!mainCardsHashMap.containsKey(mainCards.get(countForDeck)))
            mainCards.remove(countForDeck);
        if (countForDeck == mainCards.size())
            countForDeck--;
        showDeckCards();
        handleButtonForCards();
    }

    public void removeCardFromSide() {
        DeckController.getInstance().removeCardFromDeck(allDecks.get(count).getDeckName(), sideCards.get(countForDeck), false);
        if (!sideCardsHashMap.containsKey(sideCards.get(countForDeck)))
            sideCards.remove(countForDeck);
        if (countForDeck == sideCards.size())
            countForDeck--;
        showDeckCards();
        handleButtonForCards();
    }

    public void addScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("Add.fxml"));
        try {
            sceneForAdding = new Scene(fxmlLoader.load());
            showPlayerCards();
            LoginView.setSize(sceneForAdding, 1424, 2224);
            LoginView.stage.setScene(sceneForAdding);
            LoginView.stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deckScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("Deck.fxml"));
        try {
            sceneForOneDeck = new Scene(fxmlLoader.load());
            showDeckCards();
            LoginView.setSize(sceneForOneDeck, 1098, 1638);
            LoginView.stage.setScene(sceneForOneDeck);
            LoginView.stage.centerOnScreen();
            handleButtonForCards();
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
        if (countForDeck < 0) countForDeck = 0;
        ImageView first = (ImageView) sceneForOneDeck.lookup("#first");
        first.setImage(mainImage());
        ImageView second = (ImageView) sceneForOneDeck.lookup("#second");
        second.setImage(sideImage());
    }

    private Image sideImage() {
        Image secondImage;
        if (sideCards.size() > countForDeck) {
            secondCard = Card.getCardByName(sideCards.get(countForDeck));
            secondImage = new Image(DeckView.class.getResourceAsStream("cardimages/" + secondCard.getName() + ".jpg"));
        } else {
            secondCard = emptyCard;
            secondImage = new Image(DeckView.class.getResourceAsStream("cardimages/" + "empty.jpg"));
        }
        return secondImage;
    }

    private Image mainImage() {
        Image firstImage;
        if (mainCards.size() > countForDeck) {
            firstCard = Card.getCardByName(mainCards.get(countForDeck));
            firstImage = new Image(DeckView.class.getResourceAsStream("cardimages/" + firstCard.getName() + ".jpg"));
        } else {
            firstCard = emptyCard;
            firstImage = new Image(DeckView.class.getResourceAsStream("cardimages/" + "empty.jpg"));
        }
        return firstImage;
    }


    private static void handleButtonForCards() {
        sceneForOneDeck.lookup("#back").setDisable(countForDeck == 0);
        sceneForOneDeck.lookup("#next").setDisable(countForDeck >= mainCards.size() - 1 && countForDeck >= sideCards.size() - 1);
    }

    public void nextForDeck() {
        countForDeck++;
        handleButtonForCards();
        showDeckCards();
    }

    public void backForDeck() {
        countForDeck--;
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

    public void backToDeck() {
        LoginView.stage.setScene(LoginView.deckScene);
        LoginView.stage.centerOnScreen();
    }

    public void backToEdit() {
        LoginView.stage.setScene(sceneForOneDeck);
        LoginView.stage.centerOnScreen();
    }
}