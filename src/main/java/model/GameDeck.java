package model;

import com.google.gson.annotations.Expose;
import controller.ImportAndExport;

import java.util.ArrayList;

public class GameDeck {
    @Expose(serialize = true, deserialize = true)
    private ArrayList<Card> mainDeck = new ArrayList<>();
    @Expose(serialize = true, deserialize = true)
    private ArrayList<Card> sideDeck = new ArrayList<>();
    @Expose(serialize = true, deserialize = true)
    private String deckName;

    public GameDeck(String deckName) {
        setDeckName(deckName);
    }

    public GameDeck(PlayerDeck playerDeck) {
        deckName = playerDeck.getDeckName();
        playerDeck.getMainDeckCards().keySet().forEach(n -> {
            for (var i = 0; i < playerDeck.getMainDeckCards().get(n); i++) {
                mainDeck.add(ImportAndExport.getInstance().readCard(n));
            }
        });
        playerDeck.getSideDeckCards().keySet().forEach(n -> {
            for (var i = 0; i < playerDeck.getSideDeckCards().get(n); i++) {
                mainDeck.add(ImportAndExport.getInstance().readCard(n));
            }
        });
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public boolean mainDeckHasCard(String cardName) {
        //TODO still doubt
        return mainDeck.contains(Card.getCardByName(cardName));
    }

    public boolean sideDeckHasCard(String cardName) {
        //TODO still doubt
        return sideDeck.contains(Card.getCardByName(cardName));
    }

    public boolean isDeckValid() {
        return mainDeck.size() >= 40 && mainDeck.size() <= 60 && sideDeck.size() <= 15;
    }

    public boolean isAddingCardValid(String cardName) {
        var card = Card.getCardByName(cardName);
        if (card instanceof SpellAndTrapCard) {
            var spellAndTrapCard = (SpellAndTrapCard) card;
            if (spellAndTrapCard.isLimited()) spellAndTrapCard.setAllowedNumber(1);
        }
        var count = getMainDeck().stream().filter(m -> m.getName().equals(cardName)).count() +
                getSideDeck().stream().filter(m -> m.getName().equals(cardName)).count();
        return count != 3 && Card.getCardByName(cardName).getAllowedNumber() != count;
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

    public boolean isMainDeckFull() {
        return mainDeck.size() == 60;
    }

    public boolean isSideDeckFull() {
        return sideDeck.size() == 15;
    }

    @Override
    public String toString() {
        return "";
    }

}
