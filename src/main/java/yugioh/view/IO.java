package yugioh.view;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;


import java.util.Scanner;

public class IO {

    private static IO singleInstance = null;

    private Scanner scanner = new Scanner(System.in);
    private final Popup popup = new Popup();
    private final Label label = new Label();

    private IO() {
        Button cancelButton = new Button();
        cancelButton.setText("cancel");
        addFunctionButton(cancelButton, popup);
        HBox hBox = new HBox();
        GridPane gridPane = new GridPane();
        configureErrors(popup, gridPane, label, hBox, cancelButton);
    }

    public static IO getInstance() {
        if (singleInstance == null)
            singleInstance = new IO();
        return singleInstance;
    }

    public String getInputMessage(){
        return scanner.nextLine().trim();
    }

    public void cardsToExchange() {
        System.out.println("enter the name of the cards you want to exchange:" +
                "\n(card from side deck * card from main deck");
    }


    public void invalidCard() {
        label.setText("selected card is not valid");
        popup.show(LoginView.stage);
    }

    public void cardNotVisible() {
        label.setText("card is not visible");
        popup.show(LoginView.stage);
    }

    public void printString(String toPrint) {
        System.out.println(toPrint);
    }

    public void invalidTributeSum() {
        label.setText("selected monsters levels don’t match with ritual monster");
        popup.show(LoginView.stage);
    }

    public void cannotRitualSummon() {
        label.setText("there is no way you could ritual summon a monster");
        popup.show(LoginView.stage);
    }

    public void cannotRitualSummonThisCard() {
        label.setText("you can't ritual summon this card");
        popup.show(LoginView.stage);
    }

    public void cannotSet() {
        label.setText("you can’t set this card");
        popup.show(LoginView.stage);
    }

    public void spellZoneFull() {
        label.setText("spell card zone is full");
        popup.show(LoginView.stage);
    }

    public void alreadyActive() {
        label.setText("you have already activated this card");
        popup.show(LoginView.stage);
    }

    public void onlySpells() {
        label.setText("activate effect is only for spell cards.");
        popup.show(LoginView.stage);
    }

    public void cannotDirectAttack() {
        label.setText("you can’t attack the opponent directly");
        popup.show(LoginView.stage);
    }

    public void noCardHere() {
        label.setText("there is no card to attack here");
        popup.show(LoginView.stage);
    }

    public void alreadyAttacked() {
        label.setText("this card already attacked");
        popup.show(LoginView.stage);
    }

    public void cannotFlipSummon() {
        label.setText("you can’t flip summon this card");
        popup.show(LoginView.stage);
    }

    public void cannotAttackThisCard() {
        label.setText("you can’t attack this card");
        popup.show(LoginView.stage);
    }

    public void cannotAttack() {
        label.setText("you can’t attack with this card");
        popup.show(LoginView.stage);
    }

    public void alreadyChangedPosition() {
        label.setText("you already changed this card position in this turn");
        popup.show(LoginView.stage);
    }

    public void cannotChangePosition() {
        label.setText("you can’t change this card's position");
        popup.show(LoginView.stage);
    }

    public void cantSet() {
        label.setText("you can’t set this card");
        popup.show(LoginView.stage);
    }

    public void notEnoughTribute() {
        label.setText("there are not enough cards for tribute");
        popup.show(LoginView.stage);
    }

    public void alreadySummonedOrSet() {
        label.setText("you already summoned/set on this turn");
        popup.show(LoginView.stage);
    }

    public void monsterZoneFull() {
        label.setText("monster card zone is full");
        popup.show(LoginView.stage);
    }

    public void wrongPhase() {
        label.setText("action not allowed in this phase");
        popup.show(LoginView.stage);
    }

    public void cantSummon() {
        label.setText("you can’t summon this card");
        popup.show(LoginView.stage);
    }

    public void cardNotSelected() {
        label.setText("no card is selected yet");
        popup.show(LoginView.stage);
    }

    public void enterANewPassword() {
        label.setText("please enter a new password");
        popup.show(LoginView.stage);
    }

    public void invalidCurrentPassword() {
        label.setText("current password is invalid");
        popup.show(LoginView.stage);
    }

    public void setText(String text) {
        label.setText(text);
        popup.show(LoginView.stage);
    }

    public void passwordChanged() {
        label.setText("password changed successfully!");
        popup.show(LoginView.stage);
    }

    public void nicknameChanged() {
        label.setText("nickname changed successfully!");
        popup.show(LoginView.stage);
    }

    public void cheatIncreaseScore() {
        System.out.println("""
                The space pirate crew of the Aurora
                Known as the Mechanisms
                Had been watching all this time, fascinated
                For when you're immortal
                A good war is a very pleasant distraction, indeed""");
    }

    public void cheatIncreaseMoney() {
        System.out.println("""
                Deep within the depth of the station
                You’d find the key that brings your salvation
                Ornate and hidden past pain and privation
                It’s clutched in the Captain’s cold hands""");
    }

    public void notAPlayer(String username) {
        label.setText(username + " is not a player in this game!");
        popup.show(LoginView.stage);
    }

    public void invalidPicture() {
        label.setText("there was a problem with your picture! (we only support gif/jpg/png)");
        popup.show(LoginView.stage);
    }

    public void invalidDeck(String username) {
        label.setText(username + "’s deck is invalid");
        popup.show(LoginView.stage);
    }

    public void noActiveDeck(String username) {
        label.setText(username + " has no active deck");
        popup.show(LoginView.stage);
    }

    public void playerDoesntExist() {
        label.setText("there is no player with this username");
        popup.show(LoginView.stage);
    }

    public void passwordDoesntMatch() {
        label.setText("Username and password didn’t match!");
        popup.show(LoginView.stage);
    }

    public void userWithNicknameExists(String nickname) {
        label.setText("user with nickname " + nickname + " already exists");
        popup.show(LoginView.stage);
    }

    public void userWithUsernameExists(String username) {
        label.setText("user with username " + username + " already exists");
        popup.show(LoginView.stage);
    }

    public void noCardSelected() {
        label.setText("no card is selected yet");
        popup.show(LoginView.stage);
    }

    public void printPhase(String phase) {
        ((Label) LoginView.mainGameSceneOne.lookup("#currentPhase")).setText(phase);
        ((Label) LoginView.mainGameSceneTwo.lookup("#currentPhase")).setText(phase);
    }

    public void tooManyCards(String deckName, String cardName) {
        label.setText("there are already too many cards with name " + cardName + " in deck " + deckName);
        popup.show(LoginView.stage);
    }

    public void sideDeckIsFull() {
        label.setText("side deck is full");
        popup.show(LoginView.stage);
    }

    public void mainDeckIsFull() {
        label.setText("main deck is full");
        popup.show(LoginView.stage);
    }

    public void deckExists(String deckName) {
        label.setText("deck with name " + deckName + " already exists");
        popup.show(LoginView.stage);
    }

    public void deckCreated() {
        label.setText("deck created successfully!");
        popup.show(LoginView.stage);
    }
    public void addFunctionButton(Button cancelButton, Popup popup) {
        cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                popup.hide();
            }
        });
    }

    private void configureErrors(Popup popup, GridPane gridPane, Label label, HBox hBox, Button button) {
        popup.getContent().add(gridPane);
        gridPane.add(hBox, 0, 0);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().add(label);
        hBox.getChildren().add(button);
        gridPane.setStyle("-fx-background-color: #6a006e; -fx-padding: 20px;");
        label.setStyle("-fx-text-fill: #ffffff; -fx-padding: 10px; -fx-font-size: 14px; -fx-font-weight: bold;");
        button.setStyle("-fx-background-color: #ff00f2; -fx-opacity: 50;");
    }
}
