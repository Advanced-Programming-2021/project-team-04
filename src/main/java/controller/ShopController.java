package controller;

import model.*;

import java.io.FileNotFoundException;
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
    public ShopController() throws FileNotFoundException {
        createCardForShop();
    }
    public static ShopController getInstance() throws FileNotFoundException {
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
    public void createCardForShop() throws FileNotFoundException {
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
        MonsterCard alexandriteDragon = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Alexandrite Dragon.JSON");
        allCards.add(alexandriteDragon);
        MonsterCard axeRaider = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Axe Raider.JSON");
        allCards.add(axeRaider);
        MonsterCard babyDragon = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Baby dragon.JSON");
        allCards.add(babyDragon);
        MonsterCard battleOX = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Battle OX.JSON");
        allCards.add(battleOX);
        MonsterCard battleWarrior = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Battle warrior.JSON");
        allCards.add(battleWarrior);
        MonsterCard beastKingBarbaros = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Beast King Barbaros.JSON");
        allCards.add(beastKingBarbaros);

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
