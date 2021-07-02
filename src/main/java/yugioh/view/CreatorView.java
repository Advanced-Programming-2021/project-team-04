package yugioh.view;

import yugioh.model.cards.Card;
import yugioh.model.cards.MonsterCard;

import java.util.HashMap;
import java.util.Map;

public class CreatorView {


    private static HashMap<String, String> allMonsterEffects = new HashMap<>();
    private static HashMap<String, String> allSTEffects = new HashMap<>();

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
        IO.getInstance().getInputMessage();
        System.out.println("Do you prefer the irresistible monsters or want to play with some magic?\n" +
                "choose monster or trap or spell");
        String cardType = IO.getInstance().getInputMessage();
        if (cardType.equals("monster")) createMonster();
        else createSpell(cardType.equals("spell"));
    }

    private static void createMonster() {
        System.out.println("Cruelty free, type down the attack power!");
        int attackPower = Integer.parseInt(IO.getInstance().getInputMessage());
        System.out.println("I’ve been wondering about your batteries, give us your defence power");
        int defencePower = Integer.parseInt(IO.getInstance().getInputMessage());
        System.out.println("You shall be a king of a ruined world. Choose your monster type! e.g Effect");
        String monsterType = IO.getInstance().getInputMessage();
        System.out.println("Now choose the card type! e.g Machine");
        String cardType = IO.getInstance().getInputMessage();
        System.out.println("Lastly, enter your card's level.");
        int level = Integer.parseInt(IO.getInstance().getInputMessage());
        System.out.println("you'll see a list down here. choose the name of the effect you want to add to your card.");
        createMonsterEffects();
        String name = IO.getInstance().getInputMessage();
        if (!name.equals("Not Special")) {
            monsterPrice(attackPower, defencePower, monsterType, cardType, level, name);
        }
        System.out.println("Great! You’ll get used to the world we created. Or you'll try I guess...");
    }

    private static void monsterPrice(int attackPower, int defencePower, String monsterType, String cardType, int level, String name) {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName(name);
        int price = 0;
        if (monsterCard.getClassAttackPower() > attackPower) price += 100;
        if (monsterCard.getClassDefensePower() > defencePower) price +=100;
        if (monsterCard.getLevel() > level) price += 100;
        price *= 4;
        MonsterCard newCard = new MonsterCard();
        newCard.setName(name);
        newCard.setLevel(level);
        newCard.setClassAttackPower(attackPower);
        newCard.setClassDefensePower(defencePower);
        newCard.setThisCardAttackPower(attackPower);
        newCard.setThisCardDefensePower(defencePower);
        newCard.setCardType(cardType);
        newCard.setMonsterType(monsterType);
        newCard.setPrice(monsterCard.getPrice() + price);
    }

    private static void createSpell(boolean isSpell) {

    }

    private static void createMonsterEffects() {
        StringBuilder toPrint = new StringBuilder();
        allMonsterEffects.put("Yomi Ship","If this card is destroyed by battle and sent to the GY: Destroy the monster that destroyed this card.");
        allMonsterEffects.put("Suijin", "During damage calculation in your opponent's turn, if this card is being attacked: You can target the attacking monster; make that target's ATK 0 during damage calculation only (this is a Quick Effect). This effect can only be used once while this card is face-up on the field.");
        allMonsterEffects.put("Crab Turtle","This monster can only be Ritual Summoned with the Ritual Spell Card, \"Turtle Oath\". You must also offer monsters whose total Level Stars equal 8 or more as a Tribute from the field or your hand.");
        allMonsterEffects.put("ManEater Bug","FLIP: Target 1 monster on the field; destroy that target.");
        allMonsterEffects.put("Scanner", "Once per turn, you can select 1 of your opponent's monsters that is removed from play. Until the End Phase, this card's name is treated as the selected monster's name, and this card has the same Attribute, Level, ATK, and DEF as the selected monster. If this card is removed from the field while this effect is applied, remove it from play.");
        allMonsterEffects.put("Marshmallon", "Cannot be destroyed by battle. After damage calculation, if this card was attacked, and was face-down at the start of the Damage Step: The attacking player takes 1000 damage.");
        allMonsterEffects.put("Texchanger", "Once per turn, when your monster is targeted for an attack: You can negate that attack, then Special Summon 1 Cyberse Normal Monster from your hand, Deck, or GY.");
        allMonsterEffects.put("The Calculator", "The ATK of this card is the combined Levels of all face-up monsters you control x 300.");
        allMonsterEffects.put("Mirage Dragon", "Your opponent cannot activate Trap Cards during the Battle Phase.");
        allMonsterEffects.put("Herald of Creation", "Once per turn: You can discard 1 card, then target 1 Level 7 or higher monster in your Graveyard; add that target to your hand.");
        allMonsterEffects.put("Exploder Dragon", "If this card is destroyed by battle and sent to the Graveyard: Destroy the monster that destroyed it. Neither player takes any battle damage from attacks involving this attacking card.");
        allMonsterEffects.put("Terratiger, the Empowered Warrior", "When this card is Normal Summoned: You can Special Summon 1 Level 4 or lower Normal Monster from your hand in Defense Position.");
        allMonsterEffects.put("Command Knight", "All Warrior-Type monsters you control gain 400 ATK. If you control another monster, monsters your opponent controls cannot target this card for an attack.");
        allMonsterEffects.put("Not Special", "You are unbowed and, yes, afraid, but still the music plays, and turns the world upon its gaudy axis. You will be someone again, someday.");
        for (Map.Entry mapElement : allMonsterEffects.entrySet())
            toPrint.append(mapElement.getKey()).append(": ").append(mapElement.getValue()).append("\n");
        System.out.println(toPrint.toString());
    }
}
