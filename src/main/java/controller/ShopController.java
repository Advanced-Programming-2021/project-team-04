package controller;

import model.Card;

import java.util.TreeSet;

public class ShopController {
    private static ShopController singleInstance = null;
    private ShopController() {

    }
    public static ShopController getInstance() {
        if (singleInstance == null)
            singleInstance = new ShopController();
        return singleInstance;
    }
    public void run() {

    }
    static {

    }
    private static TreeSet<Card> allCards;
    private void showCard(String name) {

    }
    private void showAllCards() {

    }
    private boolean isCardNameValid(String name) {
        return true;
    }
    private void buyCard(String cardName) {

    }
    private Card createCard(String cardName) {
    }
}
