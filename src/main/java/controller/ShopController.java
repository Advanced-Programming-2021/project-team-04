package controller;

import model.*;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ShopController {
    private static ShopController singleInstance = null;
    private static ArrayList<Card> allCards;
    static {
        allCards = new ArrayList<>();
    }
    public ShopController() {
        createCardForShop();
    }
    public static ShopController getInstance() {
        if (singleInstance == null)
            singleInstance = new ShopController();
        return singleInstance;
    }

    public void showCard(String name) {

    }
    public void showAllCards() {

    }
    public boolean isCardNameValid(String name) {
        return true;
    }
    public void buyCard(String cardName) {

    }
    public void createCardForShop() {
        ChangeOfHeart changeOfHeart = new ChangeOfHeart();
        allCards.add(changeOfHeart);
        CommandKnight commandKnight = new CommandKnight();
        allCards.add(commandKnight);
        ManEaterBug manEaterBug = new ManEaterBug();
        allCards.add(manEaterBug);
        MessengerOfPeace messengerOfPeace = new MessengerOfPeace();
        allCards.add(messengerOfPeace);
        Suijin suijin = new Suijin();
        allCards.add(suijin);
        Texchanger texchanger = new Texchanger();
        allCards.add(texchanger);
        TheCalculator calculator = new TheCalculator();
        allCards.add(calculator);
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
