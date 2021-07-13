package yugioh.view;

import yugioh.controller.ImportAndExport;
import yugioh.controller.MainController;
import yugioh.controller.ShopController;
import yugioh.model.cards.Card;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;

import java.util.HashMap;

public class CreatorView {


    private static final HashMap<String, String> ALL_MONSTER_EFFECTS;
    private static final HashMap<String, String> ALL_SPELLANDTRAP_EFFECTS;

    static {
        ALL_MONSTER_EFFECTS = ImportAndExport.getInstance().readAllMonsterEffects();
        ALL_SPELLANDTRAP_EFFECTS = ImportAndExport.getInstance().readAllSpellAndTrapEffects();
    }

    private CreatorView() {
    }

    public static void start() {
        System.out.println("Round and round and round it goes," +
                " and when it deigns to stop, who you might be you cannot know.");
        System.out.println("to create a card, type magic. to exit, type exit.");
        while (!IO.getInstance().getInputMessage().equals("exit")) {
            process();
        }
    }

    private static void process() {
        System.out.println("""
                Write down a name for your card!\s
                Now don’t get agitated, I’m sure we’ll get there.\s
                Names are… tricky.""");
        var cardName = IO.getInstance().getInputMessage();
        System.out.println("Do you prefer the irresistible monsters or want to play with some magic?\n" +
                "choose monster or trap or spell");
        String cardType = IO.getInstance().getInputMessage();
        if (cardType.equals("monster")) createMonster(cardName);
        else createSpell(cardType.equals("spell"), cardName);
    }

    private static void createMonster(String cardName) {
        System.out.println("Cruelty free, type down the attack power!");
        var attackPower = Integer.parseInt(IO.getInstance().getInputMessage());
        System.out.println("I’ve been wondering about your batteries, give us your defence power");
        var defencePower = Integer.parseInt(IO.getInstance().getInputMessage());
        System.out.println("You shall be a king of a ruined world. Choose your monster type! e.g Effect");
        var monsterType = IO.getInstance().getInputMessage();
        System.out.println("Now choose the card type! e.g Machine");
        var cardType = IO.getInstance().getInputMessage();
        System.out.println("Lastly, enter your card's level.");
        var level = Integer.parseInt(IO.getInstance().getInputMessage());
        System.out.println("you'll see a list down here. choose the name of the effect you want to add to your card.\n" +
                "if you want to add more than one effect, simply write the names of the effects with a space between");
        printMonsterEffects();
        var name = IO.getInstance().getInputMessage();
        System.out.println("""
                Restiamo un po' di tempo ancora, tanto non c'è fretta
                Ché c'ho una frase scritta in testa, ma non l'ho mai detta
                give me the description Marlena""");
        var description = IO.getInstance().getInputMessage();
        var monsterCard = createNewMonster(attackPower, defencePower, monsterType, cardType, level, name, cardName, description);
        ShopController.getAllCards().add(monsterCard);
        ImportAndExport.getInstance().writeObjectToJson(ImportAndExport.RESOURCES_CARDS + monsterCard.getName(), monsterCard);
        System.out.println("Great! You’ll get used to the world we created. Or you'll try I guess...");
    }

    private static MonsterCard createNewMonster(int attackPower, int defencePower, String monsterType, String cardType, int level, String name, String cardName, String description) {
        var monsterCard = (MonsterCard) Card.getCardByName(name);
        var price = 0;
        if (monsterCard.getClassAttackPower() < attackPower) price += 400;
        if (monsterCard.getClassDefensePower() < defencePower) price += 400;
        if (monsterCard.getLevel() < level) price += 400;
        var newCard = new MonsterCard();
        newCard.setName(cardName + " " + name);
        newCard.setLevel(level);
        newCard.setClassAttackPower(attackPower);
        newCard.setClassDefensePower(defencePower);
        newCard.setThisCardAttackPower(attackPower);
        newCard.setThisCardDefensePower(defencePower);
        newCard.setCardType(cardType);
        newCard.setMonsterType(monsterType);
        newCard.setDescription(description);
        int money = (monsterCard.getPrice() + price) * 11 / 10;
        newCard.setPrice(money);
        newCard.setOriginal(false);
        MainController.getInstance().getLoggedIn().setMoney(MainController.getInstance().getLoggedIn().getMoney() - money);
        return newCard;
    }

    private static void createSpell(boolean isSpell, String cardName) {
        System.out.println("It is the need to tear and rend and coat their faces slick with the blood of the guilty that pulses through every fibre of them. Is your card limited?");
        var limit = IO.getInstance().getInputMessage();
        System.out.println("The thumping need inside their head to hate, and to be right within that hate. Type down the name of effect you want from the list below.\n" +
                "if you want to add more than one effect, simply write the names of the effects with a space between");
        printSpellAndTrapEffects();
        var name = IO.getInstance().getInputMessage();
        System.out.println("If the card you created is a field card that you wish to change the type of cards it affects positively," +
                " write the type of the monsters you want with a * between. if you dont want to change it, write the original types.");
        var typesPositive = IO.getInstance().getInputMessage().split("\\*");
        System.out.println("If the card you created is a field card that you wish to change the type of cards it affects negatively," +
                " write the type of the monsters you want with a * between. if you dont want to change it, write the original types.");
        var typesNegative = IO.getInstance().getInputMessage().split("\\*");
        System.out.println("The pounding in their heart drowns out the unease, makes it hard to taste and feel it out, but it is there. Choose a card property e.g. Continuous");
        var property = IO.getInstance().getInputMessage();
        System.out.println("""
                Restiamo un po' di tempo ancora, tanto non c'è fretta
                Ché c'ho una frase scritta in testa, ma non l'ho mai detta
                give me the description Marlena""");
        var description = IO.getInstance().getInputMessage();
        SpellAndTrapCard newCard = new SpellAndTrapCard();
        newCard.setName(name + " " + cardName);
        newCard.setProperty(property);
        newCard.setSpell(isSpell);
        newCard.setLimited(!limit.equals("yes"));
        newCard.setOriginal(false);
        newCard.setFieldNegativeEffects(typesNegative);
        newCard.setFieldPositiveEffects(typesPositive);
        newCard.setDescription(description);
        var price = !limit.equals("yes") && property.equals("Continuous") ? 200 : 0;
        int money = (Card.getCardByName(name).getPrice() + price) * 11 / 10;
        newCard.setPrice(money);
        MainController.getInstance().getLoggedIn().setMoney(MainController.getInstance().getLoggedIn().getMoney() - money);
        ShopController.getAllCards().add(newCard);
        ImportAndExport.getInstance().writeObjectToJson(ImportAndExport.RESOURCES_CARDS + newCard.getName(), newCard);
    }

    private static void printSpellAndTrapEffects() {
        var toPrint = new StringBuilder();
        ALL_SPELLANDTRAP_EFFECTS.forEach((key, value) -> toPrint.append(key).append(": ").append(value).append("\n"));
        toPrint.setLength(toPrint.length() - 1);
        System.out.println(toPrint);
    }

    private static void printMonsterEffects() {
        var toPrint = new StringBuilder();
        ALL_MONSTER_EFFECTS.forEach((key, value) -> toPrint.append(key).append(": ").append(value).append("\n"));
        toPrint.setLength(toPrint.length() - 1);
        System.out.println(toPrint);
    }
}
