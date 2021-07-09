package yugioh.controller;


import lombok.Getter;
import lombok.Setter;
import yugioh.model.cards.Card;

import java.util.ArrayList;
import java.util.Comparator;


@Getter
@Setter
public class ShopController {


    private static ShopController singleInstance = null;
    private static ArrayList<Card> allCards;


    static {
        allCards = new ArrayList<>(ImportAndExport.getInstance().readAllCards());
        sort();
    }


    private ShopController() { }


    public static ShopController getInstance() {
        if (singleInstance == null)
            singleInstance = new ShopController();
        return singleInstance;
    }


    public static ArrayList<Card> getAllCards() {
        return allCards;
    }


    public boolean isCardNameValid(String name) {
        return allCards.stream().map(Card::getName).anyMatch(n -> n.equals(name));
    }


    private boolean hasEnoughMoney(String cardName) {
        return MainController.getInstance().getLoggedIn().hasEnoughMoney(Card.getCardByName(cardName).getPrice());
    }


    public void buyCard(String cardName) {
        if (isCardNameValid(cardName) && hasEnoughMoney(cardName)) {
            MainController.getInstance().getLoggedIn().addCard(cardName);
            MainController.getInstance().getLoggedIn().setMoney(MainController.getInstance().getLoggedIn().getMoney() - Card.getCardByName(cardName).getPrice());
        }
    }


    private static void sort() {
        allCards.sort(Comparator.comparing(Card::getName));
    }
}