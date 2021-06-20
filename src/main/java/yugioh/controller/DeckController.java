package yugioh.controller;

import yugioh.model.Account;
import yugioh.model.cards.Card;
import yugioh.model.cards.MonsterCard;
import yugioh.model.PlayerDeck;
import yugioh.view.IO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DeckController {
    private static DeckController singleInstance = null;

    public static DeckController getInstance() {
        if (singleInstance == null)
            singleInstance = new DeckController();
        return singleInstance;
    }

    public void createDeck(String deckName) {
        if (errorForCreation(deckName)) {
            var thisPlayerDeck = new PlayerDeck(deckName);
            MainController.getInstance().getLoggedIn().addDeck(thisPlayerDeck);
            IO.getInstance().deckCreated();
        }
    }

    public void deleteDeck(String deckName) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (errorForDeletingOrActivating(deckName)) {
            IO.getInstance().deckDeleted();
            if (thisPlayer.getActiveDeck() != null && thisPlayer.getActiveDeck().getDeckName().equals(deckName)) thisPlayer.setActivePlayerDeck(null);
            thisPlayer.deleteDeck(thisPlayer.getDeckByName(deckName));
        }
    }

    public void activateDeck(String deckName) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (errorForDeletingOrActivating(deckName)) {
            thisPlayer.setActivePlayerDeck(deckName);
            IO.getInstance().deckActivated();
        }
    }

    public void addCardToDeck(String deckName, String cardName, boolean isMainDeck) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (errorForAddingCard(deckName, cardName, isMainDeck)) {
            if (isMainDeck) thisPlayer.getDeckByName(deckName).addCardToMainDeck(cardName);
            else thisPlayer.getDeckByName(deckName).addCardToSideDeck(cardName);
            IO.getInstance().cardAddedToDeck();
        }
    }

    public void removeCardFromDeck(String deckName, String cardName, boolean isMainDeck) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (errorsForRemoving(deckName, cardName, isMainDeck)) {
            if (isMainDeck) thisPlayer.getDeckByName(deckName).removeCardFromMainDeck(cardName);
            else thisPlayer.getDeckByName(deckName).removeCardFromSideDeck(cardName);
            IO.getInstance().cardRemoved();
        }
    }

    public void printAllDecks() {
        //TODO move this method to IO
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        var toPrint = new StringBuilder("Decks:\nActive deck:\n");
        if (thisPlayer.getActiveDeck() != null) {
            var activePlayerDeck = thisPlayer.getActiveDeck();
            toPrint.append(activePlayerDeck.getDeckName()).append(": main deck ").append(activePlayerDeck.getMainDeckSize())
                    .append(", side deck ").append(activePlayerDeck.getSideDeckSize()).append(", ")
                    .append(activePlayerDeck.isDeckValid() ? "valid" : "invalid").append("\n");
        }
        //TODO isn't active deck an object of all decks? is this method tested?
        toPrint.append("Other decks: \n");
        if (!thisPlayer.getAllPlayerDecks().isEmpty()) {
            sortDecks();
            thisPlayer.getAllPlayerDecks().stream().filter(d -> !d.equals(thisPlayer.getActiveDeck())).forEach(d -> toPrint.append(d.getDeckName()).append(": main deck ").append(d.getMainDeckSize())
                    .append(", side deck ").append(d.getSideDeckSize()).append(", ")
                    .append(d.isDeckValid() ? "valid" : "invalid").append("\n"));
        }
        toPrint.setLength(toPrint.length() - 1);
        IO.getInstance().printString(toPrint.toString());
    }

    private void sortDecks() {
        MainController.getInstance().getLoggedIn().getAllPlayerDecks().sort(Comparator.comparing(PlayerDeck::getDeckName));
    }

    public void printDeck(String deckName, boolean isMain) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        if (errorForDeletingOrActivating(deckName)) {
            ArrayList<Card> monsterCards = new ArrayList<>();
            ArrayList<Card> spellAndTrap = new ArrayList<>();
            var toPrint = new StringBuilder("Deck: " + deckName + "\n");
            sortCards(deckName);
            if (isMain) {
                toPrint.append("Main deck:\n");
                thisPlayer.getDeckByName(deckName).getMainDeckCards().keySet().forEach(c -> {
                    Card card = null;
                    try {
                        card = ImportAndExport.getInstance().readCard(c);
                    } catch (Exception ignored) { }
                    if (card instanceof MonsterCard) monsterCards.add(card);
                    else spellAndTrap.add(card);
                });
                toPrint.append("Monsters:\n");
                monsterCards.forEach(c -> toPrint.append(c.getName()).append(": ").append(c.getDescription()).append("\n"));
                toPrint.append("Spell and Traps:\n");
                spellAndTrap.forEach(c -> toPrint.append(c.getName()).append(": ").append(c.getDescription()).append("\n"));
            }
            toPrint.setLength(toPrint.length() - 1);
            IO.getInstance().printString(toPrint.toString());
        }
    }

    public void sortCards(String deckName) {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        thisPlayer.getDeckByName(deckName).setMainDeckCards(thisPlayer.getDeckByName(deckName).getMainDeckCards().entrySet()
                .stream().sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (c1, c2) -> c1, LinkedHashMap::new)));
        thisPlayer.getDeckByName(deckName).setSideDeckCards(thisPlayer.getDeckByName(deckName).getSideDeckCards().entrySet()
                .stream().sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (c1, c2) -> c1, LinkedHashMap::new)));
    }

    public void printAllCards() {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        var toPrint = new StringBuilder();
        sortAllCards();
        thisPlayer.getAllCardsHashMap().keySet().forEach(n -> {
            for (var i = 0; i < thisPlayer.getAllCardsHashMap().get(n); i++)
                toPrint.append(n).append(": ").append(Card.getDescriptionByName(n)).append("\n");
        });
        IO.getInstance().printString(toPrint.toString());
    }

    private void sortAllCards() {
        Account thisPlayer = MainController.getInstance().getLoggedIn();
        thisPlayer.setAllCardsHashMap(thisPlayer.getAllCardsHashMap().entrySet().stream().sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (c1, c2) -> c1, LinkedHashMap::new)));
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
        } else if (isMainDeck && !thisPlayer.getDeckByName(deckName).mainDeckContainsCard(cardName)) {
            IO.getInstance().cardDoesntExistInMainDeck(cardName);
            return false;
        } else if (!isMainDeck && !thisPlayer.getDeckByName(deckName).sideDeckContainsCard(cardName)) {
            IO.getInstance().cardDoesntExistInSideDeck(cardName);
            return false;
        }
        return true;
    }
}
