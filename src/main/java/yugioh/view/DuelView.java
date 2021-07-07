package yugioh.view;


import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import com.fasterxml.jackson.annotation.JsonFormat;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape3D;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import yugioh.controller.DuelController;
import yugioh.model.Account;
import yugioh.model.CardStatusInField;
import yugioh.model.MonsterCardModeInField;
import yugioh.model.cards.Card;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;
import yugioh.utils.DragUtils;
import yugioh.utils.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.stream.Collectors;


public class DuelView {

    private static DuelView singleInstance;
    private static ArrayList<String> myGY;
    private static ArrayList<String> opponentGY;
    private static int count;
    private static Card firstCard;
    private static Card secondCard;
    private static final Card emptyCard = new Card();
    private static int counter;
    public static Stage secondStage;
    private static Account player1;
    private static Account player2;

    public static DuelView getInstance() {
        if (singleInstance == null) singleInstance = new DuelView();
        return singleInstance;
    }

    public static void run() {
        handleTurn((Account) DuelController.getInstance().getGame().getCurrentPlayer());
        setCardImages(player1, player2, LoginView.mainGameSceneOne);
        setCardImages(player2, player1, LoginView.mainGameSceneTwo);
        makeCardsDraggable(LoginView.mainGameSceneOne);
        makeCardsDraggable(LoginView.mainGameSceneTwo);
        handleOpponentCardsDrop(LoginView.mainGameSceneOne);
        handleOpponentCardsDrop(LoginView.mainGameSceneTwo);
        setProfiles(player1, player2, LoginView.mainGameSceneOne);
        setProfiles(player2, player1, LoginView.mainGameSceneTwo);
        setLP(player1.getUsername(), player1.getLP());
        setLP(player2.getUsername(), player2.getLP());
    }

    private static void makeCardsDraggable(Scene scene) {
        for (var i = 1; i <= 5; i++) {
            setDraggable(scene.lookup("#monster" + i));
            setDraggable(scene.lookup("#spell" + i));
        }
        for (var i = 1; i <= 6; i++) {
            setDraggable(scene.lookup("#hand" + i));
        }
        setDraggable(scene.lookup("#field"));
    }

    private static void setDraggable(Node node) {
        if (Objects.isNull(node)) return;
        DragUtils.DragHandler dragHandler = new DragUtils.DragHandler(node);
        dragHandler.addListener((listenerDragHandler, listenerDragEvent) -> dragHandler.getDragNodes().forEach((dragNode, coordination) -> {
            if (listenerDragEvent == DragUtils.Event.DRAG_START)
                DuelController.getInstance().selectCard(true, CardStatusInField.MONSTER_FIELD, Integer.parseInt(dragNode.getId().substring(dragNode.getId().length() - 1)) - 1);
            if (listenerDragEvent == DragUtils.Event.DRAG_END) {
                if (dragNode.getId().startsWith("monster")) {
                    var target = getCardByCoordination(new Point2D(dragHandler.getLastMouseX(), dragHandler.getLastMouseY()), node.getScene());
                    if (Objects.nonNull(target) && !target.getFirst() && target.getSecond() == CardStatusInField.MONSTER_FIELD)
                        DuelController.getInstance().attack(target.getThird() - 1);
                }
                var transition = new TranslateTransition();
                transition.setNode(dragNode);
                transition.setToX(coordination.get(0));
                transition.setToY(coordination.get(1));
                transition.setDuration(Duration.seconds(DragUtils.getDurationByCoordination(coordination.get(0), coordination.get(1), dragNode.getTranslateX(), dragNode.getTranslateY())));
                transition.play();
            }
        }));
    }

    private static void handleOpponentCardsDrop(Scene scene) {
        for (var i = 1; i <= 5; i++)
            makeOpponentMonsterAttackable(scene.lookup("#opponentMonster" + i));
    }


    private static void makeOpponentMonsterAttackable(Node node) {
        // TODO: 7/7/2021 a failed project, check if I can pull this off
//        setOpponentMonsterOnDragEntered(node);
//        setOpponentMonsterOnDragExited(node);
//        setOpponentMonsterOnDragDone(node);
    }

//    private static void setOpponentMonsterOnDragDone(Node node) {
//        node.setOnDragDone(event -> {
//            System.out.println("drag done");
//            node.setStyle("box-shadow: 0px 0px FFFFFF;");
//            event.consume();
//        });
//    }

//    private static void setOpponentMonsterOnDragExited(Node node) {
//        node.setOnDrag(event -> {
//            System.out.println("drag exited");
//            node.setStyle("box-shadow: 0px 0px FFFFFF;");
//            event.consume();
//        });
//    }

//    private static void setOpponentMonsterOnDragEntered(Node node) {
//        node.addEventHandler(DragEvent.DRAG_ENTERED_TARGET, event -> {
//            event.acceptTransferModes(TransferMode.ANY);
//            System.out.println("drag entered");
//            node.setStyle("box-shadow: 5px 10px F00000;");
//            event.consume();
//        });
//    }

    private static Triple<Boolean, CardStatusInField, Integer> getCardByCoordination(Point2D point, Scene scene) {
        if (scene.lookup("#opponentField").getBoundsInParent().contains(point))
            return new Triple<>(false, CardStatusInField.FIELD_ZONE, 0);
        for (var i = 1; i <= 5; i++) {
            if (scene.lookup("#opponentMonster" + i).getBoundsInParent().contains(point))
                return new Triple<>(false, CardStatusInField.MONSTER_FIELD, i);
            if (scene.lookup("#opponentSpell" + i).getBoundsInParent().contains(point))
                return new Triple<>(false, CardStatusInField.SPELL_FIELD, i);
            if (scene.lookup("#monster" + i).getBoundsInParent().contains(point))
                return new Triple<>(true, CardStatusInField.MONSTER_FIELD, i);
            if (scene.lookup("#spell" + i).getBoundsInParent().contains(point))
                return new Triple<>(true, CardStatusInField.SPELL_FIELD, i);
        }
        for (var i = 1; i <= 6; i++)
            if (scene.lookup("#hand" + i).getBoundsInParent().contains(point))
                return new Triple<>(true, CardStatusInField.HAND, i);
        if (scene.lookup("#field").getBoundsInParent().contains(point))
            return new Triple<>(true, CardStatusInField.FIELD_ZONE, 0);
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
                false, "selected card");
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


//    public void run() {
//        String command;
//        while (!DuelController.getInstance().getGame().isGameFinished() &&
//                !(command = IO.getInstance().getInputMessage()).matches("(?:menu )?exit") &&
//                !command.matches("(?:menu )?enter [Mm]ain(?: menu)?")) {
//            Matcher selectCardMatcher = selectCardPattern.matcher(command);
//            Matcher attackMatcher = attackPattern.matcher(command);
//            Matcher cheatDecreaseLPMatcher = cheatDecreaseLPPattern.matcher(command);
//            Matcher cheatIncreaseLPMatcher = cheatIncreaseLPPattern.matcher(command);
//            if (command.matches("(?:menu )?s(?:how)?-c(?:urrent)?"))
//                showCurrentMenu();
//            else if (command.matches("select -d|deselect"))
//                DuelController.getInstance().deselectCard();
//            else if (selectCardMatcher.matches())
//                selectCard(selectCardMatcher, !command.contains("-o"), CardStatusInField.getCardStatusInField(command));
//            else if (command.matches("sum(?:mon)?"))
//                DuelController.getInstance().summon();
//            else if (command.matches("set?"))
//                DuelController.getInstance().set();
//            else if (command.matches("set -(?:-position|p) (?:att(?:ack)?|def(?:ense)?)"))
//                DuelController.getInstance().setPosition(command.contains("att"));
//            else if (command.matches("f(?:lip)?-sum(?:mon)?"))
//                DuelController.getInstance().flipSummon();
//            else if (attackMatcher.matches())
//                DuelController.getInstance().attack(Integer.parseInt(attackMatcher.group("number")) - 1);
//            else if (command.matches("att(?:ack)? d(?:ir(?:ect)?)?"))
//                DuelController.getInstance().directAttack();
//            else if (command.matches("activ(?:at)?e(?: effect)?"))
//                DuelController.getInstance().activateSpell();
//            else if (command.matches("(?:show )?grave(?:yard)?"))
//                showGraveyard();
//            else if (command.matches("(?:c(?:ard)? )?show -(?:-select(?:ed)?|s)"))
//                DuelController.getInstance().showSelectedCard();
//            else if (command.matches("sur(?:render)?"))
//                DuelController.getInstance().surrender();
//            else if (command.matches("next(?: phase)?"))
//                DuelController.getInstance().nextPhase();
//            else if (cheatDecreaseLPMatcher.matches())
//                DuelController.getInstance().cheatDecreaseLP(Integer.parseInt(cheatDecreaseLPMatcher.group("number")));
//            else if (cheatIncreaseLPMatcher.matches())
//                DuelController.getInstance().cheatIncreaseLP(Integer.parseInt(cheatIncreaseLPMatcher.group("number")));
//            else if (command.matches("Person(?: of)? Interest"))
//                DuelController.getInstance().cheatSeeMyDeck();
//            else if (command.matches("Conspiracy(?: to)? Commit Treason"))
//                DuelController.getInstance().cheatSetWinner();
//            else if (command.matches("Drunk Space Pirate"))
//                DuelController.getInstance().cheatShowRivalHand();
//            else IO.getInstance().printInvalidCommand();
//        }
//    }


    public static void coin() {
        LoginView.stage.setScene(LoginView.coinScene);
        DuelController.getInstance().coin();
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
        IO.getInstance().wantToActivate(cardName);
        return IO.getInstance().getInputMessage().toLowerCase().matches("y(?:es)?");
    }


    public int getTribute() {
        IO.getInstance().chooseTribute();
        return Integer.parseInt(IO.getInstance().getInputMessage()) - 1;
    }


    private void selectCard(Matcher matcher, boolean isPlayersCard, CardStatusInField cardStatusInField) {
        DuelController.getInstance().selectCard(isPlayersCard, cardStatusInField,
                cardStatusInField == CardStatusInField.FIELD_ZONE ? 0 : Integer.parseInt(matcher.group("number")) - 1);
    }

    public void select() {

    }

    public void deselect() {
        DuelController.getInstance().deselectCard();
        Scene scene;
        if (player1.getUsername().equals(DuelController.getInstance().getGame().getCurrentPlayer().getUsername()))
            scene = LoginView.mainGameSceneOne;
        else
            scene = LoginView.mainGameSceneTwo;
        setCardImage(DuelController.getInstance().getGame().getSelectedCard(), (ImageView) scene.lookup("#selectedCard"),
                false, "selected card");
    }

    public void attack() {

    }

    public static void finishGame(String username) {
        Label label = (Label) LoginView.finishGame.lookup("#congrats");
        label.setText("Congrats " + username + "\n" + "Hold fast to the joy of the rise; despise all thoughts you might descend.");
        label.setStyle("-fx-text-fill: #00F2FF");
        LoginView.stage.setScene(LoginView.finishGame);
    }

    public void activate() {
        DuelController.getInstance().activateSpell();
        Account currentPlayer = (Account) DuelController.getInstance().getGame().getCurrentPlayer();
        Scene myScene = LoginView.mainGameSceneOne;
        Scene opponentScene = LoginView.mainGameSceneTwo;
        if (player2.getUsername().equals(currentPlayer.getUsername())) {
            myScene = LoginView.mainGameSceneTwo;
            opponentScene = LoginView.mainGameSceneOne;
        }
        setHandImages(currentPlayer, myScene);
        setSpellZone(currentPlayer.getField().getSpellAndTrapCards(), myScene, "#spell");
        setSpellZone(currentPlayer.getField().getSpellAndTrapCards(), opponentScene, "#opponentSpell");
        setCardImage(DuelController.getInstance().getGame().getSelectedCard(), (ImageView) myScene.lookup("#selectedCard"),
                false, "selected card");
    }

    public void set() {
        DuelController.getInstance().set();
        Scene myScene = LoginView.mainGameSceneOne;
        Scene opponentScene = LoginView.mainGameSceneTwo;
        Account currentPlayer = (Account) DuelController.getInstance().getGame().getCurrentPlayer();
        if (player2.getUsername().equals(currentPlayer.getUsername())) {
            myScene = LoginView.mainGameSceneTwo;
            opponentScene = LoginView.mainGameSceneOne;
        }
        setHandImages(currentPlayer, myScene);
        setSpellZone(currentPlayer.getField().getSpellAndTrapCards(), myScene, "#spell");
        setMonsterZone(currentPlayer.getField().getMonsterCards(), myScene, "#monster");
        setSpellZone(currentPlayer.getField().getSpellAndTrapCards(), opponentScene, "#opponentSpell");
        setMonsterZone(currentPlayer.getField().getMonsterCards(), opponentScene, "#opponentMonster");
        setCardImage(DuelController.getInstance().getGame().getSelectedCard(), (ImageView) myScene.lookup("#selectedCard"),
                false, "selected card");
    }

    public void settings() {
        popUp();
    }

    private void popUp() {
        Popup popup = new Popup();
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        Button exit = new Button("Exit");
        exit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                secondStage.close();
                DuelFirstPage.handleResume();
                LoginView.stage.setScene(LoginView.duelFirstScene);
                popup.hide();
            }
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

    public void flipSummon() {
        DuelController.getInstance().flipSummon();
        Scene myScene = LoginView.mainGameSceneOne;
        Account currentPlayer = (Account) DuelController.getInstance().getGame().getCurrentPlayer();
        Scene opponentScene = LoginView.mainGameSceneTwo;
        if (player2.getUsername().equals(currentPlayer.getUsername())) {
            myScene = LoginView.mainGameSceneTwo;
            opponentScene = LoginView.mainGameSceneOne;
        }
        setMonsterZone(currentPlayer.getField().getMonsterCards(), myScene, "#monster");
        setMonsterZone(currentPlayer.getField().getMonsterCards(), opponentScene, "#opponentMonster");
        setCardImage(DuelController.getInstance().getGame().getSelectedCard(), (ImageView) myScene.lookup("#selectedCard"),
                false, "selected card");
    }

    public void summon() {
        DuelController.getInstance().summon();
        Scene opponentScene = LoginView.mainGameSceneTwo;
        Account currentPlayer = (Account) DuelController.getInstance().getGame().getCurrentPlayer();
        Scene myScene = LoginView.mainGameSceneOne;
        if (player2.getUsername().equals(currentPlayer.getUsername())) {
            myScene = LoginView.mainGameSceneTwo;
            opponentScene = LoginView.mainGameSceneOne;
        }
        setHandImages(currentPlayer, myScene);
        setMonsterZone(currentPlayer.getField().getMonsterCards(), myScene, "#monster");
        setMonsterZone(currentPlayer.getField().getMonsterCards(), opponentScene, "#opponentMonster");
        setCardImage(DuelController.getInstance().getGame().getSelectedCard(), (ImageView) myScene.lookup("#selectedCard"),
                false, "selected card");
    }

    public void nextPhase() {
        DuelController.getInstance().nextPhase();
        setHandImages(player1, LoginView.mainGameSceneOne);
        setSpellZone(player1.getField().getSpellAndTrapCards(), LoginView.mainGameSceneOne, "#spell");
        setMonsterZone(player1.getField().getMonsterCards(), LoginView.mainGameSceneOne, "#monster");
        setHandImages(player2, LoginView.mainGameSceneTwo);
        setSpellZone(player2.getField().getSpellAndTrapCards(), LoginView.mainGameSceneTwo, "#spell");
        setMonsterZone(player2.getField().getMonsterCards(), LoginView.mainGameSceneTwo, "#monster");
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
        LoginView.stage.setScene(LoginView.mainGameSceneOne);
    }

    public void showGraveyard() {
        myGY = new ArrayList<>();
        opponentGY = new ArrayList<>();
        ArrayList<Card> myGraveYard = DuelController.getInstance().getGame().getCurrentPlayer().getField().getGraveyard();
        ArrayList<Card> opponentGraveYard = DuelController.getInstance().getGame().getTheOtherPlayer().getField().getGraveyard();
        for (Card card : myGraveYard) myGY.add(card.getName());
        for (Card card : opponentGraveYard) opponentGY.add(card.getName());
        count = 0;
        showCards();
        LoginView.setSize(LoginView.graveyardScene);
        LoginView.stage.setScene(LoginView.graveyardScene);
        LoginView.stage.centerOnScreen();
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
            secondCard = emptyCard;
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
            firstCard = emptyCard;
        }
        return firstImage;
    }


    public MonsterCard getRitualCard() {
        IO.getInstance().chooseRitualCard();
        String ritualCardNumber = IO.getInstance().getInputMessage();
        if (ritualCardNumber.equals("cancel")) return null;
        try {
            return (MonsterCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand().get(Integer.parseInt(ritualCardNumber) - 1);
        } catch (Exception e) {
            return getRitualCard();
        }
    }


    public MonsterCard getOpponentMonster() {
        IO.getInstance().chooseMonster();
        String monsterCardNumber = IO.getInstance().getInputMessage();
        if (monsterCardNumber.equals("cancel")) return null;
        try {
            return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getMonsterCards().get(Integer.parseInt(monsterCardNumber) - 1);
        } catch (Exception e) {
            return getOpponentMonster();
        }
    }


    public ArrayList<MonsterCard> getTributes() {
        IO.getInstance().chooseTributes();
        try {
            return getTributeMonsterCards();
        } catch (Exception exception) {
            return getTributesAgain();
        }
    }


    private ArrayList<MonsterCard> getTributesAgain() {
        try {
            return getTributeMonsterCards();
        } catch (Exception exception) {
            return getTributesAgain();
        }
    }


    private ArrayList<MonsterCard> getTributeMonsterCards() {
        var input = IO.getInstance().getInputMessage();
        if (input.equals("cancel")) return null;
        return Arrays.stream(input.split(" ")).map(Integer::parseInt).map(i -> DuelController.getInstance().getGame().getCurrentPlayer().getField().getMonsterCards().get(i - 1)).collect(Collectors.toCollection(ArrayList::new));
    }


    public String monsterMode() {
        IO.getInstance().chooseMonsterMode();
        return IO.getInstance().getInputMessage();
    }


    public MonsterCard getMonsterCardFromHand() {
        IO.getInstance().chooseMonster();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return (MonsterCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            return getMonsterCardFromHand();
        }
    }


    public MonsterCard getFromMyGY() {
        IO.getInstance().chooseMonster();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return (MonsterCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getGraveyard().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            return getFromMyGY();
        }
    }


    public Card getCardFromMyGY() {
        IO.getInstance().chooseCard();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return DuelController.getInstance().getGame().getCurrentPlayer().getField().getGraveyard().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            return getCardFromMyGY();
        }
    }


    public Card getCardFromOpponentGY() {
        IO.getInstance().chooseCard();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getGraveyard().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            return getCardFromOpponentGY();
        }
    }


    public MonsterCard getFromOpponentGY() {
        IO.getInstance().chooseMonster();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return (MonsterCard) DuelController.getInstance().getGame().getTheOtherPlayer().getField().getGraveyard().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            return getFromOpponentGY();
        }
    }


    public boolean isMine() {
        IO.getInstance().isMine();
        return IO.getInstance().getInputMessage().toLowerCase().matches("y(?:es)?");
    }


    public SpellAndTrapCard getFieldSpellFromDeck() {
        IO.getInstance().printString(DuelController.getInstance().sortFieldCards());
        IO.getInstance().chooseFieldSpell();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return (SpellAndTrapCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getDeckZone().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            return getFieldSpellFromDeck();
        }
    }


    public MonsterCard getMonsterToEquip() {
        IO.getInstance().chooseMonster();
        String monsterCardNumber = IO.getInstance().getInputMessage();
        if (monsterCardNumber.equals("cancel")) return null;
        try {
            return DuelController.getInstance().getGame().getCurrentPlayer().getField().getMonsterCards().get(Integer.parseInt(monsterCardNumber) - 1);
        } catch (Exception e) {
            return getMonsterToEquip();
        }
    }


    public MonsterCard getHijackedCard() {
        IO.getInstance().chooseMonster();
        String monsterCardNumber = IO.getInstance().getInputMessage();
        if (monsterCardNumber.equals("cancel")) return null;
        try {
            return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getMonsterCards().get(Integer.parseInt(monsterCardNumber) - 1);
        } catch (Exception e) {
            return getHijackedCard();
        }
    }


    public boolean wantsToActivateTrap(String name, String username) {
        if (DuelController.getInstance().handleMirageDragon(username))
            return false;
        IO.getInstance().wantToActivate(name);
        return IO.getInstance().getInputMessage().toLowerCase().matches("y(?:es)?");
    }


    public boolean ordinaryOrSpecial() {
        IO.getInstance().summonMode();
        String ordinaryOrSpecial = IO.getInstance().getInputMessage().toLowerCase();
        return !ordinaryOrSpecial.matches("o(?:rd(?:inary)?)?");
    }


    public int numOfSpellCardsToDestroy() {
        IO.getInstance().numOfCards();
        String number = IO.getInstance().getInputMessage();
        return Integer.parseInt(number);
    }


    public Card getCardFromHand() {
        IO.getInstance().chooseCard();
        String cardNumber = IO.getInstance().getInputMessage();
        if (cardNumber.matches("cancel")) return null;
        try {
            return DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand().get(Integer.parseInt(cardNumber) - 1);
        } catch (Exception e) {
            return getCardFromHand();
        }
    }


    public Card getCardFromOpponentHand() {
        IO.getInstance().chooseCard();
        String cardNumber = IO.getInstance().getInputMessage();
        if (cardNumber.matches("cancel")) return null;
        try {
            return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getHand().get(Integer.parseInt(cardNumber) - 1);
        } catch (Exception e) {
            return getCardFromOpponentHand();
        }
    }


    public boolean summonGateGuardian() {
        IO.getInstance().gateGuardian();
        String input = IO.getInstance().getInputMessage().toLowerCase();
        return input.matches("y(?:es)?");
    }


    public int barbaros() {
        IO.getInstance().barbaros();
        String summonMode = IO.getInstance().getInputMessage();
        return Integer.parseInt(summonMode);
        // 1 summon with 2 tributes
        // 2 summon normally
        // 3 summon with 3 tributes
    }


    public boolean killMessengerOfPeace() {
        IO.getInstance().killMessengerOfPeace();
        String input = IO.getInstance().getInputMessage().toLowerCase();
        return input.matches("y(?:es)?");
    }


    public SpellAndTrapCard getFromMyField() {
        String spellNumber = IO.getInstance().getInputMessage();
        if (spellNumber.matches("cancel")) return null;
        try {
            return DuelController.getInstance().getGame().getCurrentPlayer().getField().getSpellAndTrapCards().get(Integer.parseInt(spellNumber) - 1);
        } catch (Exception e) {
            return getFromMyField();
        }
    }


    public SpellAndTrapCard getFromOpponentField() {
        String spellNumber = IO.getInstance().getInputMessage();
        if (spellNumber.matches("cancel")) return null;
        try {
            return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getSpellAndTrapCards().get(Integer.parseInt(spellNumber) - 1);
        } catch (Exception e) {
            return getFromOpponentField();
        }
    }


    public int whereToSummonFrom() {
        String originOfSummon = IO.getInstance().getInputMessage();
        return Integer.parseInt(originOfSummon);
        // 1 hand
        // 2 deck
        // 3 gy
    }


    public String getCardName() {
        return IO.getInstance().getInputMessage();
    }


    public boolean wantsToExchange() {
        return IO.getInstance().getInputMessage().toLowerCase().matches("y(?:es)?");
    }


    public String[] cardsToExchange() {
        IO.getInstance().cardsToExchange();
        //card from side deck
        // *
        //card from main deck
        return IO.getInstance().getInputMessage().split(" \\* ");
    }


    public MonsterCard getFromMyDeck(boolean isOpponent) {
        IO.getInstance().chooseMonster();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return (MonsterCard) DuelController.getInstance().getGame().getTheOtherPlayer().getField().getDeckZone().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            return getFromMyDeck(isOpponent);
        }
    }


    public MonsterCard getFromMyGY(boolean isOpponent) {
        IO.getInstance().chooseMonster();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return (MonsterCard) DuelController.getInstance().getGame().getTheOtherPlayer().getField().getGraveyard().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            return getFromMyGY(isOpponent);
        }
    }


    public MonsterCard getMonsterCardFromHand(boolean isOpponent) {
        IO.getInstance().chooseMonster();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return (MonsterCard) DuelController.getInstance().getGame().getTheOtherPlayer().getField().getHand().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            return getMonsterCardFromHand(isOpponent);
        }
    }

    public void muteGame(MouseEvent mouseEvent) {
        MainView.isGameMute = ((ToggleButton) mouseEvent.getTarget()).isSelected();
        if (MainView.isGameMute) MainView.gameMusic.pause();
        else if (MainView.gameMusic == null) MainView.playGameMusic();
        else MainView.gameMusic.play();
    }

    public void changeBackground() {
        if (counter == 17)
            counter = 0;
        GridPane content = (GridPane) LoginView.mainGameSceneOne.lookup("#gridpane");
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
}