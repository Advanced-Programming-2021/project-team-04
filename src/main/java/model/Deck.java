package model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Deck {
    @Expose(serialize = true, deserialize = true)
    private ArrayList<Card> mainDeck = new ArrayList<>();
    @Expose(serialize = true, deserialize = true)
    private ArrayList<Card> sideDeck = new ArrayList<>();
    @Expose(serialize = true, deserialize = true)
    private String deckName;
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

    public boolean isDeckValid() {
        return mainDeck.size() >= 40 && mainDeck.size() <= 60 && sideDeck.size() <= 15;
    }

    public boolean isAddingCardValid(String cardName) {
        Card card = Card.getCardByName(cardName);
        if (card instanceof SpellAndTrapCard) {
            SpellAndTrapCard spellAndTrapCard = (SpellAndTrapCard) card;
            if (spellAndTrapCard.isLimited()) spellAndTrapCard.setAllowedNumber(1);
        }
        int count = 0;
        for (Card thisCard : getMainDeck())
            if (thisCard.getName().equals(cardName))
                count++;
        for (Card thisCard : getSideDeck())
            if (thisCard.getName().equals(cardName))
                count++;
        return count != 3 && Card.getCardByName(cardName).getAllowedNumber() != count;
    }

    public ArrayList<Card> getMainDeck() {
        return mainDeck;
    }

    public ArrayList<Card> getSideDeck() {
        return sideDeck;
    }

    public boolean isMainDeckFull() {
        isMainDeckFull = mainDeck.size() == 60;
        return isMainDeckFull;
    }

    public boolean isSideDeckFull() {
        isSideDeckFull = sideDeck.size() == 15;
        return isSideDeckFull;
    }

    @Override
    public String toString() {
        return "";
    }

}
