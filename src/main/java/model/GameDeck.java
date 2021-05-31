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

    public void setMainDeck(ArrayList<Card> mainDeck) {
        this.mainDeck = mainDeck;
    }

    public void setSideDeck(ArrayList<Card> sideDeck) {
        this.sideDeck = sideDeck;
    }

    public boolean sideDeckHasCard(String cardName) {
        //TODO still doubt
        return sideDeck.contains(Card.getCardByName(cardName));
    }

    public ArrayList<Card> getMainDeck() {
        return mainDeck;
    }

    public ArrayList<Card> getSideDeck() {
        return sideDeck;
    }

    @Override
    public String toString() {
        return "";
    }

}
