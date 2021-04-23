package model;

import java.util.ArrayList;

public class Deck {
    private ArrayList<Card> mainDeck = new ArrayList<>();
    private ArrayList<Card> sideDeck = new ArrayList<>();
    private String deckName;
    private boolean isMainDeckValid;
    private boolean isMainDeckFull, isSideDeckFull;

    public Deck(String deckName) {
        setDeckName(deckName);
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public boolean mainDeckHasCard(String cardName) { //TODO still doubt
        return mainDeck.contains(Card.getCardByName(cardName));
    }

    public boolean sideDeckHasCard(String cardName) { //TODO still doubt
        return sideDeck.contains(Card.getCardByName(cardName));
    }

    public boolean isAddingCardValid(String cardName) {
        int count = 0;
        for (Card thisCard : getMainDeck())
            if (thisCard.getName().equals(cardName))
                count++;
        for (Card thisCard : getSideDeck())
            if (thisCard.getName().equals(cardName))
                count++;
        if (count == 3) return false;
        return true;
    }

    public ArrayList<Card> getMainDeck() {
        return mainDeck;
    }

    public void setMainDeck(ArrayList<Card> mainDeck) {
        this.mainDeck = mainDeck;
    }

    public ArrayList<Card> getSideDeck() {
        return sideDeck;
    }

    public void setSideDeck(ArrayList<Card> sideDeck) {
        this.sideDeck = sideDeck;
    }

    public boolean isMainDeckValid() {
        isMainDeckValid = mainDeck.size() >= 40;
        return isMainDeckValid;
    }

    public void setMainDeckValid(boolean mainDeckValid) {
        isMainDeckValid = mainDeckValid;
    }

    public boolean isMainDeckFull() {
        isMainDeckFull = mainDeck.size() == 60;
        return isMainDeckFull;
    }

    public void setMainDeckFull(boolean mainDeckFull) {
        isMainDeckFull = mainDeckFull;
    }

    public boolean isSideDeckFull() {
        isSideDeckFull = sideDeck.size() == 15;
        return isSideDeckFull;
    }

    public void setSideDeckFull(boolean sideDeckFull) {
        isSideDeckFull = sideDeckFull;
    }

    private void addCardToDeck(Card card, boolean isMainDeck) {
        if (isMainDeck) mainDeck.add(card);
        else sideDeck.add(card);
    }

    private void removeCardFromDeck(Card card, boolean isMainDeck) {
        if (isMainDeck) // in the controller, check the errors
            mainDeck.remove(card);
        else sideDeck.remove(card);
    }

    @Override
    public String toString() {
        return "";
    }

}
