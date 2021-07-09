package yugioh.controller;

import yugioh.model.Account;
import yugioh.model.PlayerDeck;
import yugioh.view.IO;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DeckController {

    private static DeckController singleInstance = null;

    public static DeckController getInstance() {
        if (singleInstance == null)
            singleInstance = new DeckController();
        return singleInstance;
    }

    public void createDeck(String deckName) {
        var thisPlayerDeck = new PlayerDeck(deckName);
        MainController.getInstance().getLoggedIn().addDeck(thisPlayerDeck);
        IO.getInstance().deckCreated();
    }

    public void deleteDeck(String deckName) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (thisPlayer.getActiveDeck() != null && thisPlayer.getActiveDeck().getDeckName().equals(deckName))
            thisPlayer.setActivePlayerDeck(null);
        thisPlayer.deleteDeck(thisPlayer.getDeckByName(deckName));
    }

    public void activateDeck(String deckName) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        thisPlayer.setActivePlayerDeck(deckName);
    }

    public void addCardToDeck(String deckName, String cardName, boolean isMainDeck) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (isMainDeck) thisPlayer.getDeckByName(deckName).addCardToMainDeck(cardName);
        else thisPlayer.getDeckByName(deckName).addCardToSideDeck(cardName);
    }

    public void removeCardFromDeck(String deckName, String cardName, boolean isMainDeck) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (isMainDeck) thisPlayer.getDeckByName(deckName).removeCardFromMainDeck(cardName);
        else thisPlayer.getDeckByName(deckName).removeCardFromSideDeck(cardName);
    }


    public String getDeckCards(PlayerDeck deck) {
        LinkedHashMap<String, Short> mainDeckCards = deck.getMainDeckCards();
        LinkedHashMap<String, Short> sideDeckCards = deck.getSideDeckCards();
        var stringBuilder = new StringBuilder(deck.getDeckName()).append(":\n").append("Main Deck:\n");
        var counter = new AtomicInteger(0);
        mainDeckCards.keySet().forEach(name -> stringBuilder.append(name).append(":").append(mainDeckCards.get(name)).append(" - ").append(counter.incrementAndGet() % 5 == 0 ? "\n" : ""));
        stringBuilder.append("\nSide Deck:\n");
        counter.set(0);
        sideDeckCards.keySet().forEach(name -> stringBuilder.append(name).append(":").append(mainDeckCards.get(name)).append(" - ").append(counter.incrementAndGet() % 5 == 0 ? "\n" : ""));
        return stringBuilder.toString();
    }
}