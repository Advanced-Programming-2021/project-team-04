package model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import model.cards.Card;
import model.cards.SpellAndTrapCard;

import java.util.LinkedHashMap;
import java.util.Objects;


@Getter
@Setter
public class PlayerDeck {


    @JsonProperty
    private LinkedHashMap<String, Short> mainDeckCards;
    @JsonProperty
    private LinkedHashMap<String, Short> sideDeckCards;
    @JsonProperty
    private String deckName;


    public PlayerDeck(String deckName) {
        mainDeckCards = new LinkedHashMap<>();
        sideDeckCards = new LinkedHashMap<>();
        this.deckName = deckName;
    }

    public PlayerDeck() { }


    public boolean mainDeckContainsCard(String cardName) {
        return mainDeckCards.containsKey(cardName);
    }


    public boolean sideDeckContainsCard(String cardName) {
        return sideDeckCards.containsKey(cardName);
    }


    public int getMainDeckSize() {
        return mainDeckCards.keySet().stream().mapToInt(c -> mainDeckCards.get(c)).sum();
    }


    public int getSideDeckSize() {
        return sideDeckCards.keySet().stream().mapToInt(c -> sideDeckCards.get(c)).sum();
    }


    public boolean isDeckInvalid() {
        var mainDeckSize = getMainDeckSize();
        return mainDeckSize < 40 || mainDeckSize > 60 || getSideDeckSize() > 15;
    }


    public boolean isMainDeckFull() {
        return getMainDeckSize() == 60;
    }


    public boolean isSideDeckFull() {
        return getSideDeckSize() == 15;
    }


    public boolean isAddingCardValid(String cardName) {
        var card = Card.getCardByName(cardName);
        if (card instanceof SpellAndTrapCard) {
            var spellAndTrapCard = (SpellAndTrapCard) card;
            if (spellAndTrapCard.isLimited()) spellAndTrapCard.setAllowedNumber(1);
        }
        var count = (mainDeckCards.containsKey(cardName) ? mainDeckCards.get(cardName) : 0) +
                (sideDeckCards.containsKey(cardName) ? sideDeckCards.get(cardName) : 0);
        return count <= 3 && Objects.requireNonNull(Card.getCardByName(cardName)).getAllowedNumber() <= count;
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