package controller;

import model.Card;

import java.util.TreeSet;

public class ShopController {
    private static ShopController singleInstance = null;
    private ShopController() {
        getInstance();
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
    private void createCard(String cardName) {
        //TODO is this the 240 khati switch case?
    }
}
