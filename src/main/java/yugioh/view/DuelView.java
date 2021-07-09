package yugioh.view;


import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import yugioh.controller.DuelController;
import yugioh.model.Account;
import yugioh.model.cards.CardStatusInField;
import yugioh.model.cards.MonsterCardModeInField;
import yugioh.model.cards.Card;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;
import yugioh.utils.DragUtils;
import yugioh.utils.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;


public class DuelView {

    private static final Card EMPTY_CARD = new Card();

    public static Stage secondStage;
    private static DuelView singleInstance;
    private static ArrayList<String> myGY;
    private static ArrayList<String> opponentGY;
    private static int count;
    private static Card firstCard;
    private static Card secondCard;
    private static int counter;
    private static Account player1;
    private static Account player2;

    private static Card chosen;
    private static Stage choosing;
    private static ArrayList<Card> toChooseFrom;
    private static int navigateChoose;
    private static Card firstOption;
    private static Card secondOption;
    public static DuelView getInstance() {
        if (singleInstance == null) singleInstance = new DuelView();
        return singleInstance;
    }

    public static void run() {
        choosing = new Stage();
        handleTurn((Account) DuelController.getInstance().getGame().getCurrentPlayer());
        setCardImages(player1, player2, LoginView.mainGameSceneOne);
        setCardImages(player2, player1, LoginView.mainGameSceneTwo);
        makeCardsDraggable(LoginView.mainGameSceneOne);
        makeCardsDraggable(LoginView.mainGameSceneTwo);
        setProfiles(player1, player2, LoginView.mainGameSceneOne);
        setProfiles(player2, player1, LoginView.mainGameSceneTwo);
        setLP(player1.getUsername(), player1.getLP());
        setLP(player2.getUsername(), player2.getLP());
    }

    private static void makeCardsDraggable(Scene scene) {
        for (var i = 1; i <= 5; i++) {
            setMonsterDraggable(scene.lookup("#monster" + i));
//            setMonsterDraggable(scene.lookup("#spell" + i));
        }
        for (var i = 1; i <= 6; i++) {
            setHandDraggable(scene.lookup("#hand" + i));
        }
//        setMonsterDraggable(scene.lookup("#field"));
    }

    private static void setMonsterDraggable(Node node) {
        if (Objects.isNull(node)) return;
        var dragHandler = new DragUtils.DragHandler(node);
        dragHandler.addListener((listenerDragHandler, listenerDragEvent) -> dragHandler.getDragNodes().forEach((dragNode, coordination) -> {
            if (listenerDragEvent == DragUtils.Event.DRAG_START)
                DuelController.getInstance().selectCard(true, CardStatusInField.MONSTER_FIELD, Integer.parseInt(dragNode.getId().substring(dragNode.getId().length() - 1)) - 1);
            if (listenerDragEvent == DragUtils.Event.DRAG_END) {
                var target = getCardByCoordination(new Point2D(dragHandler.getLastMouseX(), dragHandler.getLastMouseY()), node.getScene());
                if (Objects.nonNull(target) && !target.getFirst() && target.getSecond() == CardStatusInField.MONSTER_FIELD) {
                    DuelController.getInstance().attack(target.getThird() - 1);
                    MainView.playAttackSong();
                    setCardImages(player1, player2, LoginView.mainGameSceneOne);
                    setCardImages(player2, player1, LoginView.mainGameSceneTwo);
                }
                DragUtils.playGoingBackTransition(dragNode, coordination);
            }
        }));
    }

    private static void setHandDraggable(Node node) {
        if (Objects.isNull(node)) return;
        var dragHandler = new DragUtils.DragHandler(node);
        dragHandler.addListener((listenerDragHandler, listenerDragEvent) -> dragHandler.getDragNodes().forEach((dragNode, coordination) -> {
            if (listenerDragEvent == DragUtils.Event.DRAG_START)
                DuelController.getInstance().selectCard(true, CardStatusInField.HAND, Integer.parseInt(dragNode.getId().substring(dragNode.getId().length() - 1)) - 1);
            if (listenerDragEvent == DragUtils.Event.DRAG_END) {
                var target = getCardByCoordination(new Point2D(dragHandler.getLastMouseX(), dragHandler.getLastMouseY()), node.getScene());
                if (Objects.nonNull(target) && target.getFirst()) {
                    if (target.getSecond() == CardStatusInField.MONSTER_FIELD) {
                        DuelController.getInstance().setOrSummon();
                        dragNode.setTranslateX(coordination.get(0));
                        dragNode.setTranslateY(coordination.get(1));
                    }
                    if (target.getSecond() == CardStatusInField.SPELL_FIELD || target.getSecond() == CardStatusInField.FIELD_ZONE) {
                        activate();
                        dragNode.setTranslateX(coordination.get(0));
                        dragNode.setTranslateY(coordination.get(1));
                    }
                } else DragUtils.playGoingBackTransition(dragNode, coordination);
            }
        }));
    }

    private static Triple<Boolean, CardStatusInField, Integer> getCardByCoordination(Point2D point, Scene scene) {
        if (scene.lookup("#opponentField").getBoundsInParent().contains(point))
            return new Triple<>(false, CardStatusInField.FIELD_ZONE, 0);
        for (var i = 1; i <= 5; i++) {
            if (scene.lookup("#opponentMonster" + i).getBoundsInParent().contains(point))
                return new Triple<>(false, CardStatusInField.MONSTER_FIELD, i);
            if (scene.lookup("#opponentSpell" + i).getBoundsInParent().contains(point))
                return new Triple<>(false, CardStatusInField.SPELL_FIELD, i);
        }
        if (scene.lookup("#field").getBoundsInParent().contains(point))
            return new Triple<>(true, CardStatusInField.FIELD_ZONE, 0);
        for (var i = 1; i <= 5; i++) {
            if (scene.lookup("#monster" + i).getBoundsInParent().contains(point))
                return new Triple<>(true, CardStatusInField.MONSTER_FIELD, i);
            if (scene.lookup("#spell" + i).getBoundsInParent().contains(point))
                return new Triple<>(true, CardStatusInField.SPELL_FIELD, i);
        }
        for (var i = 1; i <= 6; i++)
            if (scene.lookup("#hand" + i).getBoundsInParent().contains(point))
                return new Triple<>(true, CardStatusInField.HAND, i);
        return null;
    }

    public static void setLP(String username, int LP) {
        String style = "-fx-text-fill: RGB(" + (8000 - LP) / 40 + ", " + LP / 40 + ", 0);";
        if (username.equals(player1.getUsername())) {
            Label label = ((Label) LoginView.mainGameSceneOne.lookup("#LP"));
            label.setText(String.valueOf(LP));
            label.setStyle(style);
            label = ((Label) LoginView.mainGameSceneTwo.lookup("#opponentLP"));
            label.setText(String.valueOf(LP));
            label.setStyle(style);
        } else {
            Label label = ((Label) LoginView.mainGameSceneTwo.lookup("#LP"));
            label.setText(String.valueOf(LP));
            label.setStyle(style);
            label = ((Label) LoginView.mainGameSceneOne.lookup("#opponentLP"));
            label.setText(String.valueOf(LP));
            label.setStyle(style);
        }
    }

    public static void handleTurn(Account currentPlayer) {
        boolean isPlayer1sTurn = currentPlayer.getUsername().equals(player1.getUsername());
        LoginView.mainGameSceneTwo.getRoot().setMouseTransparent(isPlayer1sTurn);
        LoginView.mainGameSceneOne.getRoot().setMouseTransparent(!isPlayer1sTurn);
    }

    private static void setProfiles(Account player, Account otherPlayer, Scene scene) {
        ImageView opponentProfile = (ImageView) scene.lookup("#opponentProfile");
        ImageView profile = (ImageView) scene.lookup("#profile");
        Image playerImage = new Image(DuelView.class.getResourceAsStream("profiles/" +
                player.getProfilePictureNumber() + player.getProfilePictureExtension()));
        Image otherPlayerImage = new Image(DuelView.class.getResourceAsStream("profiles/" +
                otherPlayer.getProfilePictureNumber() + otherPlayer.getProfilePictureExtension()));
        opponentProfile.setImage(otherPlayerImage);
        profile.setImage(playerImage);
        setToolTip(player.toString(), profile);
        setToolTip(otherPlayer.toString(), opponentProfile);
    }

    private static void setCardImages(Account player, Account otherPlayer, Scene scene) {
        setHandImages(player, scene);
        setMonsterZone(player.getField().getMonsterCards(), scene, "#monster");
        setMonsterZone(otherPlayer.getField().getMonsterCards(), scene, "#opponentMonster");
        setSpellZone(player.getField().getSpellAndTrapCards(), scene, "#spell");
        setSpellZone(otherPlayer.getField().getSpellAndTrapCards(), scene, "#opponentSpell");
        setCardImage(player.getField().getFieldZone(), (ImageView) scene.lookup("#field"), true, "field zone");
        setCardImage(otherPlayer.getField().getFieldZone(), (ImageView) scene.lookup("#opponentField"), true, "field zone");
        setCardImage(DuelController.getInstance().getGame().getSelectedCard(), (ImageView) scene.lookup("#selectedCard"),
                DuelController.getInstance().getGame().getCurrentPlayer().getUsername().equals(player.getUsername()), "selected card");
    }

    private static void setSpellZone(ArrayList<SpellAndTrapCard> cards, Scene scene, String id) {
        int size = cards.size();
        for (int i = 1; i <= 5; i++) {
            if (i > size) setCardImage(null, (ImageView) scene.lookup(id + i), id.equals("#spell"), "spell zone");
            else setCardImage(cards.get(i - 1), (ImageView) scene.lookup(id + i), id.equals("#spell"), "spell zone");
        }
    }

    private static void setMonsterZone(ArrayList<MonsterCard> cards, Scene scene, String id) {
        int size = cards.size();
        for (int i = 1; i <= 5; i++) {
            if (i > size) setCardImage(null, (ImageView) scene.lookup(id + i), id.equals("#monster"), "monster zone");
            else
                setCardImage(cards.get(i - 1), (ImageView) scene.lookup(id + i), id.equals("#monster"), "monster zone");
        }
    }

    private static void setHandImages(Account player, Scene scene) {
        ArrayList<Card> hand = player.getField().getHand();
        int handSize = hand.size();
        for (int i = 1; i <= 6; i++) {
            if (i > handSize) setCardImage(null, (ImageView) scene.lookup("#hand" + i), true, "player hand");
            else setCardImage(hand.get(i - 1), (ImageView) scene.lookup("#hand" + i), true, "player hand");
        }
    }

    private static void setCardImage(Card card, ImageView imageView, boolean isMine, String place) {
        if (card == null) {
            imageView.setImage(new Image(DuelView.class.getResourceAsStream("cardimages/void.jpg")));
            setToolTip(place + "\nempty", imageView);
            return;
        }
        if (card instanceof MonsterCard) {
            MonsterCard monsterCard = (MonsterCard) card;
            if (monsterCard.getMonsterCardModeInField() == MonsterCardModeInField.DEFENSE_FACE_DOWN
                    && !place.equals("player hand") && !(isMine && place.equals("selected card"))) {
                imageView.setImage(new Image(DuelView.class.getResourceAsStream("cardimages/hidden.jpg")));
                if (isMine) setToolTip(place + "\n" + monsterCard.toString(), imageView);
                else setToolTip(place + "\nhidden", imageView);
            } else {
                imageView.setImage(new Image(DuelView.class.getResourceAsStream("cardimages/" + monsterCard.getName() + ".jpg")));
                setToolTip(place + "\n" + monsterCard.toString(), imageView);
            }
        } else {
            SpellAndTrapCard spellAndTrapCard = (SpellAndTrapCard) card;
            if (!spellAndTrapCard.isActive() && !place.equals("player hand") && !(isMine && place.equals("selected card"))) {
                imageView.setImage(new Image(DuelView.class.getResourceAsStream("cardimages/hidden.jpg")));
                if (isMine) setToolTip(place + "\n" + spellAndTrapCard.toString(), imageView);
                else setToolTip(place + "\nhidden", imageView);
            } else {
                imageView.setImage(new Image(DuelView.class.getResourceAsStream("cardimages/" + spellAndTrapCard.getName() + ".jpg")));
                setToolTip(place + "\n" + spellAndTrapCard.toString(), imageView);
            }
        }
    }

    private static void setToolTip(String text, ImageView imageView) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.seconds(0));
        tooltip.setStyle("-fx-font-size: 16");
        Tooltip.install(imageView, tooltip);
    }


    private static void chooseCard() {
        navigateChoose = 0;
        LoginView.choosingScene.lookup("#back").setDisable(true);
        LoginView.choosingScene.lookup("#next").setDisable(toChooseFrom.size() <= 2);
        showOptions();
        choosing.setScene(LoginView.choosingScene);
        choosing.showAndWait();
    }

    private static void showOptions() {
        if (toChooseFrom.isEmpty()) firstOption = null;
        else firstOption = toChooseFrom.get(navigateChoose * 2);
        if (navigateChoose * 2 + 1 == toChooseFrom.size()) secondOption = null;
        else secondOption = toChooseFrom.get(navigateChoose * 2 + 1);
        setImages();
    }

    private static void setImages() {
        ImageView cardOne = (ImageView) LoginView.choosingScene.lookup("#cardOne");
        ImageView cardTwo = (ImageView) LoginView.choosingScene.lookup("#cardTwo");
        Image imageOne;
        Image imageTwo;
        if (firstOption == null) {
            imageOne = new Image(ShopView.class.getResourceAsStream("cardimages/empty.jpg"));
            LoginView.choosingScene.lookup("#selectOne").setDisable(true);
        } else
            imageOne = new Image(ShopView.class.getResourceAsStream("cardimages/" + firstOption.getName() + ".jpg"));
        if (secondOption == null) {
            imageTwo = new Image(ShopView.class.getResourceAsStream("cardimages/empty.jpg"));
            LoginView.choosingScene.lookup("#selectTwo").setDisable(true);
        } else
            imageTwo = new Image(ShopView.class.getResourceAsStream("cardimages/" + secondOption.getName() + ".jpg"));
        cardOne.setImage(imageOne);
        cardTwo.setImage(imageTwo);
    }

    public static void coin() {
        LoginView.stage.setScene(LoginView.coinScene);
        DuelController.getInstance().coin();
    }

    public static void finishGame(String username) {
        MainView.gameMusic.stop();
        MainView.gameFinishedSong();
        Label label = (Label) LoginView.finishGame.lookup("#congrats");
        label.setText("Congrats " + username + "\n" + "Hold fast to the joy of the rise; despise all thoughts you might descend.");
        label.setStyle("-fx-text-fill: #00F2FF; -fx-font-size: 24");
        LoginView.stage.setScene(LoginView.finishGame);
    }

    public static void summon() {
        DuelController.getInstance().summon();
        MainView.monsterSong();
        setCardImages(player1, player2, LoginView.mainGameSceneOne);
        setCardImages(player2, player1, LoginView.mainGameSceneTwo);
    }

    public static void set() {
        DuelController.getInstance().set();
        MainView.playSpellSong();
        setCardImages(player1, player2, LoginView.mainGameSceneOne);
        setCardImages(player2, player1, LoginView.mainGameSceneTwo);
    }

    public static void activate() {
        DuelController.getInstance().activateSpell();
        Account currentPlayer = (Account) DuelController.getInstance().getGame().getCurrentPlayer();
        setCardImages(player1, player2, LoginView.mainGameSceneOne);
        setCardImages(player2, player1, LoginView.mainGameSceneTwo);
    }


    public void selectOpponentMonster(MouseEvent mouseEvent) {
        int number = Integer.parseInt(((Node) mouseEvent.getTarget()).getId().substring(15));
        selectCard(number, CardStatusInField.MONSTER_FIELD, false);
    }

    public void selectOpponentSpell(MouseEvent mouseEvent) {
        int number = Integer.parseInt(((Node) mouseEvent.getTarget()).getId().substring(13));
        selectCard(number, CardStatusInField.SPELL_FIELD, false);
    }

    public void selectOpponentField() {
        selectCard(0, CardStatusInField.SPELL_FIELD, false);

    }

    public void selectMonster(MouseEvent mouseEvent) {
        int number = Integer.parseInt(((Node) mouseEvent.getTarget()).getId().substring(7));
        selectCard(number, CardStatusInField.MONSTER_FIELD, true);
    }

    public void selectSpell(MouseEvent mouseEvent) {
        int number = Integer.parseInt(((Node) mouseEvent.getTarget()).getId().substring(5));
        selectCard(number, CardStatusInField.SPELL_FIELD, true);
    }

    public void selectField() {
        selectCard(0, CardStatusInField.SPELL_FIELD, true);
    }

    public void selectHand(MouseEvent mouseEvent) {
        int number = Integer.parseInt(((Node) mouseEvent.getTarget()).getId().substring(4));
        selectCard(number, CardStatusInField.HAND, true);
    }

    public void selectCard(int number, CardStatusInField cardStatusInField, boolean isMine) {
        Scene scene;
        if (player1.getUsername().equals(DuelController.getInstance().getGame().getCurrentPlayer().getUsername()))
            scene = LoginView.mainGameSceneOne;
        else
            scene = LoginView.mainGameSceneTwo;
        DuelController.getInstance().selectCard(isMine, cardStatusInField, number - 1);
        setCardImage(DuelController.getInstance().getGame().getSelectedCard(), (ImageView) scene.lookup("#selectedCard"),
                isMine, "selected card");
    }

    public void nextOptions() {
        if (navigateChoose == 0) LoginView.choosingScene.lookup("#back").setDisable(false);
        if (navigateChoose == ((toChooseFrom.size() - 1) / 2) - 1)
            LoginView.choosingScene.lookup("#next").setDisable(true);
        navigateChoose++;
        showOptions();
    }

    public void previousOptions() {
        if (navigateChoose == (toChooseFrom.size() - 1) / 2) {
            LoginView.choosingScene.lookup("#next").setDisable(false);
            LoginView.choosingScene.lookup("#selectTwo").setDisable(false);
        }
        if (navigateChoose == 1) LoginView.choosingScene.lookup("#back").setDisable(true);
        navigateChoose--;
        showOptions();
    }

    public void pickOptionOne() {
        chosen = firstOption;
        choosing.close();
    }

    public void pickOptionTwo() {
        chosen = secondOption;
        choosing.close();
    }

    public void backToFirstPage() {
        LoginView.stage.setScene(LoginView.duelFirstScene);
    }

    public void backToMainPage() {
        LoginView.stage.setScene(LoginView.mainScene);
    }

    public void chooseStarter(String winnerUsername) {
        Label label = (Label) LoginView.coinScene.lookup("#toShow");
        label.setText(winnerUsername + " is our lucky star!\nyou may now choose the\nfirst player your majesty!");
    }

    public String addForChain() {
        TextInputDialog textInputDialog = new TextInputDialog("type the card's name to add it to chain");
        textInputDialog.setHeaderText("The Vast");
        textInputDialog.showAndWait();
        return textInputDialog.getEditor().getText();
    }

    public void cancel() {
        chosen = null;
        choosing.close();
    }

    public String makeChain() {
        String[] numbers = new String[2];
        numbers[0] = "yes";
        numbers[1] = "no";
        ChoiceDialog dialog = new ChoiceDialog(numbers[0], numbers);
        dialog.setHeaderText("The Flesh");
        dialog.setContentText("wanna make a chain?");
        dialog.showAndWait();
        return dialog.getSelectedItem().toString();
    }

    public void start() {
        player1 = (Account) DuelController.getInstance().getGame().getCurrentPlayer();
        player2 = (Account) DuelController.getInstance().getGame().getTheOtherPlayer();
        TextField firstPlayer = (TextField) LoginView.coinScene.lookup("#textField");
        if (DuelController.getInstance().chooseStarter(firstPlayer.getText())) {
            LoginView.stage.setScene(LoginView.mainGameSceneOne);
            secondStage = new Stage();
            secondStage.setScene(LoginView.mainGameSceneTwo);
            secondStage.show();
            run();
        }
        firstPlayer.clear();
    }

    public boolean wantsToActivate(String cardName) {
        String[] numbers = new String[2];
        numbers[0] = "yeah";
        numbers[1] = "nuh";
        ChoiceDialog dialog = new ChoiceDialog(numbers[0], numbers);
        dialog.setHeaderText("The Stranger");
        dialog.setContentText("do you want to activate " + cardName + "?");
        dialog.showAndWait();
        String answer = dialog.getSelectedItem().toString();
        return answer.matches("yeah");
    }

    public int getTribute() {
        ArrayList<MonsterCard> allMonsters = DuelController.getInstance().getGame().getCurrentPlayer().getField().getMonsterCards();
        String[] numbers = new String[allMonsters.size()];
        for (int i = 1; i < allMonsters.size(); i++)
            numbers[i] = i + ":" + allMonsters.get(i).getName();
        ChoiceDialog choiceDialog = new ChoiceDialog(numbers[0], numbers);
        choiceDialog.setContentText("choose the card you seek the blood of:");
        choiceDialog.setHeaderText("The Lonely");
        return Integer.parseInt(choiceDialog.getSelectedItem().toString().substring(0, 1));
    }

    public void deselectButtonClicked() {
        DuelController.getInstance().deselectCard();
        Scene scene;
        if (player1.getUsername().equals(DuelController.getInstance().getGame().getCurrentPlayer().getUsername()))
            scene = LoginView.mainGameSceneOne;
        else
            scene = LoginView.mainGameSceneTwo;
        setCardImage(DuelController.getInstance().getGame().getSelectedCard(), (ImageView) scene.lookup("#selectedCard"),
                false, "selected card");
    }

    public void attackButtonClicked() {
        DuelController.getInstance().directAttack();
        MainView.playAttackSong();
    }

    public void activate() {
        DuelController.getInstance().activateSpell();
        Account currentPlayer = (Account) DuelController.getInstance().getGame().getCurrentPlayer();
        setCardImages(player1, player2, LoginView.mainGameSceneOne);
        setCardImages(player2, player1, LoginView.mainGameSceneTwo);
    }

    public void set() {
        DuelController.getInstance().set();
        MainView.playSpellSong();
        setCardImages(player1, player2, LoginView.mainGameSceneOne);
        setCardImages(player2, player1, LoginView.mainGameSceneTwo);
    }

    public void activateButtonClicked() {
        activate();
    }

    public void setButtonClicked() {
        set();
    }

    public void settingsButtonClicked() {
        popUp();
    }

    private void popUp() {
        Popup popup = new Popup();
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        Button exit = new Button("Exit");
        exit.setOnMouseClicked(mouseEvent -> {
            secondStage.close();
            MainView.gameMusic.pause();
            DuelFirstPage.handleResume();
            LoginView.stage.setScene(LoginView.duelFirstScene);
            popup.hide();
        });
        Button cancelButton = new Button("Cancel");
        hBox.getChildren().add(exit);
        hBox.getChildren().add(cancelButton);
        popup.getContent().add(hBox);
        IO.getInstance().addFunctionButton(cancelButton, popup);
        hBox.setStyle("-fx-background-color: #3c3c3c; -fx-padding: 20px; -fx-font-family: Jokerman; -fx-border-color: #ffffff");
        exit.setStyle("-fx-background-color: #000000; -fx-opacity: 50; -fx-font-family: Jokerman; -fx-text-fill: #ffffff");
        cancelButton.setStyle("-fx-background-color: #000000; -fx-opacity: 50; -fx-font-family: Jokerman; -fx-text-fill: #ffffff");
        popup.show(LoginView.stage);
    }

    public void flipSummonButtonClicked() {
        DuelController.getInstance().flipSummon();
        setCardImages(player1, player2, LoginView.mainGameSceneOne);
        setCardImages(player2, player1, LoginView.mainGameSceneTwo);
    }

    public void summonButtonClicked() {
        summon();
    }

    public void nextPhaseButtonClicked() {
        DuelController.getInstance().nextPhase();
        setCardImages(player1, player2, LoginView.mainGameSceneOne);
        setCardImages(player2, player1, LoginView.mainGameSceneTwo);
    }

    public void previous() {
        count--;
        handleButtons();
        showCards();
    }

    public void next() {
        count++;
        handleButtons();
        showCards();
    }

    public void backToGame() {
        if (DuelController.getInstance().getGame().getCurrentPlayer().getUsername().equals(player1.getUsername()))
            LoginView.stage.setScene(LoginView.mainGameSceneOne);
        else
            secondStage.setScene(LoginView.mainGameSceneTwo);
    }

    public void showGraveyardButtonClicked() {
        myGY = new ArrayList<>();
        opponentGY = new ArrayList<>();
        ArrayList<Card> myGraveYard = DuelController.getInstance().getGame().getCurrentPlayer().getField().getGraveyard();
        ArrayList<Card> opponentGraveYard = DuelController.getInstance().getGame().getTheOtherPlayer().getField().getGraveyard();
        for (Card card : myGraveYard) myGY.add(card.getName());
        for (Card card : opponentGraveYard) opponentGY.add(card.getName());
        count = 0;
        showCards();
        LoginView.setSize(LoginView.graveyardScene);
        if (DuelController.getInstance().getGame().getCurrentPlayer().getUsername().equals(player1.getUsername())) {
            LoginView.stage.setScene(LoginView.graveyardScene);
            LoginView.stage.centerOnScreen();
        } else {
            secondStage.setScene(LoginView.graveyardScene);
            secondStage.centerOnScreen();
        }
        handleButtons();
    }

    private void handleButtons() {
        LoginView.graveyardScene.lookup("#backToFirstPage").setDisable(count == 0);
        LoginView.graveyardScene.lookup("#next").setDisable(count >= myGY.size() - 1 && count >= opponentGY.size() - 1);
    }

    private void showCards() {
        if (count < 0) count = 0;
        ImageView first = (ImageView) LoginView.graveyardScene.lookup("#first");
        first.setImage(myGYImage());
        ImageView second = (ImageView) LoginView.graveyardScene.lookup("#second");
        second.setImage(opponentGYImage());
    }

    private Image opponentGYImage() {
        Image secondImage;
        if (opponentGY.size() > count) {
            secondCard = Card.getCardByName(opponentGY.get(count));
            if (!firstCard.isOriginal() || firstCard.isConverted())
                secondImage = new Image(DuelView.class.getResourceAsStream("cardimages/JonMartin.jpg"));
            else
                secondImage = new Image(DuelView.class.getResourceAsStream("cardimages/" + secondCard.getName() + ".jpg"));
        } else {
            secondCard = EMPTY_CARD;
            secondImage = new Image(DuelView.class.getResourceAsStream("cardimages/" + "empty.jpg"));
        }
        return secondImage;
    }

    private Image myGYImage() {
        Image firstImage;
        if (myGY.size() > count) {
            firstCard = Card.getCardByName(myGY.get(count));
            if (!firstCard.isOriginal() || firstCard.isConverted())
                firstImage = new Image(DuelView.class.getResourceAsStream("cardimages/JonMartin.jpg"));
            else
                firstImage = new Image(DuelView.class.getResourceAsStream("cardimages/" + firstCard.getName() + ".jpg"));
        } else {
            firstImage = new Image(DuelView.class.getResourceAsStream("cardimages/" + "empty.jpg"));
            firstCard = EMPTY_CARD;
        }
        return firstImage;
    }

    public void muteGame(MouseEvent mouseEvent) {
        MainView.isGameMute = ((ToggleButton) mouseEvent.getTarget()).isSelected();
        if (MainView.isGameMute) {
            MainView.gameMusic.pause();
            if (MainView.gameFinished != null) MainView.gameFinished.pause();
        } else if (MainView.gameMusic == null) MainView.playGameMusic();
        else MainView.gameMusic.play();
    }

    public void changeBackgroundButtonClicked() {
        Scene scene;
        if (DuelController.getInstance().getGame().getCurrentPlayer().getUsername().equals(player1.getUsername()))
            scene = LoginView.mainGameSceneOne;
        else
            scene = LoginView.mainGameSceneTwo;
        if (counter == 17)
            counter = 0;
        GridPane content = (GridPane) scene.lookup("#gridpane");
        content.setBackground(new Background(
                new BackgroundImage(
                        new Image(DuelView.class.getResourceAsStream("stylesheets/" + counter + ".jpg")),
                        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
                        new BackgroundPosition(Side.LEFT, 0, true, Side.BOTTOM, 0, true),
                        new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true)
                ))
        );
        counter++;
    }


    public MonsterCard getRitualCard() {
//        IO.getInstance().chooseRitualCard();
//        String ritualCardNumber = IO.getInstance().getInputMessage();
//        if (ritualCardNumber.equals("cancel")) return null;
//        try {
//            return (MonsterCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand().get(Integer.parseInt(ritualCardNumber) - 1);
//        } catch (Exception e) {
//            return getRitualCard();
//        }
        ArrayList<Card> hand = DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand();
        toChooseFrom = new ArrayList<>();
        for (Card card : hand)
            if (card instanceof MonsterCard && ((MonsterCard) card).getCardType().equals("Ritual"))
                toChooseFrom.add(card);
        chooseCard();
        return (MonsterCard) chosen;
    }


    public MonsterCard getOpponentMonster() {
//        IO.getInstance().chooseMonster();
//        String monsterCardNumber = IO.getInstance().getInputMessage();
//        if (monsterCardNumber.equals("cancel")) return null;
//        try {
//            return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getMonsterCards().get(Integer.parseInt(monsterCardNumber) - 1);
//        } catch (Exception e) {
//            return getOpponentMonster();
//        }
        toChooseFrom = new ArrayList<>(DuelController.getInstance().getGame().getTheOtherPlayer().getField().getMonsterCards());
        chooseCard();
        return (MonsterCard) chosen;
    }


    public ArrayList<MonsterCard> getTributeMonsterCards() {
        TextInputDialog textInputDialog = new TextInputDialog("enter the number of cards with a space between.\n" +
                "if you changed your mind, type cancel.");
        textInputDialog.setHeaderText("The Hunt");
        textInputDialog.showAndWait();
        var input = textInputDialog.getEditor().getText();
        if (input.equals("cancel")) return null;
        return Arrays.stream(input.split(" ")).map(Integer::parseInt).map(i -> DuelController.getInstance().getGame().getCurrentPlayer().getField().getMonsterCards().get(i - 1)).collect(Collectors.toCollection(ArrayList::new));
    }


    public String monsterMode() {
        String[] numbers = new String[2];
        numbers[0] = "Attack";
        numbers[1] = "Defense";
        ChoiceDialog dialog = new ChoiceDialog(numbers[0], numbers);
        dialog.setHeaderText("The Flesh");
        dialog.setContentText("Choose your monster's mode");
        dialog.showAndWait();
        return dialog.getSelectedItem().toString();
    }


    public MonsterCard getMonsterCardFromHand() {
//        IO.getInstance().chooseMonster();
//        String number = IO.getInstance().getInputMessage();
//        if (number.equals("cancel")) return null;
//        try {
//            return (MonsterCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand().get(Integer.parseInt(number) - 1);
//        } catch (Exception e) {
//            return getMonsterCardFromHand();
//        }
        toChooseFrom = new ArrayList<>(DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand());
        chooseCard();
        return (MonsterCard) chosen;
    }


    public MonsterCard getFromMyGY() {
//        IO.getInstance().chooseMonster();
//        String number = IO.getInstance().getInputMessage();
//        if (number.equals("cancel")) return null;
//        try {
//            return (MonsterCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getGraveyard().get(Integer.parseInt(number) - 1);
//        } catch (Exception e) {
//            return getFromMyGY();
//        }
        ArrayList<Card> graveyard = DuelController.getInstance().getGame().getCurrentPlayer().getField().getGraveyard();
        toChooseFrom = new ArrayList<>();
        for (Card card : graveyard)
            if (card instanceof MonsterCard)
                toChooseFrom.add(card);
        chooseCard();
        return (MonsterCard) chosen;
    }


    public Card getCardFromMyGY() {
//        IO.getInstance().chooseCard();
//        String number = IO.getInstance().getInputMessage();
//        if (number.equals("cancel")) return null;
//        try {
//            return DuelController.getInstance().getGame().getCurrentPlayer().getField().getGraveyard().get(Integer.parseInt(number) - 1);
//        } catch (Exception e) {
//            return getCardFromMyGY();
//        }
        toChooseFrom = new ArrayList<>(DuelController.getInstance().getGame().getCurrentPlayer().getField().getGraveyard());
        chooseCard();
        return chosen;
    }


    public Card getCardFromOpponentGY() {
//        IO.getInstance().chooseCard();
//        String number = IO.getInstance().getInputMessage();
//        if (number.equals("cancel")) return null;
//        try {
//            return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getGraveyard().get(Integer.parseInt(number) - 1);
//        } catch (Exception e) {
//            return getCardFromOpponentGY();
//        }
        toChooseFrom = new ArrayList<>(DuelController.getInstance().getGame().getTheOtherPlayer().getField().getGraveyard());
        chooseCard();
        return chosen;
    }


    public MonsterCard getFromOpponentGY() {
//        IO.getInstance().chooseMonster();
//        String number = IO.getInstance().getInputMessage();
//        if (number.equals("cancel")) return null;
//        try {
//            return (MonsterCard) DuelController.getInstance().getGame().getTheOtherPlayer().getField().getGraveyard().get(Integer.parseInt(number) - 1);
//        } catch (Exception e) {
//            return getFromOpponentGY();
//        }
        ArrayList<Card> graveyard = DuelController.getInstance().getGame().getTheOtherPlayer().getField().getGraveyard();
        toChooseFrom = new ArrayList<>();
        for (Card card : graveyard)
            if (card instanceof MonsterCard)
                toChooseFrom.add(card);
        chooseCard();
        return (MonsterCard) chosen;
    }


    public boolean isMine() {
        String[] numbers = new String[2];
        numbers[0] = "yeah";
        numbers[1] = "nuh";
        ChoiceDialog dialog = new ChoiceDialog(numbers[0], numbers);
        dialog.setHeaderText("The Eye");
        dialog.setContentText("wanna choose from your own cards? self destruction 100");
        dialog.showAndWait();
        String answer = dialog.getSelectedItem().toString();
        return answer.matches("yeah");
    }


    public SpellAndTrapCard getFieldSpellFromDeck() {
//        IO.getInstance().printString(DuelController.getInstance().sortFieldCards());
//        IO.getInstance().chooseFieldSpell();
//        String number = IO.getInstance().getInputMessage();
//        if (number.equals("cancel")) return null;
//        try {
//            return (SpellAndTrapCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getDeckZone().get(Integer.parseInt(number) - 1);
//        } catch (Exception e) {
//            return getFieldSpellFromDeck();
//        }
        ArrayList<Card> deck = DuelController.getInstance().getGame().getCurrentPlayer().getField().getDeckZone();
        toChooseFrom = new ArrayList<>();
        for (Card card : deck)
            if (card instanceof SpellAndTrapCard && ((SpellAndTrapCard) card).getProperty().equals("Field"))
                toChooseFrom.add(card);
        chooseCard();
        return (SpellAndTrapCard) chosen;
    }


    public MonsterCard getMonsterToEquip() {
//        IO.getInstance().chooseMonster();
//        String monsterCardNumber = IO.getInstance().getInputMessage();
//        if (monsterCardNumber.equals("cancel")) return null;
//        try {
//            return DuelController.getInstance().getGame().getCurrentPlayer().getField().getMonsterCards().get(Integer.parseInt(monsterCardNumber) - 1);
//        } catch (Exception e) {
//            return getMonsterToEquip();
//        }
        toChooseFrom = new ArrayList<>(DuelController.getInstance().getGame().getCurrentPlayer().getField().getMonsterCards());
        chooseCard();
        return (MonsterCard) chosen;
    }


    public MonsterCard getHijackedCard() {
//        IO.getInstance().chooseMonster();
//        String monsterCardNumber = IO.getInstance().getInputMessage();
//        if (monsterCardNumber.equals("cancel")) return null;
//        try {
//            return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getMonsterCards().get(Integer.parseInt(monsterCardNumber) - 1);
//        } catch (Exception e) {
//            return getHijackedCard();
//        }
        toChooseFrom = new ArrayList<>(DuelController.getInstance().getGame().getTheOtherPlayer().getField().getMonsterCards());
        chooseCard();
        return (MonsterCard) chosen;
    }


    public boolean wantsToActivateTrap(String name, String username) {
        if (DuelController.getInstance().handleMirageDragon(username))
            return false;
        String[] numbers = new String[2];
        numbers[0] = "hell yes";
        numbers[1] = "sorry to disappoint";
        ChoiceDialog dialog = new ChoiceDialog(numbers[0], numbers);
        dialog.setHeaderText("The Spiral");
        dialog.setContentText("wanna activate " + name + " and burn the place down?");
        dialog.showAndWait();
        String answer = dialog.getSelectedItem().toString();
        return answer.matches("hell yes");
    }


    public boolean ordinaryOrSpecial() {
        String[] numbers = new String[2];
        numbers[0] = "ordinary";
        numbers[1] = "special";
        ChoiceDialog dialog = new ChoiceDialog(numbers[0], numbers);
        dialog.setHeaderText("The Spiral");
        dialog.setContentText("Straight or Spiral?");
        dialog.showAndWait();
        String answer = dialog.getSelectedItem().toString();
        return !answer.matches("ordinary");
    }


    public int numOfSpellCardsToDestroy() {
        String[] numbers = new String[2];
        numbers[0] = "1";
        numbers[1] = "2";
        ChoiceDialog dialog = new ChoiceDialog(numbers[0], numbers);
        dialog.setHeaderText("The Desolation");
        dialog.setContentText("how many cards do you want to throw in hell?");
        dialog.showAndWait();
        String answer = dialog.getSelectedItem().toString();
        return Integer.parseInt(answer);
    }


    public Card getCardFromHand() {
//        IO.getInstance().chooseCard();
//        String cardNumber = IO.getInstance().getInputMessage();
//        if (cardNumber.matches("cancel")) return null;
//        try {
//            return DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand().get(Integer.parseInt(cardNumber) - 1);
//        } catch (Exception e) {
//            return getCardFromHand();
//        }
        toChooseFrom = new ArrayList<>(DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand());
        chooseCard();
        return chosen;
    }


    public Card getCardFromOpponentHand() {
//        IO.getInstance().chooseCard();
//        String cardNumber = IO.getInstance().getInputMessage();
//        if (cardNumber.matches("cancel")) return null;
//        try {
//            return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getHand().get(Integer.parseInt(cardNumber) - 1);
//        } catch (Exception e) {
//            return getCardFromOpponentHand();
//        }
        toChooseFrom = new ArrayList<>(DuelController.getInstance().getGame().getTheOtherPlayer().getField().getHand());
        chooseCard();
        return chosen;
    }


    public boolean summonGateGuardian() {
        String[] numbers = new String[2];
        numbers[0] = "hell yes";
        numbers[1] = "sorry to disappoint";
        ChoiceDialog dialog = new ChoiceDialog(numbers[0], numbers);
        dialog.setHeaderText("The Dark");
        dialog.setContentText("do you want to summon this card with three tributes?");
        dialog.showAndWait();
        String answer = dialog.getSelectedItem().toString();
        return answer.equals("hell yes");
    }


    public int barbaros() {
        String[] numbers = new String[3];
        numbers[0] = "1: basic 2 tributes";
        numbers[1] = "2: too coward to kill anyone, prefering attack reduction";
        numbers[2] = "3: three tributes full murder mood on";
        ChoiceDialog dialog = new ChoiceDialog(numbers[0], numbers);
        dialog.setHeaderText("The Corruption");
        dialog.setContentText("how to summon the hell-hound:");
        dialog.showAndWait();
        String answer = dialog.getSelectedItem().toString().substring(0, 1);
        return Integer.parseInt(answer);
    }


    public boolean killMessengerOfPeace() {
        String[] numbers = new String[2];
        numbers[0] = "hell yes";
        numbers[1] = "sorry to disappoint";
        ChoiceDialog dialog = new ChoiceDialog(numbers[0], numbers);
        dialog.setHeaderText("The Spiral");
        dialog.setContentText("how about killing the messenger of peace?\npeace is overrated anyway...");
        dialog.showAndWait();
        String answer = dialog.getSelectedItem().toString();
        return answer.equals("hell yes");
    }


    public SpellAndTrapCard getFromMyField() {
//        String spellNumber = IO.getInstance().getInputMessage();
//        if (spellNumber.matches("cancel")) return null;
//        try {
//            return DuelController.getInstance().getGame().getCurrentPlayer().getField().getSpellAndTrapCards().get(Integer.parseInt(spellNumber) - 1);
//        } catch (Exception e) {
//            return getFromMyField();
//        }
        toChooseFrom = new ArrayList<>(DuelController.getInstance().getGame().getCurrentPlayer().getField().getSpellAndTrapCards());
        chooseCard();
        return (SpellAndTrapCard) chosen;
    }


    public SpellAndTrapCard getFromOpponentField() {
//        String spellNumber = IO.getInstance().getInputMessage();
//        if (spellNumber.matches("cancel")) return null;
//        try {
//            return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getSpellAndTrapCards().get(Integer.parseInt(spellNumber) - 1);
//        } catch (Exception e) {
//            return getFromOpponentField();
//        }
        toChooseFrom = new ArrayList<>(DuelController.getInstance().getGame().getTheOtherPlayer().getField().getSpellAndTrapCards());
        chooseCard();
        return (SpellAndTrapCard) chosen;
    }


    public int whereToSummonFrom() {
        String[] numbers = new String[3];
        numbers[0] = "1 hand";
        numbers[1] = "2 deck";
        numbers[3] = "3 graveyard";
        ChoiceDialog dialog = new ChoiceDialog(numbers[0], numbers);
        dialog.setHeaderText("The Buried");
        dialog.setContentText("summon the hell-hound from:");
        dialog.showAndWait();
        return Integer.parseInt(dialog.getSelectedItem().toString().substring(0, 1));
    }


    public String getCardName() {
        TextInputDialog textInputDialog = new TextInputDialog("enter the name of the card you intend to murder");
        textInputDialog.setHeaderText("The Slaughter");
        textInputDialog.showAndWait();
        return textInputDialog.getEditor().getText();
    }


    public boolean wantsToExchange() {
        String[] numbers = new String[2];
        numbers[0] = "yeah";
        numbers[1] = "nuh";
        ChoiceDialog dialog = new ChoiceDialog(numbers[0], numbers);
        dialog.setHeaderText("The Eye");
        dialog.setContentText("wanna exchange?");
        dialog.showAndWait();
        String answer = dialog.getSelectedItem().toString();
        return answer.matches("yeah");
    }


    public String[] cardsToExchange() {
        IO.getInstance().cardsToExchange();
        //card from side deck
        // *
        //card from main deck
        return IO.getInstance().getInputMessage().split(" \\* ");
    }


    public MonsterCard getFromMyGY(boolean isOpponent) {
//        IO.getInstance().chooseMonster();
//        String number = IO.getInstance().getInputMessage();
//        if (number.equals("cancel")) return null;
//        try {
//            return (MonsterCard) DuelController.getInstance().getGame().getTheOtherPlayer().getField().getGraveyard().get(Integer.parseInt(number) - 1);
//        } catch (Exception e) {
//            return getFromMyGY(isOpponent);
//        }
        return getFromOpponentGY();
    }


    public MonsterCard getMonsterCardFromHand(boolean isOpponent) {
//        IO.getInstance().chooseMonster();
//        String number = IO.getInstance().getInputMessage();
//        if (number.equals("cancel")) return null;
//        try {
//            return (MonsterCard) DuelController.getInstance().getGame().getTheOtherPlayer().getField().getHand().get(Integer.parseInt(number) - 1);
//        } catch (Exception e) {
//            return getMonsterCardFromHand(isOpponent);
//        }
        ArrayList<Card> hand = DuelController.getInstance().getGame().getTheOtherPlayer().getField().getHand();
        toChooseFrom = new ArrayList<>();
        for (Card card : hand)
            if (card instanceof MonsterCard)
                toChooseFrom.add(card);
        chooseCard();
        return (MonsterCard) chosen;
    }
}