package controller;

import model.*;
import view.IO;

import java.util.ArrayList;
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
            var thisGameDeck = new GameDeck(deckName);
            MainController.getInstance().getLoggedIn().addDeck(thisGameDeck);
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
            var card = thisPlayer.getCardByName(cardName);
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
        var toPrint = new StringBuilder("Decks:\nActive deck:\n");
        if (thisPlayer.getActiveDeck() != null) {
            var activeGameDeck = thisPlayer.getActiveDeck();
            toPrint.append(activeGameDeck.getDeckName()).append(": main deck ").append(activeGameDeck.getMainDeck().size()).append(", side deck ").append(activeGameDeck.getSideDeck().size()).append(", ").append(activeGameDeck.isDeckValid() ? "valid" : "invalid").append("\n");
        }
        //TODO isn't active deck an object of all decks? is this method tested?
        toPrint.append("Other decks: \n");
        if (!thisPlayer.getAllDecks().isEmpty()) {
            sortedDecks();
            //TODO test this two commented lines, they could do better
//            StringBuilder finalToPrint = toPrint;
//            thisPlayer.getAllDecks().forEach(d -> finalToPrint.append(d.getDeckName()).append(": main deck ").append(d.getMainDeck().size()).append(", side deck ").append(d.getSideDeck().size()).append(", ").append(d.isDeckValid() ? "valid" : "invalid").append("\n"));
            for (GameDeck gameDeck : thisPlayer.getAllDecks()) {
                toPrint.append(gameDeck.getDeckName()).append(": main deck ").append(gameDeck.getMainDeck().size()).append(", side deck ").append(gameDeck.getSideDeck().size()).append(", ").append(gameDeck.isDeckValid() ? "valid" : "invalid").append("\n");
            }
        }
        toPrint.setLength(toPrint.length() - 1);
        IO.getInstance().printString(toPrint.toString());
    }

    private void sortedDecks() {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        thisPlayer.getAllDecks().sort(Comparator.comparing(GameDeck::getDeckName));
    }

    public void printDeck(String deckName, boolean isMain) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (errorForDeletingOrActivating(deckName)) {
            ArrayList<Card> monsterCards = new ArrayList<>();
            ArrayList<Card> spellAndTrap = new ArrayList<>();
            var toPrint = new StringBuilder("Deck: " + deckName + "\n");
            sortedCards(deckName);
            if (isMain) {
                toPrint.append("Main deck:\n");
                thisPlayer.getDeckByName(deckName).getMainDeck().forEach(c -> {
                    if (c instanceof MonsterCard) monsterCards.add(c);
                    else spellAndTrap.add(c);
                });
                toPrint.append("Monsters:\n");
                //TODO won't this do better?
//                StringBuilder finalToPrint = toPrint;
//                monsterCards.forEach(c -> finalToPrint.append(c.getName()).append(": ").append(c.getDescription()).append("\n"));
//                toPrint.append("Spell and Traps:\n");
//                spellAndTrap.forEach(c -> finalToPrint.append(c.getName()).append(": ").append(c.getDescription()).append("\n"));
                for (Card card : monsterCards)
                    toPrint.append(card.getName()).append(": ").append(card.getDescription()).append("\n");
                toPrint.append("Spell and Traps:\n");
                for (Card card : spellAndTrap)
                    toPrint.append(card.getName()).append(": ").append(card.getDescription()).append("\n");
            }
            toPrint.setLength(toPrint.length() - 1);
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
        var toPrint = new StringBuilder();
        sortAllCards();
        //TODO won't this do better?
//        StringBuilder finalToPrint = toPrint;
//        thisPlayer.getAllCards().forEach(c -> finalToPrint.append(c.getName()).append(":").append(c.getDescription()).append("\n"));
        for (Card card : thisPlayer.getAllCards())
            toPrint.append(card.getName()).append(":").append(card.getDescription()).append("\n");
        toPrint.setLength(toPrint.length() - 1);
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
