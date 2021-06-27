package yugioh.controller;

import lombok.Getter;
import lombok.Setter;
import yugioh.model.cards.Card;
import yugioh.model.cards.specialcards.*;
import yugioh.view.IO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

@Getter
@Setter
public class ShopController {

    private static ShopController singleInstance = null;
    private static ArrayList<Card> allCards;

    static {
        allCards = new ArrayList<>();
        addSpecialCards();
        allCards.addAll(ImportAndExport.getInstance().readAllCards());
        sort();
    }

    private ShopController() { }

    public static ShopController getInstance() {
        if (singleInstance == null)
            singleInstance = new ShopController();
        return singleInstance;
    }

    public void showCard(String cardName) {
        var card = Card.getCardByName(cardName);
        if (Objects.isNull(card)) {
            IO.getInstance().noSuchCard();
            return;
        }
        IO.getInstance().printString(card.toString());
    }

    public void showAllCards() {
        var toPrint = new StringBuilder();
        for (Card card : allCards)
            toPrint.append(card.getName()).append(":").append(card.getPrice()).append("\n");
        toPrint.setLength(toPrint.length() - 1);
        IO.getInstance().printString(toPrint.toString());
    }

    public static ArrayList<Card> getAllCards() {
        return allCards;
    }

    public boolean isCardNameValid(String name) {
        return allCards.stream().map(Card::getName).anyMatch(n -> n.equals(name));
    }

    private boolean hasEnoughMoney(String cardName) {
        if (!MainController.getInstance().getLoggedIn().hasEnoughMoney(Card.getCardByName(cardName).getPrice())) {
            IO.getInstance().printDoesntHaveEnoughMoney();
            return false;
        }
        return true;
    }

    public void buyCard(String cardName) {
        if (isCardNameValid(cardName) && hasEnoughMoney(cardName)) {
            MainController.getInstance().getLoggedIn().addCard(cardName);
            MainController.getInstance().getLoggedIn().setMoney(MainController.getInstance().getLoggedIn().getMoney() - Card.getCardByName(cardName).getPrice());
        }
    }

    private static void addSpecialCards() {
        allCards.add(new ChangeOfHeart());
        allCards.add(new CommandKnight());
        allCards.add(new ManEaterBug());
        allCards.add(new MessengerOfPeace());
        allCards.add(new Suijin());
        allCards.add(new TheCalculator());
        allCards.add(new UnitedWeStand());
        allCards.add(new SwordsOfRevealingLight());
        allCards.add(new SwordOfDarkDestruction());
        allCards.add(new BlackPendant());
        allCards.add(new MagnumShield());
        allCards.add(new Scanner());
    }

    private static void sort() {
        allCards.sort(Comparator.comparing(Card::getName));
    }
}
