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

    public void summoned() {
        System.out.println("summoned successfully");
    }

    public void revealCard(String cardName) {
        System.out.print("opponent’s monster card was " + cardName + " and ");
    }

    public void lostInDefense(int damage) {
        System.out.println("no card is destroyed and you received " + damage + " battle damage");
    }

    public void drawInDefense() {
        System.out.println("no card is destroyed");
    }

    public void wonInDefense() {
        System.out.println("the defense position monster is destroyed");
    }

    public void lostInAttack(int damage) {
        System.out.println("Your monster card is destroyed and you received " + damage + " battle damage");
    }

    public void drawInAttack() {
        System.out.println("both you and your opponent monster cards are destroyed and no one receives damage");
    }

    public void wonAttackInAttack(int damage) {
        System.out.println("your opponent’s monster is destroyed and your opponent receives " + damage + " battle damage");
    }

    public void cardsToExchange() {
        System.out.println("enter the name of the cards you want to exchange:" +
                "\n(card from side deck * card from main deck");
    }

    public void wantsToExchange() {
        System.out.println("do you want to exchange a card with your side deck?");
    }

    public void cardName() {
        System.out.println("enter a card name:");
    }

    public void whereToSummonFrom() {
        System.out.println("where do you want to summon a card from?" +
                "\n1: hand" +
                "\n2: deck" +
                "\n3: graveyard");
    }

    public void chooseSpell() {
        System.out.println("enter the number of the spell you choose:");
    }

    public void killMessengerOfPeace() {
        System.out.println("do you want to kill messenger of peace instead of paying 100 LP?");
    }

    public void barbaros() {
        System.out.println("how do you want to summon this card?" +
                "\n1: with 2 tributes" +
                "\n2: with 0 tributes and ATK reduction" +
                "\n3: with 3 tributes and destruction of all opponent cards");
    }

    public void gateGuardian() {
        System.out.println("do you want to summon this card with three tributes?");
    }

    public void numOfCards() {
        System.out.println("how many cards do you want to destroy? (two cards at most)");
    }

    public void summonMode() {
        System.out.println("how do you want to summon this card (ordinary or special)?");
    }

    public void chooseFieldSpell() {
        System.out.println("enter the number of the field spell you choose:");
    }

    public void isMine() {
        System.out.println("do you want to choose from your own cards?");
    }

    public void chooseCard() {
        System.out.println("enter the number of the card you choose:");
    }

    public void chooseMonsterMode() {
        System.out.println("choose the monster mode (Attack or Defense):");
    }

    public void chooseTributes() {
        System.out.println("enter the numbers of the card you want to tribute divided by a space:");
    }

    public void chooseMonster() {
        System.out.println("enter the number of the monster you choose:");
    }

    public void chooseRitualCard() {
        System.out.println("enter the number of the ritual card you choose:");
    }

    public void chooseTribute() {
        System.out.println("enter the number of the card you want to tribute:");
    }

    public void chooseStarter() {
        System.out.println("choose who should begin the game:");
    }

    public void pickRPS() {
        System.out.println("choose rock or paper or scissors:");
    }

    public void printAIsRPS(String rps) {
        System.out.println("computer chose: " + rps );
    }

    public void wantToActivate(String cardName) {
        System.out.println("do you want to activate " + cardName + "?");
    }

    public void selectCardToAdd() {
        System.out.println("select a card to add to chain");
    }

    public void addToChain() {
        System.out.println("do you want to add card to chain?");
    }

    public void makeChain() {
        System.out.println("do you want to make a chain?");
    }

    public void invalidCard() {
        System.out.println("selected card is not valid");
    }

    public void cardNotVisible() {
        System.out.println("card is not visible");
    }

    public void printString(String toPrint) {
        System.out.println(toPrint);
    }

    public void invalidTributeSum() {
        System.out.println("selected monsters levels don’t match with ritual monster");
    }

    public void cannotRitualSummon() {
        System.out.println("there is no way you could ritual summon a monster");
    }

    public void cannotRitualSummonThisCard() {
        System.out.println("you can't ritual summon this card");
    }

    public void cannotSet() {
        System.out.println("you can’t set this card");
    }

    public void preparationsNotDone() {
        System.out.println("preparations of this spell are not done yet");
    }

    public void spellZoneFull() {
        System.out.println("spell card zone is full");
    }

    public void alreadyActive() {
        System.out.println("you have already activated this card");
    }

    public void onlySpells() {
        System.out.println("activate effect is only for spell cards.");
    }

    public void spellActivated() {
        System.out.println("spell activated");
    }

    public void receivedDamage(int damage) {
        System.out.println("you opponent receives " + damage + " battle damage");
    }

    public void cannotDirectAttack() {
        System.out.println("you can’t attack the opponent directly");
    }

    public void noCardHere() {
        System.out.println("there is no card to attack here");
    }

    public void alreadyAttacked() {
        System.out.println("this card already attacked");
    }

    public void cannotFlipSummon() {
        System.out.println("you can’t flip summon this card");
    }

    public void cannotAttackThisCard() {
        System.out.println("you can’t attack this card");
    }

    public void cannotAttack() {
        System.out.println("you can’t attack with this card");
    }

    public void positionChanged() {
        System.out.println("monster card position changed successfully");
    }

    public void flipSummoned() {
        System.out.println("flip summoned successfully");
    }

    public void alreadyChangedPosition() {
        System.out.println("you already changed this card position in this turn");
    }

    public void alreadyInPosition() {
        System.out.println("this card is already in the wanted position");
    }

    public void cannotChangePosition() {
        System.out.println("you can’t change this card's position");
    }

    public void setSuccessfully() {
        System.out.println("set successfully");
    }

    public void cantSet() {
        System.out.println("you can’t set this card");
    }

    public void noMonster() {
        System.out.println("there no monsters one this address");
    }

    public void notEnoughTribute() {
        System.out.println("there are not enough cards for tribute");
    }

    public void alreadySummonedOrSet() {
        System.out.println("you already summoned/set on this turn");
    }

    public void monsterZoneFull() {
        System.out.println("monster card zone is full");
    }

    public void wrongPhase() {
        System.out.println("action not allowed in this phase");
    }

    public void cantSummon() {
        System.out.println("you can’t summon this card");
    }

    public void cardNotSelected() {
        System.out.println("no card is selected yet");
    }

    public void enterANewPassword() {
        label.setText("please enter a new password");
        popup.show(LoginView.stage);
    }

    public void invalidCurrentPassword() {
        label.setText("current password is invalid");
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

    public void invalidDeck(String username) {
        label.setText(username + "’s deck is invalid");
        popup.show(LoginView.stage);
    }

    public void noActiveDeck(String username) {
        label.setText(username + " has no active deck");
        popup.show(LoginView.stage);
    }

    public void invalidNumOfRounds() {
        System.out.println("number of rounds is not supported");

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
        System.out.println("no card is selected yet");
    }

    public void cardDeselected() {
        System.out.println("card deselected");
    }

    public void noCardInPosition() {
        System.out.println("no card found in the given position");
    }

    public void invalidSelection() {
        System.out.println("invalid selection");
    }

    public void cardSelected() {
        System.out.println("card selected");
    }

    public void printPhase(String phase) {
        System.out.println("phase: " + phase);
    }

    public void cardDoesntExistInSideDeck(String cardName) {
        System.out.println("card with name " + cardName + " does not exist in side deck");
    }

    public void cardDoesntExistInMainDeck(String cardName) {
        System.out.println("card with name " + cardName + " does not exist in main deck");
    }

    public void tooManyCards(String deckName, String cardName) {
        System.out.println("there are already too many cards with name " + cardName + " in deck " + deckName);
    }

    public void sideDeckIsFull() {
        System.out.println("side deck is full");
    }

    public void mainDeckIsFull() {
        System.out.println("main deck is full");
    }

    public void cardDoesntExist(String cardName) {
        System.out.println("card with name " + cardName + " does not exist");
    }

    public void deckDoesntExist(String deckName) {
        System.out.println("deck with name " + deckName + " does not exist");
    }

    public void deckExists(String deckName) {
        System.out.println("deck with name " + deckName + " already exists");
    }

    public void cardRemoved() {
        System.out.println("card removed form deck successfully");
    }

    public void cardAddedToDeck() {
        System.out.println("card added to deck successfully");
    }

    public void deckCreated() {
        System.out.println("deck created successfully!");
    }

    public void deckDeleted() {
        System.out.println("deck deleted successfully");
    }

    public void deckActivated() {
        System.out.println("deck activated successfully");
    }

    public void printImportExportMenuName() {
        System.out.println("Import and Export Menu");
    }
    public void printLoginMenuName() {
        System.out.println("Login Menu");
    }

    public void printMainMenuName() {
        System.out.println("Main Menu");
    }

    public void printDuelMenuName() {
        System.out.println("Duel Menu");
    }

    public void printDeckMenuName() {
        System.out.println("Deck Menu");
    }

    public void printScoreboardMenuName() {
        System.out.println("Scoreboard Menu");
    }

    public void printProfileMenuName() {
        System.out.println("Profile Menu");
    }

    public void printShopMenuName() {
        System.out.println("Shop Menu");
    }

    public void printMenuNavigationImpossible() {
        System.out.println("menu navigation is not possible");
    }

    public void printLoginFirst() {
        System.out.println("please login first");
    }

    public void printInvalidCommand() {
        System.out.println("invalid command");
    }

    public void printUserLoggedOut() {
        System.out.println("user logged out successfully!");
    }

    public void printInvalidCardName() {
        System.out.println("there is no card with this name");
    }

    public void printDoesntHaveEnoughMoney() {
        System.out.println("not enough money");
    }

    private void addFunctionButton(Button cancelButton, Popup popup) {
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
