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

    private DeckController() {

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
        String toPrint = "Decks:\nActive deck:\n";
        String isValid = "invalid";
        if (thisPlayer.getActiveDeck() != null) {
            Deck activeDeck = thisPlayer.getActiveDeck();
            if (activeDeck.isDeckValid()) isValid = "valid";
            toPrint += activeDeck.getDeckName() + ": main deck " + activeDeck.getMainDeck().size() + ", side deck " +
                    activeDeck.getSideDeck().size() + ", " + isValid + "\n";
        }
        toPrint += "Other decks: \n";
        if (!thisPlayer.getAllDecks().isEmpty()) {
            sortedDecks();
            for (Deck deck : thisPlayer.getAllDecks()) {
                if (deck.isDeckValid()) isValid = "valid";
                toPrint += deck.getDeckName() + ": main deck " + deck.getMainDeck().size() + ", side deck " +
                        deck.getSideDeck().size() + ", " + isValid + "\n";
            }
        }
        toPrint = toPrint.substring(0, toPrint.length() - 1);
        IO.getInstance().printString(toPrint);
    }

    private void sortedDecks() {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        thisPlayer.getAllDecks().sort(new Comparator<Deck>() {
            @Override
            public int compare(Deck o1, Deck o2) {
                return o1.getDeckName().compareTo(o2.getDeckName());
            }
        });
    }

    public void printDeck(String deckName, boolean isMain) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (errorForDeletingOrActivating(deckName)) {
            ArrayList<Card> monsterCards = new ArrayList<>();
            ArrayList<Card> spellAndTrap = new ArrayList<>();
            String toPrint = "Deck: " + deckName + "\n";
            sortedCards(deckName);
            if (isMain) {
                toPrint += "Main deck:\n";
                for (Card card : thisPlayer.getDeckByName(deckName).getMainDeck())
                    if (card instanceof MonsterCard) monsterCards.add(card);
                    else spellAndTrap.add(card);
                toPrint += "Monsters:\n";
                for (Card card : monsterCards)
                    toPrint += card.getName() + ": " + card.getDescription() + "\n";
                toPrint += "Spell and Traps:\n";
                for (Card card : spellAndTrap)
                    toPrint += card.getName() + ": " + card.getDescription() + "\n";
            }
            toPrint = toPrint.substring(0, toPrint.length() - 2);
            IO.getInstance().printString(toPrint);
        }
    }

    public void sortedCards(String deckName) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        thisPlayer.getDeckByName(deckName).getMainDeck().sort(new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        thisPlayer.getDeckByName(deckName).getSideDeck().sort(new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    public void printAllCards() {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        String toPrint = "";
        sortAllCards();
        for (Card card : thisPlayer.getAllCards())
            toPrint += card.getName() + ":" + card.getDescription() + "\n";
        toPrint = toPrint.substring(0, toPrint.length() - 2);
        IO.getInstance().printString(toPrint);
    }

    private void sortAllCards() {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        Collections.sort(thisPlayer.getAllCards(), new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
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
