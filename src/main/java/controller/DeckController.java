package controller;

public class DeckController {
    private static DeckController singleInstance = null;
    private DeckController() {
        getInstance();
    }
    public void run() {

    }
    public static DeckController getInstance() {
        if (singleInstance == null)
            singleInstance = new DeckController();
        return singleInstance;
    }
    private void createDeck(String deckName) {

    }
    private void deleteDeck(String deckName) {

    }
    private void activateDeck(String deckName) {

    }
    private void addCardToDeck(String deckName, String cardName, boolean isMainDeck) {

    }
    private void removeCardFromDeck(String deckName, String cardName, boolean isMainDeck) {

    }
    private void printAllDecks() {

    }
    private void printDeck(String deckName) {

    }

}
