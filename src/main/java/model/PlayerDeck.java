package model;

import java.util.HashMap;
import java.util.Objects;

public class PlayerDeck {

    private HashMap<String, Short> mainDeckCards;
    private HashMap<String, Short> sideDeckCards;
    private String deckName;

    public PlayerDeck(String deckName) {
        mainDeckCards = new HashMap<>();
        sideDeckCards = new HashMap<>();
        this.deckName = deckName;
    }

    public HashMap<String, Short> getMainDeckCards() {
        return mainDeckCards;
    }

    public void setMainDeckCards(HashMap<String, Short> mainDeckCards) {
        this.mainDeckCards = mainDeckCards;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public HashMap<String, Short> getSideDeckCards() {
        return sideDeckCards;
    }

    public void setSideDeckCards(HashMap<String, Short> sideDeckCards) {
        this.sideDeckCards = sideDeckCards;
    }

    public boolean mainDeckContainsCard(String cardName) {
        return mainDeckCards.containsKey(cardName);
    }

    public boolean sideDeckContainsCard(String cardName) {
        return sideDeckCards.containsKey(cardName);
    }

    public boolean isDeckValid() {
        return mainDeckCards.size() >= 40 && mainDeckCards.size() <= 60 && sideDeckCards.size() <= 15;
    }

    public boolean isMainDeckFull() {
        return mainDeckCards.size() == 60;
    }

    public boolean isSideDeckFull() {
        return sideDeckCards.size() == 15;
    }

    public boolean isAddingCardValid(String cardName) {
        var card = Card.getCardByName(cardName);
        if (card instanceof SpellAndTrapCard) {
            var spellAndTrapCard = (SpellAndTrapCard) card;
            if (spellAndTrapCard.isLimited()) spellAndTrapCard.setAllowedNumber(1);
        }
        var count = (mainDeckCards.containsKey(cardName) ? mainDeckCards.get(cardName) : 0) +
                (sideDeckCards.containsKey(cardName) ? sideDeckCards.get(cardName) : 0);
        return count != 3 && Objects.requireNonNull(Card.getCardByName(cardName)).getAllowedNumber() != count;
    }

    public void addCardToMainDeck(String cardName) {
        if (mainDeckCards.containsKey(cardName)) mainDeckCards.replace(cardName, (short) (mainDeckCards.get(cardName) + 1));
        else mainDeckCards.put(cardName, (short) 1);
    }

    public void addCardToSideDeck(String cardName) {
        if (sideDeckCards.containsKey(cardName)) sideDeckCards.replace(cardName, (short) (sideDeckCards.get(cardName) + 1));
        else sideDeckCards.put(cardName, (short) 1);
    }

    public void removeCardFromMainDeck(String cardName) {
        if (mainDeckCards.get(cardName) > 1) mainDeckCards.replace(cardName, (short) (mainDeckCards.get(cardName) - 1));
        else mainDeckCards.remove(cardName);
    }

    public void removeCardFromSideDeck(String cardName) {
        if (sideDeckCards.get(cardName) > 1) sideDeckCards.replace(cardName, (short) (sideDeckCards.get(cardName) - 1));
        else sideDeckCards.remove(cardName);
    }
}
