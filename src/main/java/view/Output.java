package view;

public class Output {
    private static Output singleInstance = null;
    private Output() {

    }
    public static Output getInstance() {
        if (singleInstance == null)
            singleInstance = new Output();
        return singleInstance;
    }
    private static String forNow = "";

    public static String getForNow() {
        return forNow;
    }

    public void printString(String toPrint) {
        System.out.println(toPrint);
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
        System.out.println("there are already three cards with name " + cardName + " in deck " + deckName);
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

}
