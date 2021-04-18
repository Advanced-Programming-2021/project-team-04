package view;

public class DeckView {
    private static DeckView singleInstance = null;
    private DeckView() {

    }
    public static DeckView getInstance() {
        if (singleInstance == null)
            singleInstance = new DeckView();
        return singleInstance;
    }
    public void run() {

    }
    private void createDeck(String input) {

    }
    private void deleteDeck(String input) {

    }
    private void activateDeck(String input) {

    }
    private void addCardToDeck(String input) {

    }
    private void removeCardFromDeck(String input) {

    }
    private void printAllDecks() {

    }
    private void printDeck(String input) {

    }
    private void showCurrent() {

    }
}
