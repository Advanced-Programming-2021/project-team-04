package model;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerDeck {

    private HashMap<String, Integer> mainDeckCards, sideDeckCards;

    public HashMap<String, Integer> getMainDeckCards() {
        return mainDeckCards;
    }

    public void setMainDeckCards(HashMap<String, Integer> mainDeckCards) {
        this.mainDeckCards = mainDeckCards;
    }

    public HashMap<String, Integer> getSideDeckCards() {
        return sideDeckCards;
    }

    public void setSideDeckCards(HashMap<String, Integer> sideDeckCards) {
        this.sideDeckCards = sideDeckCards;
    }

    public PlayerDeck(GameDeck gameDeck) {
        mainDeckCards = new HashMap<>();
        sideDeckCards = new HashMap<>();
//        for (Card card : gameDeck.getMainDeck()) {
//            if (mainDeckCards.containsKey(card.getName()))
//                mainDeckCards.replace(card.getName(), mainDeckCards.get(card.getName()) + 1);
//            else mainDeckCards.put(card.getName(), 1);
//        }
//        for (Card card : gameDeck.getSideDeck()) {
//            if (sideDeckCards.containsKey(card.getName()))
//                sideDeckCards.replace(card.getName(), sideDeckCards.get(card.getName()) + 1);
//            else sideDeckCards.put(card.getName(), 1);
//        } //TODO is this part even necessary? WTF was I thinking?
    }

}
