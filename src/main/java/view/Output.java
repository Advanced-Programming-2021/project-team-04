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
        System.out.println("you can’t change this card position");
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

    public void summoned() {
        System.out.println("summoned successfully");
    }

    public void enterANewPassword() {
        System.out.println("please enter a new password");
    }

    public void invalidCurrentPassword() {
        System.out.println("current password is invalid");
    }

    public void passwordChanged() {
        System.out.println("password changed successfully!");
    }

    public void nicknameChanged() {
        System.out.println("nickname changed successfully!");
    }

    public void cheatIncreaseScore() {
        System.out.println("The space pirate crew of the Aurora\n" +
                "Known as the Mechanisms\n" +
                "Had been watching all this time, fascinated\n" +
                "For when you're immortal\n" +
                "A good war is a very pleasant distraction, indeed");
    }

    public void cheatIncreaseMoney() {
        System.out.println("Deep within the depth of the station\n" +
                "You’d find the key that brings your salvation\n" +
                "Ornate and hidden past pain and privation\n" +
                "It’s clutched in the Captain’s cold hands");
    }

    public void invalidDeck(String username) {
        System.out.println(username + "’s deck is invalid");
    }

    public void noActiveDeck(String username) {
        System.out.println(username + " has no active deck");
    }

    public void invalidNumOfRounds() {
        System.out.println("number of rounds is not supported");
    }

    public void playerDoesntExist() {
        System.out.println("there is no player with this username");
    }

    public void passwordDoesntMatch() {
        System.out.println("Username and password didn’t match!");
    }

    public void userWithNicknameExists(String nickname) {
        System.out.println("user with nickname " + nickname + " already exists");
    }

    public void userWithUsernameExists(String username) {
        System.out.println("user with username " + username + " already exists");
    }

    public void userCreated() {
        System.out.println("user created successfully!");
    }
    public void loggedIn() {
        System.out.println("user logged in successfully!");
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
        //TODO create a method for every phase so this command doesn't get an input String
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
