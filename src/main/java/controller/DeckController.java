package controller;

import model.Account;
import model.Card;
import model.Deck;
import view.Output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DeckController {
    private static DeckController singleInstance = null;
    private Account thisPlayer = MainController.getInstance().getLoggedIn();

    public static DeckController getInstance() {
        if (singleInstance == null)
            singleInstance = new DeckController();
        return singleInstance;
    }

    public void createDeck(String deckName) {
        if (errorForCreation(deckName)) {
            Deck thisDeck = new Deck(deckName);
            MainController.getInstance().getLoggedIn().addDeck(thisDeck);
            Output.getForNow();
        }
    }

    public void deleteDeck(String deckName) {
        if (errorForDeletingOrActivating(deckName)) {
            Output.getForNow();
            if (thisPlayer.getDeckByName(deckName).equals(thisPlayer.getActiveDeck())) thisPlayer.setActiveDeck(null);
            thisPlayer.deleteDeck(thisPlayer.getDeckByName(deckName));
        }
    }

    public void activateDeck(String deckName) {
        if (errorForDeletingOrActivating(deckName)) {
            thisPlayer.setActiveDeck(thisPlayer.getDeckByName(deckName));
            Output.getForNow();
        }
    }

    public void addCardToDeck(String deckName, String cardName, boolean isMainDeck) {
        if (errorForAddingCard(deckName, cardName, isMainDeck)) {
            Card card = thisPlayer.getCardByName(cardName);
            if (isMainDeck) {
                thisPlayer.getDeckByName(deckName).getMainDeck().add(card);
                Output.getForNow();
            } else {
                thisPlayer.getDeckByName(deckName).getSideDeck().add(card);
                Output.getForNow();
            }
        }
    }

    public void removeCardFromDeck(String deckName, String cardName, boolean isMainDeck) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (errorsForRemoving(deckName, cardName, isMainDeck)) {
            if (isMainDeck) thisPlayer.getDeckByName(deckName).getMainDeck().remove(Card.getCardByName(cardName));
            else thisPlayer.getDeckByName(deckName).getSideDeck().remove(Card.getCardByName(cardName));
            Output.getForNow();
        }
    }

    public void printAllDecks() {
        String toPrint = "Decks:\nActive deck:\n";
        String isValid = "invalid";
        if (thisPlayer.getActiveDeck() != null) {
            Deck activeDeck = thisPlayer.getActiveDeck();
            if (activeDeck.isMainDeckValid()) isValid = "valid";
            toPrint += activeDeck.getDeckName() + ": main deck " + activeDeck.getMainDeck().size() + ", side deck " +
                    activeDeck.getSideDeck().size() + ", " + isValid + "\n";
        }
        toPrint += "Other decks: \n";
        if (!thisPlayer.getAllDecks().isEmpty()) {
            sortedDecks();
            for (Deck deck : thisPlayer.getAllDecks()) {
                if (deck.isMainDeckValid()) isValid = "valid";
                toPrint += deck.getDeckName() + ": main deck " + deck.getMainDeck().size() + ", side deck " +
                        deck.getSideDeck().size() + ", " + isValid + "\n";
            }
        }
        toPrint = toPrint.substring(0, toPrint.length() - 2);
        Output.getForNow();
    }

    private void sortedDecks() {
        thisPlayer.getAllDecks().sort(new Comparator<Deck>() {
            @Override
            public int compare(Deck o1, Deck o2) {
                return o1.getDeckName().compareTo(o2.getDeckName());
            }
        });
    }

    public void printDeck(String deckName, boolean isMain) {
        if (errorForDeletingOrActivating(deckName)) {
            ArrayList<Card> monsterCards = new ArrayList<>();
            ArrayList<Card> spellAndTrap = new ArrayList<>();
            String toPrint = "Deck: " + deckName + "\n";
            sortedCards(deckName);
            if (isMain) {
                toPrint += "Main deck:\n";
                for (Card card : thisPlayer.getDeckByName(deckName).getMainDeck())
                    if (card.isMonster) monsterCards.add(card);
                    else spellAndTrap.add(card);
                toPrint += "Monsters:\n";
                for (Card card : monsterCards)
                    toPrint += card.getName() + ": " + card.getDescription() + "\n";
                toPrint += "Spell and Traps:\n";
                for (Card card : spellAndTrap)
                    toPrint += card.getName() + ": " + card.getDescription() + "\n";
            }
            toPrint = toPrint.substring(0, toPrint.length() - 2);
            Output.getForNow();
        }
    }

    public void sortedCards(String deckName) {
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
        String toPrint = "";
        sortAllCards();
        for (Card card : thisPlayer.getAllCards())
            toPrint += card.getName() + ":" + card.getDescription() + "\n";
        toPrint = toPrint.substring(0, toPrint.length() - 2);
        Output.getForNow();
    }

    private void sortAllCards() {
        Collections.sort(thisPlayer.getAllCards(), new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
    private boolean errorForCreation(String deckName) {
        if (MainController.getInstance().getLoggedIn().getDeckByName(deckName) != null) { //TODO just for this user?
            Output.getForNow();
            return false;
        }
        return true;
    }

    private boolean errorForDeletingOrActivating(String deckName) {
        if (thisPlayer.getDeckByName(deckName) == null) {
            Output.getForNow();
            return false;
        }
        return true;
    }

    private boolean errorForAddingCard(String deckName, String cardName, boolean isMainDeck) {
        if (!thisPlayer.hasCard(cardName)) {
            Output.getForNow();
            return false;
        } else if (!thisPlayer.hasDeck(deckName)) {
            Output.getForNow();
            return false;
        } else if (isMainDeck && thisPlayer.getDeckByName(deckName).isMainDeckFull()) {
            Output.getForNow();
            return false;
        } else if (!isMainDeck && thisPlayer.getDeckByName(deckName).isSideDeckFull()) {
            Output.getForNow();
            return false;
        } else if (!thisPlayer.getDeckByName(deckName).isAddingCardValid(cardName)) {
            Output.getForNow();
            return false;
        }
        return true;
    }

    private boolean errorsForRemoving(String deckName, String cardName, boolean isMainDeck) {
        if (!thisPlayer.getAllDecks().contains(thisPlayer.getDeckByName(deckName))) {
            Output.getForNow();
            return false;
        } else if (isMainDeck && !thisPlayer.getDeckByName(deckName).mainDeckHasCard(cardName)) {
            Output.getForNow();
            return false;
        } else if (!isMainDeck && !thisPlayer.getDeckByName(deckName).sideDeckHasCard(cardName)) {
            Output.getForNow();
            return false;
        }
        return true;
    }
}
