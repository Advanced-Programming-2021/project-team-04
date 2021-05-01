package view;

public class DeckView extends Menu {
    private static DeckView singleInstance = null;
    private DeckView() {

    }
    public static DeckView getInstance() {
        if (singleInstance == null)
            singleInstance = new DeckView();
        return singleInstance;
    }

    @Override
    public void run() {

    }

    @Override
    public void showCurrentMenu() {
        Output.getInstance().printDeckMenuName();
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
}
