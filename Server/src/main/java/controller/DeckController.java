package controller;

import model.Account;
import model.PlayerDeck;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DeckController {

    public static String createDeck(String[] command) {
        String deckName = command[4];
        Account loggedIn = MainController.getLoggedInAccounts().get(command[1]);
        String result = errorForCreation(deckName, loggedIn);
        if (result.equals("success")) {
            var thisPlayerDeck = new PlayerDeck(deckName);
            loggedIn.addDeck(thisPlayerDeck);
        }
        return result;
    }

    private static String errorForCreation(String deckName, Account loggedIn) {
        if (loggedIn.getDeckByName(deckName) != null) {
            return "deck with name " + deckName + " already exists";
        }
        return "success";
    }

    public static String deleteDeck(String[] command) {
        String deckName = command[4];
        Account thisPlayer = MainController.getLoggedInAccounts().get(command[1]);
        if (thisPlayer.getActiveDeck() != null && thisPlayer.getActiveDeck().getDeckName().equals(deckName))
            thisPlayer.setActivePlayerDeck(null);
        thisPlayer.deleteDeck(thisPlayer.getDeckByName(deckName));
        return "success";
    }

    public static String activateDeck(String[] command) {
        String deckName = command[4];
        Account thisPlayer = MainController.getLoggedInAccounts().get(command[1]);
        thisPlayer.setActivePlayerDeck(deckName);
        return "success";
    }

    public static String addCardToDeck(String[] command) {
        String deckName = command[4];
        String cardName = command[5];
        boolean isMainDeck = Boolean.parseBoolean(command[6]);
        Account thisPlayer = MainController.getLoggedInAccounts().get(command[1]);
        String result = errorForAddingCard(deckName, cardName, isMainDeck, thisPlayer);
        if (result.equals("success")) {
            if (isMainDeck) thisPlayer.getDeckByName(deckName).addCardToMainDeck(cardName);
            else thisPlayer.getDeckByName(deckName).addCardToSideDeck(cardName);
        }
        return result;
    }

    private static String errorForAddingCard(String deckName, String cardName, boolean isMainDeck, Account thisPlayer) {
        if (isMainDeck && thisPlayer.getDeckByName(deckName).isMainDeckFull())
            return "main deck is full";
        if (!isMainDeck && thisPlayer.getDeckByName(deckName).isSideDeckFull())
            return "side deck is full";
        if (!thisPlayer.getDeckByName(deckName).isAddingCardValid(cardName))
            return "there are already too many cards with name " + cardName + " in deck " + deckName;
        return "success";
    }

    public static String removeCardFromDeck(String[] command) {
        String deckName = command[4];
        String cardName = command[5];
        boolean isMainDeck = Boolean.parseBoolean(command[6]);
        Account thisPlayer =  MainController.getLoggedInAccounts().get(command[1]);
        if (isMainDeck) thisPlayer.getDeckByName(deckName).removeCardFromMainDeck(cardName);
        else thisPlayer.getDeckByName(deckName).removeCardFromSideDeck(cardName);
        return "success";
    }


    public static String getDeckCards(String[] command) {
        PlayerDeck deck =  MainController.getLoggedInAccounts().get(command[1]).getDeckByName(command[4]);
        LinkedHashMap<String, Short> mainDeckCards = deck.getMainDeckCards();
        LinkedHashMap<String, Short> sideDeckCards = deck.getSideDeckCards();
        var stringBuilder = new StringBuilder(deck.getDeckName()).append(":\n").append("Main Deck:\n");
        var counter = new AtomicInteger(0);
        mainDeckCards.keySet().forEach(name -> stringBuilder.append(name).append(":").append(mainDeckCards.get(name)).append(" - ").
                append(counter.incrementAndGet() % 5 == 0 ? "\n" : ""));
        stringBuilder.append("\nSide Deck:\n");
        counter.set(0);
        sideDeckCards.keySet().forEach(name -> stringBuilder.append(name).append(":").append(mainDeckCards.get(name)).append(" - ").
                append(counter.incrementAndGet() % 5 == 0 ? "\n" : ""));
        return stringBuilder.toString();
    }
}
