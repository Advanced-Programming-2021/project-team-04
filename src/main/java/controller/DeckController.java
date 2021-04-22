package controller;
import model.Account;
import model.Card;
import model.Deck;
import view.Output;

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
        if (errorForCreation(deckName)) {
            Deck thisDeck = new Deck(deckName);
            MainController.getInstance().getLoggedIn().getAllDecks().add(thisDeck);
            Output.getForNow();
        }
    }
    private boolean errorForCreation(String deckName) {
        if (MainController.getInstance().getLoggedIn().getDeckByName(deckName) != null) { //TODO just for this user?
            Output.getForNow();
            return false;
        }
        return true;
    }
    private void deleteDeck(String deckName) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (errorForDeletingOrActivating(deckName, thisPlayer)) {
            Output.getForNow();
            thisPlayer.getAllDecks().remove(thisPlayer.getDeckByName(deckName));
        }
    }

    private void activateDeck(String deckName) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (errorForDeletingOrActivating(deckName, thisPlayer)) {
            thisPlayer.setActiveDeck(thisPlayer.getDeckByName(deckName));
            Output.getForNow();
        }
    }
    private boolean errorForDeletingOrActivating(String deckName, Account thisPlayer) {
        if (thisPlayer.getDeckByName(deckName) == null) {
            Output.getForNow();
            return false;
        }
        return true;
    }
    private void addCardToDeck(String deckName, String cardName, boolean isMainDeck) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (errorForAddingCard(deckName, cardName, isMainDeck, thisPlayer)) {
            Card card = thisPlayer.getCardByName(cardName);
            if (isMainDeck) {
                thisPlayer.getDeckByName(deckName).getMainDeck().add(card);
                Output.getForNow();
            }
            else {
                thisPlayer.getDeckByName(deckName).getSideDeck().add(card);
                Output.getForNow();
            }
        }
    }
    private boolean errorForAddingCard(String deckName, String cardName, boolean isMainDeck, Account thisPlayer) {
        if (!thisPlayer.getUnusedCards().contains(thisPlayer.getCardByName(cardName))) {
            Output.getForNow();
            return false;
        }
        else if (!thisPlayer.getAllDecks().contains(thisPlayer.getDeckByName(deckName))) {
            Output.getForNow();
            return false;
        }
        else if (isMainDeck && thisPlayer.getDeckByName(deckName).isMainDeckFull()) {
            Output.getForNow();
            return false;
        }
        else if (!isMainDeck && thisPlayer.getDeckByName(deckName).isSideDeckFull()) {
            Output.getForNow();
            return false;
        }
        else if (!thisPlayer.getDeckByName(deckName).isAddingCardValid(cardName)) {
            Output.getForNow();
            return false;
        }
        return true;
    }
    private void removeCardFromDeck(String deckName, String cardName, boolean isMainDeck) {

    }
    private boolean errorsForRemoving(String deckName, String cardName, boolean isMainDeck, Account thisPlayer) {
        if (!thisPlayer.getAllDecks().contains(thisPlayer.getDeckByName(deckName))) {
            Output.getForNow();
            return false;
        }
        else if (!thisPlayer.getDeckByName(deckName).)
    }
    private void printAllDecks() {

    }
    private void printDeck(String deckName) {

    }

}
