package controller;

import lombok.Getter;
import lombok.Setter;
import model.*;
import view.IO;

import java.util.ArrayList;
import java.util.Comparator;

@Getter
@Setter
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

    public boolean showCard(String cardName) {
        Card card = Card.getCardByName(cardName);
        if (card == null) return false;
        IO.getInstance().printString(card.toString());
        return true;
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
        for (Card card : allCards)
            if (card.getName().equals(name)) {
                return true;
            }
        IO.getInstance().printInvalidCardName();
        return false;
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
            switch (cardName) {
                case "Change of Heart" -> MainController.getInstance().getLoggedIn().addCard(new ChangeOfHeart());
                case "Command Knight" -> MainController.getInstance().getLoggedIn().addCard(new CommandKnight());
                case "Man-Eater Bug" -> MainController.getInstance().getLoggedIn().addCard(new ManEaterBug());
                case "Messenger of peace" -> MainController.getInstance().getLoggedIn().addCard(new MessengerOfPeace());
                case "Scanner" -> MainController.getInstance().getLoggedIn().addCard(new Scanner());
                case "Suijin" -> MainController.getInstance().getLoggedIn().addCard(new Suijin());
                case "The Calculator" -> MainController.getInstance().getLoggedIn().addCard(new TheCalculator());
                case "Swords of Revealing Light" -> MainController.getInstance().getLoggedIn().addCard(new SwordsOfRevealingLight());
                case "United We Stand" -> MainController.getInstance().getLoggedIn().addCard(new UnitedWeStand());
                case "Sword of dark destruction" -> MainController.getInstance().getLoggedIn().addCard(new SwordOfDarkDestruction());
                case "Magnum Shield" -> MainController.getInstance().getLoggedIn().addCard(new MagnumShield());
                case "Black Pendant" -> MainController.getInstance().getLoggedIn().addCard(new BlackPendant());
                default -> {
                    var monsterCard = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/" + cardName + ".JSON");
                    if (monsterCard != null)
                        MainController.getInstance().getLoggedIn().addCard(monsterCard);
                    else {
                        var spellAndTrapCard = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/" + cardName + ".JSON");
                        MainController.getInstance().getLoggedIn().addCard(spellAndTrapCard);
                    }
                }
            }
            MainController.getInstance().getLoggedIn().setMoney(MainController.getInstance().getLoggedIn().getMoney() - Card.getCardByName(cardName).getPrice());
        }
    }

    public void createCardForShop() {
        //TODO a static method would do better, or perhaps a static block
        allCards = new ArrayList<>();
        addSpecialCards();
        allCards.addAll(ImportAndExport.getInstance().readAllCards());
        sort();
    }

    private void addSpecialCards() {
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

    private void sort() {
        allCards.sort(Comparator.comparing(Card::getName));
    }
}
