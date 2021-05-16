package controller;

import model.Account;
import model.Card;
import model.Deck;
import model.MonsterCard;
import view.IO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DeckController {
    private static DeckController singleInstance = null;

    public static DeckController getInstance() {
        if (singleInstance == null)
            singleInstance = new DeckController();
        return singleInstance;
    }

    public void createDeck(String deckName) {
        if (errorForCreation(deckName)) {
            Deck thisDeck = new Deck(deckName);
            MainController.getInstance().getLoggedIn().addDeck(thisDeck);
            IO.getInstance().deckCreated();
        }
    }

    public void deleteDeck(String deckName) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (errorForDeletingOrActivating(deckName)) {
            IO.getInstance().deckDeleted();
            if (thisPlayer.getDeckByName(deckName).equals(thisPlayer.getActiveDeck())) thisPlayer.setActiveDeck(null);
            thisPlayer.deleteDeck(thisPlayer.getDeckByName(deckName));
        }
    }

    public void activateDeck(String deckName) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (errorForDeletingOrActivating(deckName)) {
            thisPlayer.setActiveDeck(thisPlayer.getDeckByName(deckName));
            IO.getInstance().deckActivated();
        }
    }

    public void addCardToDeck(String deckName, String cardName, boolean isMainDeck) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (errorForAddingCard(deckName, cardName, isMainDeck)) {
            Card card = thisPlayer.getCardByName(cardName);
            if (isMainDeck)
                thisPlayer.getDeckByName(deckName).getMainDeck().add(card);
            else
                thisPlayer.getDeckByName(deckName).getSideDeck().add(card);
            IO.getInstance().cardAddedToDeck();
        }
    }

    public void removeCardFromDeck(String deckName, String cardName, boolean isMainDeck) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (errorsForRemoving(deckName, cardName, isMainDeck)) {
            if (isMainDeck) thisPlayer.getDeckByName(deckName).getMainDeck().remove(Card.getCardByName(cardName));
            else thisPlayer.getDeckByName(deckName).getSideDeck().remove(Card.getCardByName(cardName));
            IO.getInstance().cardRemoved();
        }
    }

    public void printAllDecks() {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        StringBuilder toPrint = new StringBuilder("Decks:\nActive deck:\n");
        String isValid = "invalid";
        if (thisPlayer.getActiveDeck() != null) {
            Deck activeDeck = thisPlayer.getActiveDeck();
            if (activeDeck.isDeckValid()) isValid = "valid";
            toPrint.append(activeDeck.getDeckName()).append(": main deck ").append(activeDeck.getMainDeck().size()).append(", side deck ").append(activeDeck.getSideDeck().size()).append(", ").append(isValid).append("\n");
        }
        toPrint.append("Other decks: \n");
        if (!thisPlayer.getAllDecks().isEmpty()) {
            sortedDecks();
            for (Deck deck : thisPlayer.getAllDecks()) {
                if (deck.isDeckValid()) isValid = "valid";
                toPrint.append(deck.getDeckName()).append(": main deck ").append(deck.getMainDeck().size()).append(", side deck ").append(deck.getSideDeck().size()).append(", ").append(isValid).append("\n");
            }
        }
        toPrint = new StringBuilder(toPrint.substring(0, toPrint.length() - 1));
        IO.getInstance().printString(toPrint.toString());
    }

    private void sortedDecks() {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        thisPlayer.getAllDecks().sort(Comparator.comparing(Deck::getDeckName));
    }

    public void printDeck(String deckName, boolean isMain) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (errorForDeletingOrActivating(deckName)) {
            ArrayList<Card> monsterCards = new ArrayList<>();
            ArrayList<Card> spellAndTrap = new ArrayList<>();
            StringBuilder toPrint = new StringBuilder("Deck: " + deckName + "\n");
            sortedCards(deckName);
            if (isMain) {
                toPrint.append("Main deck:\n");
                for (Card card : thisPlayer.getDeckByName(deckName).getMainDeck())
                    if (card instanceof MonsterCard) monsterCards.add(card);
                    else spellAndTrap.add(card);
                toPrint.append("Monsters:\n");
                for (Card card : monsterCards)
                    toPrint.append(card.getName()).append(": ").append(card.getDescription()).append("\n");
                toPrint.append("Spell and Traps:\n");
                for (Card card : spellAndTrap)
                    toPrint.append(card.getName()).append(": ").append(card.getDescription()).append("\n");
            }
            toPrint = new StringBuilder(toPrint.substring(0, toPrint.length() - 2));
            IO.getInstance().printString(toPrint.toString());
        }
    }

    public void sortedCards(String deckName) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        thisPlayer.getDeckByName(deckName).getMainDeck().sort(Comparator.comparing(Card::getName));
        thisPlayer.getDeckByName(deckName).getSideDeck().sort(Comparator.comparing(Card::getName));
    }

    public void printAllCards() {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        StringBuilder toPrint = new StringBuilder();
        sortAllCards();
        for (Card card : thisPlayer.getAllCards())
            toPrint.append(card.getName()).append(":").append(card.getDescription()).append("\n");
        toPrint = new StringBuilder(toPrint.substring(0, toPrint.length() - 2));
        IO.getInstance().printString(toPrint.toString());
    }

    private void sortAllCards() {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        thisPlayer.getAllCards().sort(Comparator.comparing(Card::getName));
    }

    private boolean errorForCreation(String deckName) {
        if (MainController.getInstance().getLoggedIn().getDeckByName(deckName) != null) {
            IO.getInstance().deckExists(deckName);
            return false;
        }
        return true;
    }

    private boolean errorForDeletingOrActivating(String deckName) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (!thisPlayer.hasDeck(deckName)) {
            IO.getInstance().deckDoesntExist(deckName);
            return false;
        }
        return true;
    }

    private boolean errorForAddingCard(String deckName, String cardName, boolean isMainDeck) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (!thisPlayer.hasCard(cardName)) {
            IO.getInstance().cardDoesntExist(cardName);
            return false;
        } else if (!thisPlayer.hasDeck(deckName)) {
            IO.getInstance().deckDoesntExist(deckName);
            return false;
        } else if (isMainDeck && thisPlayer.getDeckByName(deckName).isMainDeckFull()) {
            IO.getInstance().mainDeckIsFull();
            return false;
        } else if (!isMainDeck && thisPlayer.getDeckByName(deckName).isSideDeckFull()) {
            IO.getInstance().sideDeckIsFull();
            return false;
        } else if (!thisPlayer.getDeckByName(deckName).isAddingCardValid(cardName)) {
            IO.getInstance().tooManyCards(deckName, cardName);
            return false;
        }
        return true;
    }

    private boolean errorsForRemoving(String deckName, String cardName, boolean isMainDeck) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (thisPlayer.getDeckByName(deckName) == null) {
            IO.getInstance().deckDoesntExist(deckName);
            return false;
        } else if (isMainDeck && !thisPlayer.getDeckByName(deckName).mainDeckHasCard(cardName)) {
            IO.getInstance().cardDoesntExistInMainDeck(cardName);
            return false;
        } else if (!isMainDeck && !thisPlayer.getDeckByName(deckName).sideDeckHasCard(cardName)) {
            IO.getInstance().cardDoesntExistInSideDeck(cardName);
            return false;
        }
        return true;
    }
}
