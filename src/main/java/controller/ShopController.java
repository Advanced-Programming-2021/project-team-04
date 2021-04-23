package controller;

import model.Card;
import model.MonsterCardEffectTypes;
import model.MonsterCardTypes;
import model.OrdinaryCards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

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
        String cardDescription = "A monster with tremendous power, it destroys enemies with a swing of its axe.";
        OrdinaryCards battleOx = new OrdinaryCards("Battle OX", 4, 1700, 1000,
                MonsterCardEffectTypes.NORMAL, MonsterCardTypes.BEAST_WARRIOR, cardDescription, 2900);
        allCards.add(battleOx);
        cardDescription = "An axe-wielding monster of tremendous strength and agility.";
        OrdinaryCards axeRaider = new OrdinaryCards("Axe Raider", 4, 1700, 1150,
                MonsterCardEffectTypes.NORMAL, MonsterCardTypes.WARRIOR, cardDescription, 3100);
        allCards.add(axeRaider);
        //TODO should it be like this or what


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
