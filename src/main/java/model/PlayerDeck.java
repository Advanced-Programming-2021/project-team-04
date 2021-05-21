package model;

import java.util.HashMap;

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

}
