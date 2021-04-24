package controller;

import model.Card;
import model.MonsterCardEffectTypes;
import model.MonsterCardTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ShopController {
    private static ShopController singleInstance = null;
    private static ArrayList<Card> allCards;
    static {
        allCards = new ArrayList<>();
    }
    public static ShopController getInstance() {
        if (singleInstance == null)
            singleInstance = new ShopController();
        return singleInstance;
    }

    private void showCard(String name) {

    }
    private void showAllCards() {

    }
    private boolean isCardNameValid(String name) {
        return true;
    }
    private void buyCard(String cardName) {

    }
    private void createCardForShop() {
        //TODO JSON
    }
    private void sort() {
        Collections.sort(allCards, new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
}
