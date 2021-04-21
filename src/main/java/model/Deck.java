package model;

import java.util.ArrayList;

public class Deck {
    private ArrayList<Card> mainDeck = new ArrayList<>();
    private ArrayList<Card> sideDeck = new ArrayList<>();
    private String deckName;
    private boolean isMainDeckValid;
    private boolean isMainDeckFull, isSideDeckFull;

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    private boolean hasCard(String cardName) {
        return true;
    }
    private boolean isAddingCardValid(Card card) {
        return false;
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
        return isMainDeckValid;
    }

    public void setMainDeckValid(boolean mainDeckValid) {
        isMainDeckValid = mainDeckValid;
    }

    public boolean isMainDeckFull() {
        return isMainDeckFull;
    }

    public void setMainDeckFull(boolean mainDeckFull) {
        isMainDeckFull = mainDeckFull;
    }

    public boolean isSideDeckFull() {
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
