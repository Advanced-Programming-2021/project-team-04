package yugioh.view;

import yugioh.controller.ShopController;
import yugioh.model.cards.Card;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreatorView {


    private static final HashMap<String, String> allMonsterEffects = new HashMap<>();
    private static final HashMap<String, String> allSTEffects = new HashMap<>();

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
        String cardName = IO.getInstance().getInputMessage();
        System.out.println("Do you prefer the irresistible monsters or want to play with some magic?\n" +
                "choose monster or trap or spell");
        String cardType = IO.getInstance().getInputMessage();
        if (cardType.equals("monster")) createMonster(cardName);
        else createSpell(cardType.equals("spell"), cardName);
    }

    private static void createMonster(String cardName) {
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
           MonsterCard monsterCard = monsterPrice(attackPower, defencePower, monsterType, cardType, level, name, cardName);
           ShopController.getAllCards().add(monsterCard);
        }
        System.out.println("Great! You’ll get used to the world we created. Or you'll try I guess...");
    }

    private static MonsterCard monsterPrice(int attackPower, int defencePower, String monsterType, String cardType, int level, String name, String cardName) {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName(name);
        int price = 0;
        if (monsterCard.getClassAttackPower() > attackPower) price += 100;
        if (monsterCard.getClassDefensePower() > defencePower) price +=100;
        if (monsterCard.getLevel() > level) price += 100;
        price *= 4;
        MonsterCard newCard = new MonsterCard();
        newCard.setName(name + " " + cardName);
        newCard.setLevel(level);
        newCard.setClassAttackPower(attackPower);
        newCard.setClassDefensePower(defencePower);
        newCard.setThisCardAttackPower(attackPower);
        newCard.setThisCardDefensePower(defencePower);
        newCard.setCardType(cardType);
        newCard.setMonsterType(monsterType);
        newCard.setPrice((monsterCard.getPrice() + price) * 11/10);
        newCard.setOriginal(false);
        return newCard;
    }

    private static void createSpell(boolean isSpell, String cardName) {
        System.out.println("It is the need to tear and rend and coat their faces slick with the blood of the guilty that pulses through every fibre of them. Is your card limited?");
        String limit = IO.getInstance().getInputMessage();
        System.out.println("The thumping need inside their head to hate, and to be right within that hate. Type down the name of effect you want from the list below");
        createSTEffects();
        String name = IO.getInstance().getInputMessage();
        System.out.println("If the card you created is a field card that you wish to change the type of cards it affects positively," +
                " write the type of the monsters you want with a * between. if you dont want to change it, write the original types.");
        String[] typesPositive = IO.getInstance().getInputMessage().split("\\*");
        System.out.println("If the card you created is a field card that you wish to change the type of cards it affects negatively," +
                " write the type of the monsters you want with a * between. if you dont want to change it, write the original types.");
        String[] typesNegative = IO.getInstance().getInputMessage().split("\\*");
        System.out.println("The pounding in their heart drowns out the unease, makes it hard to taste and feel it out, but it is there. Choose a card property e.g. Continuous");
        String property = IO.getInstance().getInputMessage();
        SpellAndTrapCard newCard = new SpellAndTrapCard();
        newCard.setName(cardName + " " + name);
        newCard.setProperty(property);
        newCard.setSpell(isSpell);
        newCard.setLimited(!limit.equals("yes"));
        newCard.setOriginal(false);
        newCard.setFieldNegativeEffects(typesNegative);
        newCard.setFieldPositiveEffects(typesPositive);
        int price = 0;
        if (!limit.equals("yes") && property.equals("Continuous")) price = 200;
        newCard.setPrice((Card.getCardByName(name).getPrice() + price) * 11/10);
        ShopController.getAllCards().add(newCard);
    }

    private static void createSTEffects() {
        StringBuilder toPrint = new StringBuilder();
        allSTEffects.put("Trap Hole", "When your opponent Normal or Flip Summons 1 monster with 1000 or more ATK: Target that monster; destroy that target.");
        allSTEffects.put("Mirror Force", "When an opponent's monster declares an attack: Destroy all your opponent's Attack Position monsters.");
        allSTEffects.put("Magic Cylinder", "When an opponent's monster declares an attack: Target the attacking monster; negate the attack, and if you do, inflict damage to your opponent equal to its ATK.");
        allSTEffects.put("Mind Crush", "Declare 1 card name; if that card is in your opponent's hand, they must discard all copies of it, otherwise you discard 1 random card.");
        allSTEffects.put("Torrential Tribute", "When a monster(s) is Summoned: Destroy all monsters on the field.");
        allSTEffects.put("Time Seal", "Skip the Draw Phase of your opponent's next turn.");
        allSTEffects.put("Negate Attack", "When an opponent's monster declares an attack: Target the attacking monster; negate the attack, then end the Battle Phase.");
        allSTEffects.put("Solemn Warning", "When a monster(s) would be Summoned, OR when a Spell/Trap Card, or monster effect, is activated that includes an effect that Special Summons a monster(s): Pay 2000 LP; negate the Summon or activation, and if you do, destroy it.");
        allSTEffects.put("Magic Jamamer", "When a Spell Card is activated: Discard 1 card; negate the activation, and if you do, destroy it.");
        allSTEffects.put("Call of The Haunted", "Activate this card by targeting 1 monster in your GY; Special Summon that target in Attack Position. When this card leaves the field, destroy that monster. When that monster is destroyed, destroy this card.");
        allSTEffects.put("Vanity's Emptiness", "Neither player can Special Summon monsters. If a card is sent from the Deck or the field to your Graveyard: Destroy this card.");
        allSTEffects.put("Wall of Revealing Light", "Activate by paying any multiple of 1000 Life Points. Monsters your opponent controls cannot attack if their ATK is less than or equal to the amount you paid.");
        allSTEffects.put("Monster Reborn", "Target 1 monster in either GY; Special Summon it.");
        allSTEffects.put("Terraforming", "Add 1 Field Spell from your Deck to your hand.");
        allSTEffects.put("Pot of Greed", "Draw 2 cards.");
        allSTEffects.put("Raigeki", "Destroy all monsters your opponent controls.");
        allSTEffects.put("Harpie's Feather Duster", "Destroy all Spells and Traps your opponent controls.");
        allSTEffects.put("Dark Hole", "Destroy all monsters on the field.");
        allSTEffects.put("Supply Squad", "Once per turn, if a monster(s) you control is destroyed by battle or card effect: Draw 1 card.");
        allSTEffects.put("Spell Absorption", "Each time a Spell Card is activated, gain 500 Life Points immediately after it resolves.");
        allSTEffects.put("Twin Twisters", "Discard 1 card, then target up to 2 Spells/Traps on the field; destroy them.");
        allSTEffects.put("Mystical space typhoon", "Target 1 Spell/Trap on the field; destroy that target.");
        allSTEffects.put("Ring of defense", "When a Trap effect that inflicts damage is activated: Make that effect damage 0.");
        allSTEffects.put("Yami", "All Fiend and Spellcaster monsters on the field gain 200 ATK/DEF, also all Fairy monsters on the field lose 200 ATK/DEF.");
        allSTEffects.put("Forest", "All Insect, Beast, Plant, and Beast-Warrior monsters on the field gain 200 ATK/DEF.");
        allSTEffects.put("Closed Forest", "All Beast-Type monsters you control gain 100 ATK for each monster in your Graveyard. Field Spell Cards cannot be activated. Field Spell Cards cannot be activated during the turn this card is destroyed.");
        allSTEffects.put("Umiiruka", "Increase the ATK of all WATER monsters by 500 points and decrease their DEF by 400 points.");
        allSTEffects.put("Advanced Ritual Art", "This card can be used to Ritual Summon any 1 Ritual Monster. You must also send Normal Monsters from your Deck to the Graveyard whose total Levels equal the Level of that Ritual Monster.");
        for (Map.Entry mapElement : allSTEffects.entrySet())
            toPrint.append(mapElement.getKey()).append(": ").append(mapElement.getValue()).append("\n");
        System.out.println(toPrint.toString());
    }

    private static void createMonsterEffects() {
        StringBuilder toPrint = new StringBuilder();
        allMonsterEffects.put("Yomi Ship","If this card is destroyed by battle and sent to the GY: Destroy the monster that destroyed this card.");
        allMonsterEffects.put("Suijin", "During damage calculation in your opponent's turn, if this card is being attacked: You can target the attacking monster; make that target's ATK 0 during damage calculation only (this is a Quick Effect). This effect can only be used once while this card is face-up on the field.");
        allMonsterEffects.put("Crab Turtle","This monster can only be Ritual Summoned with the Ritual Spell Card, \"Turtle Oath\". You must also offer monsters whose total Level Stars equal 8 or more as a Tribute from the field or your hand.");
        allMonsterEffects.put("ManEater Bug","FLIP: Target 1 monster on the field; destroy that target.");
        allMonsterEffects.put("Marshmallon", "Cannot be destroyed by battle. After damage calculation, if this card was attacked, and was face-down at the start of the Damage Step: The attacking player takes 1000 damage.");
        allMonsterEffects.put("Texchanger", "Once per turn, when your monster is targeted for an attack: You can negate that attack, then Special Summon 1 Cyberse Normal Monster from your hand, Deck, or GY.");
        allMonsterEffects.put("The Calculator", "The ATK of this card is the combined Levels of all face-up monsters you control x 300.");
        allMonsterEffects.put("Mirage Dragon", "Your opponent cannot activate Trap Cards during the Battle Phase.");
        allMonsterEffects.put("Herald of Creation", "Once per turn: You can discard 1 card, then target 1 Level 7 or higher monster in your Graveyard; add that target to your hand.");
        allMonsterEffects.put("Exploder Dragon", "If this card is destroyed by battle and sent to the Graveyard: Destroy the monster that destroyed it. Neither player takes any battle damage from attacks involving this attacking card.");
        allMonsterEffects.put("Terratiger, the Empowered Warrior", "When this card is Normal Summoned: You can Special Summon 1 Level 4 or lower Normal Monster from your hand in Defense Position.");
        allMonsterEffects.put("Not Special", "You are unbowed and, yes, afraid, but still the music plays, and turns the world upon its gaudy axis. You will be someone again, someday.");
        for (Map.Entry mapElement : allMonsterEffects.entrySet())
            toPrint.append(mapElement.getKey()).append(": ").append(mapElement.getValue()).append("\n");
        System.out.println(toPrint.toString());
    }
}
