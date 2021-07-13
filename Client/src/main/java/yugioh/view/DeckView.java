package yugioh.view;


import javafx.animation.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import yugioh.controller.DeckController;
import yugioh.controller.ImportAndExport;
import yugioh.controller.MainController;
import yugioh.model.PlayerDeck;
import yugioh.model.cards.Card;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class DeckView {

    private static final Card EMPTY_CARD = new Card();
    private static ArrayList<PlayerDeck> allDecks;
    private static ArrayList<String> allCards;
    private static int count;
    private static ArrayList<String> mainCards;
    private static ArrayList<String> sideCards;
    private static LinkedHashMap<String, Short> mainCardsHashMap;
    private static LinkedHashMap<String, Short> sideCardsHashMap;
    private static int countForDeck;
    private static int countForAdd;
    private static int countForCard;
    private static Card firstCard;
    private static Card secondCard;
    private static Scene sceneForAdding;
    private static Scene sceneForOneDeck;

    public static void run() {
        handleAnimation();
        allDecks = MainController.getInstance().getLoggedIn().getAllPlayerDecks();
        countForDeck = 0;
        countForAdd = 0;
        count = 0;
        countForCard = 0;
        updateLists();
        showDetails();
        handleButtons();
    }

    private static void initializeForAddToDeck() {
        allCards = new ArrayList<>(MainController.getInstance().getLoggedIn().getAllCardsHashMap().keySet());
    }

    private static void updateLists() {
        if (allDecks.isEmpty()) return;
        mainCards = new ArrayList<>(allDecks.get(count).getMainDeckCards().keySet());
        sideCards = new ArrayList<>(allDecks.get(count).getSideDeckCards().keySet());
        mainCardsHashMap = allDecks.get(count).getMainDeckCards();
        sideCardsHashMap = allDecks.get(count).getSideDeckCards();
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
            deckName.setStyle("-fx-text-fill: #ffffff");
            count = 0;
            LoginView.deckScene.lookup("#edit").setDisable(true);
            LoginView.deckScene.lookup("#delete").setDisable(true);
            LoginView.deckScene.lookup("#activate").setDisable(true);
            return;
        }
        setLabelToolTip(deckName);
        deckName.setText(allDecks.get(count).getDeckName() + " " + allDecks.get(count).getMainDeckSize());
        if (allDecks.get(count).equals(MainController.getInstance().getLoggedIn().getActiveDeck()))
            deckName.setStyle("-fx-text-fill: #4a78ff");
        else deckName.setStyle("-fx-text-fill: #ffffff");
    }

    private static void setLabelToolTip(Label deckName) {
        Tooltip tooltip = new Tooltip(DeckController.getInstance().getDeckCards(allDecks.get(count)));
        tooltip.setWrapText(true);
        tooltip.setStyle("-fx-font-size: 27");
        deckName.setTooltip(tooltip);
        tooltip.setShowDelay(Duration.seconds(0));
    }

    private static void handleAnimation() {
        if (MainController.getInstance().getLoggedIn().getAllPlayerDecks().isEmpty()) return;
        ImageView animation = (ImageView) LoginView.deckScene.lookup("#animation");
        var transition = new SequentialTransition(getNewTranslateTransition("#edit", "#activate"), getNewPauseAndChangeImageTransition(animation), new PauseTransition(Duration.seconds(0.25)), getNewTranslateTransition("#activate", "#edit"), getNewPauseAndChangeImageTransition(animation), new PauseTransition(Duration.seconds(0.25)));
        transition.setNode(animation);
        transition.setDelay(Duration.ZERO);
        transition.setInterpolator(Interpolator.EASE_BOTH);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.play();
    }

    private static PauseTransition getNewPauseAndChangeImageTransition(ImageView animation) {
        var pauseAndChangeImageTransition = new PauseTransition();
        pauseAndChangeImageTransition.setDuration(Duration.seconds(0.25));
        pauseAndChangeImageTransition.setOnFinished(event -> {
            var deck = allDecks.get(count);
            if (countForCard == deck.getMainDeckCards().keySet().size())
                countForCard = 0;
            try {
                //TODO what the fuck is this
                animation.setImage(deck.getMainDeckCards().keySet().isEmpty() ? getNullCardImage() : getImageByCard(ImportAndExport.getInstance().readCard((String) deck.getMainDeckCards().keySet().toArray()[countForCard])));
            } catch (Exception e) {
                animation.setImage(getNullCardImage());
            }
            countForCard++;
            event.consume();
        });
        return pauseAndChangeImageTransition;
    }

    private static TranslateTransition getNewTranslateTransition(String firstButton, String secondButton) {
        var translateTransition = new TranslateTransition();
        translateTransition.setFromX(LoginView.deckScene.getX() - LoginView.deckScene.lookup(firstButton).getBoundsInParent().getCenterX());
        translateTransition.setToX(LoginView.deckScene.getX() - LoginView.deckScene.lookup(secondButton).getBoundsInParent().getCenterX());
        translateTransition.setDuration(Duration.seconds(2));
        translateTransition.setInterpolator(Interpolator.EASE_BOTH);
        return translateTransition;
    }

    private static Image getImageByCard(Card card) {
        try {
            return new Image(Objects.requireNonNull(DeckView.class.getResource("cardimages/" + card.getName() + ".jpg")).toExternalForm(), 300, 300, true, true);
        } catch (Exception e) {
            return getNullCardImage();
        }
    }

    private static Image getNullCardImage() {
        return new Image(Objects.requireNonNull(DeckView.class.getResource("cardimages/void.jpg")).toExternalForm(), 300, 300, true, true);
    }

    private static void handleButtonForCards() {
        sceneForOneDeck.lookup("#back").setDisable(countForDeck == 0);
        sceneForOneDeck.lookup("#next").setDisable(countForDeck >= mainCards.size() - 1 && countForDeck >= sideCards.size() - 1);
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
        LoginView.deckScene.lookup("#edit").setDisable(false);
        LoginView.deckScene.lookup("#delete").setDisable(false);
        LoginView.deckScene.lookup("#activate").setDisable(false);
    }

    public void addFirstCardToMain() {
        if (firstCard == null) return;
        DeckController.getInstance().addCardToDeck(allDecks.get(count).getDeckName(), allCards.get(countForAdd), true);
    }

    public void addFirstCardToSide() {
        if (firstCard == null) return;
        DeckController.getInstance().addCardToDeck(allDecks.get(count).getDeckName(), allCards.get(countForAdd), false);
    }

    public void addSecondCardToMain() {
        if (secondCard == null) return;
        DeckController.getInstance().addCardToDeck(allDecks.get(count).getDeckName(), allCards.get(countForAdd + 1), true);
    }

    public void addSecondCardToSide() {
        if (secondCard == null) return;
        DeckController.getInstance().addCardToDeck(allDecks.get(count).getDeckName(), allCards.get(countForAdd + 1), false);
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
        initializeForAddToDeck();
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("Add.fxml"));
        try {
            sceneForAdding = new Scene(fxmlLoader.load());
            showPlayerCards();
            LoginView.setSize(sceneForAdding);
            LoginView.stage.setScene(sceneForAdding);
            LoginView.stage.centerOnScreen();
            sceneForAdding.lookup("#back").setDisable(true);
            if (allCards.size() == 2) sceneForAdding.lookup("#next").setDisable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deckScene() {
        updateLists();
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("Deck.fxml"));
        try {
            sceneForOneDeck = new Scene(fxmlLoader.load());
            showDeckCards();
            LoginView.setSize(sceneForOneDeck);
            LoginView.stage.setScene(sceneForOneDeck);
            LoginView.stage.centerOnScreen();
            handleButtonForCards();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPlayerCards() {
        if (allCards.size() == 0) firstCard = null;
        else firstCard = Card.getCardByName(allCards.get(countForAdd));
        if (allCards.size() <= 1) secondCard = null;
        else secondCard = Card.getCardByName(allCards.get(countForAdd + 1));
        ImageView first = (ImageView) sceneForAdding.lookup("#first");
        ImageView second = (ImageView) sceneForAdding.lookup("#second");
        Image firstImage;
        Image secondImage;
        if (firstCard == null)
            firstImage = new Image(Objects.requireNonNull(DeckView.class.getResourceAsStream("cardimages/empty.jpg")));
        else
            firstImage = new Image(Objects.requireNonNull(DeckView.class.getResourceAsStream("cardimages/" + firstCard.getName() + ".jpg")));
        if (secondCard == null)
            secondImage = new Image(Objects.requireNonNull(DeckView.class.getResourceAsStream("cardimages/empty.jpg")));
        else
            secondImage = new Image(Objects.requireNonNull(DeckView.class.getResourceAsStream("cardimages/" + secondCard.getName() + ".jpg")));
        first.setImage(firstImage);
        second.setImage(secondImage);
    }

    private void showDeckCards() {
        if (countForDeck < 0) countForDeck = 0;
        ImageView first = (ImageView) sceneForOneDeck.lookup("#first");
        first.setImage(mainImage((Label) sceneForOneDeck.lookup("#numberMain")));
        ImageView second = (ImageView) sceneForOneDeck.lookup("#second");
        second.setImage(sideImage((Label) sceneForOneDeck.lookup("#numberSide")));
    }

    private Image sideImage(Label number) {
        Image secondImage;
        if (sideCards.size() > countForDeck) {
            secondCard = Card.getCardByName(sideCards.get(countForDeck));
            if (!firstCard.isOriginal() || firstCard.isConverted())
                secondImage = new Image(Objects.requireNonNull(ShopView.class.getResourceAsStream("cardimages/JonMartin.jpg")));
            else
                secondImage = new Image(Objects.requireNonNull(DeckView.class.getResourceAsStream("cardimages/" + secondCard.getName() + ".jpg")));
            number.setText(String.valueOf(sideCardsHashMap.get(secondCard.getName())));
            sceneForOneDeck.lookup("#removeSide").setDisable(false);
        } else {
            secondCard = EMPTY_CARD;
            secondImage = new Image(Objects.requireNonNull(DeckView.class.getResourceAsStream("cardimages/" + "empty.jpg")));
            sceneForOneDeck.lookup("#removeSide").setDisable(true);
            number.setText("");
        }
        return secondImage;
    }

    private Image mainImage(Label number) {
        Image firstImage;
        if (mainCards.size() > countForDeck) {
            firstCard = Card.getCardByName(mainCards.get(countForDeck));
            number.setText(String.valueOf(mainCardsHashMap.get(firstCard.getName())));
            if (!firstCard.isOriginal() || firstCard.isConverted())
                firstImage = new Image(Objects.requireNonNull(ShopView.class.getResourceAsStream("cardimages/JonMartin.jpg")));
            else
                firstImage = new Image(Objects.requireNonNull(DeckView.class.getResourceAsStream("cardimages/" + firstCard.getName() + ".jpg")));
            sceneForOneDeck.lookup("#removeMain").setDisable(false);
        } else {
            firstCard = EMPTY_CARD;
            number.setText("");
            firstImage = new Image(Objects.requireNonNull(DeckView.class.getResourceAsStream("cardimages/" + "empty.jpg")));
            sceneForOneDeck.lookup("#removeMain").setDisable(true);
        }
        return firstImage;
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

    public void nextForAdd() {
        if (countForAdd == 0) sceneForAdding.lookup("#back").setDisable(false);
        if (countForAdd == allCards.size() - 3) sceneForAdding.lookup("#next").setDisable(true);
        countForAdd++;
        showPlayerCards();
    }

    public void backForAdd() {
        if (countForAdd == allCards.size() - 2) sceneForAdding.lookup("#next").setDisable(false);
        if (countForAdd == 1) sceneForAdding.lookup("#back").setDisable(true);
        countForAdd--;
        showPlayerCards();
    }

    public void backToDeck() {
        LoginView.stage.setScene(LoginView.deckScene);
        showDetails();
        LoginView.stage.centerOnScreen();
    }

    public void backToEdit() {
        deckScene();
        LoginView.stage.centerOnScreen();
    }

}