package controller;

import com.google.gson.GsonBuilder;
import model.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImportAndExport {
    private static ImportAndExport singleInstance = null;

//    public static void main(String[] args) {
//        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
//        linkedHashMap.put("Change of Heart", "Target 1 monster your opponent controls; take control of it until the End Phase.");
//        linkedHashMap.put("Command Knight", "All Warrior-Type monsters you control gain 400 ATK. " +
//                "If you control another monster, monsters your opponent " +
//                "controls cannot target this card for an attack.");
//        linkedHashMap.put("Man-Eater Bug", "FLIP: Target 1 monster on the field; destroy that target.");
//        linkedHashMap.put("Messenger of peace", "Monsters with 1500 or more ATK cannot declare an attack." +
//                " Once per turn, during your Standby Phase, pay 100 LP or destroy this card.");
//        linkedHashMap.put("Suijin", "During damage calculation in your opponent's turn, if this card is being attacked:" +
//                " You can target the attacking monster;" +
//                " make that target's ATK 0 during damage calculation only (this is a Quick Effect). " +
//                "This effect can only be used once while this card is face-up on the field.");
//        linkedHashMap.put("The Calculator", "The ATK of this card is the combined Levels of all face-up monsters you control x 300.");
//        linkedHashMap.put("United We Stand", "The equipped monster gains 800 ATK/DEF for each face-up monster you control.");
//        linkedHashMap.put("Swords of Revealing Light", "After this card's activation, it remains on the field, but destroy it during the End Phase of your opponent's 3rd turn." +
//                " When this card is activated: If your opponent controls a face-down monster," +
//                " flip all monsters they control face-up. While this card is face-up on the field, your opponent's monsters cannot declare an attack.");
//        linkedHashMap.put("Sword of dark destruction", "A DARK monster equipped with this card increases its ATK by 400 points and decreases its DEF by 200 points.");
//        linkedHashMap.put("Black Pendant", "The equipped monster gains 500 ATK. When this card is sent from the field to the Graveyard: Inflict 500 damage to your opponent.");
//        linkedHashMap.put("Magnum Shield", "Equip only to a Warrior-Type monster. Apply this effect, depending on its battle position." +
//                "\n-Attack Position: It gains ATK equal to its original DEF." +
//                "\n-Defense Position: It gains DEF equal to its original ATK.");
//        linkedHashMap.put("Scanner", "Once per turn, you can select 1 of your opponent's monsters that is removed from play." +
//                " Until the End Phase, this card's name is treated as the selected monster's name, " +
//                "and this card has the same Attribute, Level, ATK, and DEF as the selected monster. " +
//                "If this card is removed from the field while this effect is applied, remove it from play.");
//        getInstance().readAllCards().forEach(c -> linkedHashMap.put(c.getName(), c.getDescription()));
//        TreeMap<String, String> cardNameToDescriptionTreeMap = new TreeMap<>(linkedHashMap);
//        getInstance().writeToJson("src/main/resources/utils/MapCardNameToCardDescription.JSON", cardNameToDescriptionTreeMap);
//    }

    public static ImportAndExport getInstance() {
        if (singleInstance == null)
            singleInstance = new ImportAndExport();
        return singleInstance;
    }

    public void writeAllUsers() {
        Account.getAllAccounts().forEach(a -> writeToJson("src/main/resources/users/" + a.getUsername() + ".JSON", a));
    }

    public void readAllUsers() {
        Arrays.stream(Objects.requireNonNull(new File("src/main/resources/users").listFiles())).filter(Objects::nonNull)
                .forEach(f -> Account.addAccount(readAccount("src/main/resources/users/" + f.getName())));
    }

    public void importCard(String cardName, String type) {
        if (type.equals("monster")) {
            var monsterCard = readMonsterCard("src/main/resources/importandexport/" + cardName + ".JSON");
            monsterCard.reset();
            ShopController.getAllCards().add(monsterCard);
        } else {
            var spellAndTrapCard = readSpellAndTrapCard("src/main/resources/importandexport/" + cardName + ".JSON");
            spellAndTrapCard.reset();
            ShopController.getAllCards().add(spellAndTrapCard);
        }
    }

    public void exportCard(String cardName) {
        writeToJson("src/main/resources/importandexport/" + cardName + ".JSON", Card.getCardByName(cardName));
    }

    public Account readAccount(String address) {
        try {
            var fileReader = new FileReader(address);
            var gsonBuilder = new GsonBuilder();
            var gson = gsonBuilder.create();
            var bufferedReader = new BufferedReader(fileReader);
            var account = gson.fromJson(bufferedReader, Account.class);
            account.setAbleToDraw(true);
            account.setAbleToAttack(true);
            return account;
        } catch (Exception fileNotFoundException) {
            return null;
        }
    }

    public ArrayList<PlayerDeck> readAllDecks(String address) {
        return (ArrayList<PlayerDeck>) Arrays.stream(Objects.requireNonNull(new File(address).listFiles()))
                .filter(Objects::nonNull).map(f -> readDeck(address + f.getName())).collect(Collectors.toList());
    }

    public PlayerDeck readDeck(String address) {
        try {
            var fileReader = new FileReader(address);
            var gsonBuilder = new GsonBuilder();
            var gson = gsonBuilder.create();
            var bufferedReader = new BufferedReader(fileReader);
            return gson.fromJson(bufferedReader, PlayerDeck.class);
        } catch (Exception fileNotFoundException) {
            return null;
        }
    }

    public Card readCard(String cardName) {
        var monsterCard = readMonsterCard("src/main/resources/monsters/" + cardName + ".JSON");
        var spellAndTrapCard = readSpellAndTrapCard("src/main/resources/spellandtraps/" + cardName + ".JSON");
        if (monsterCard == null && spellAndTrapCard != null) return spellAndTrapCard;
        if (monsterCard != null && spellAndTrapCard == null) {
            monsterCard.reset();
            return monsterCard;
        }
        return null;
    }

    public ArrayList<Card> readAllCards() {
        return Stream.concat(
                Arrays.stream(Objects.requireNonNull(new File("src/main/resources/monsters/").listFiles()))
                        .map(f -> readMonsterCard(f.getPath())).filter(Objects::nonNull).peek(MonsterCard::reset),
                Arrays.stream(Objects.requireNonNull(new File("src/main/resources/spellandtraps/").listFiles()))
                        .map(f -> readSpellAndTrapCard(f.getPath())).filter(Objects::nonNull))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public MonsterCard readMonsterCard(String address) {
        try {
            var fileReader = new FileReader(address);
            var gsonBuilder = new GsonBuilder();
            var gson = gsonBuilder.create();
            var bufferedReader = new BufferedReader(fileReader);
            return gson.fromJson(bufferedReader, MonsterCard.class);
        } catch (Exception fileNotFoundException) {
            return null;
        }
    }

    public SpellAndTrapCard readSpellAndTrapCard(String address) {
        try {
            var fileReader = new FileReader(address);
            var gsonBuilder = new GsonBuilder();
            var gson = gsonBuilder.create();
            var bufferedReader = new BufferedReader(fileReader);
            return gson.fromJson(bufferedReader, SpellAndTrapCard.class);
        } catch (Exception fileNotFoundException) {
            return null;
        }
    }

    public TreeMap<String, String> readCardNameToDescriptionMap() {
        try {
            var fileReader = new FileReader("src/main/resources/utils/MapCardNameToCardDescription.JSON");
            var gsonBuilder = new GsonBuilder();
            var gson = gsonBuilder.create();
            var bufferedReader = new BufferedReader(fileReader);
            return gson.fromJson(bufferedReader, TreeMap.class);
        } catch (Exception fileNotFoundException) {
            return null;
        }
    }

    public void writeToJson(String address, Object object) {
        var gsonBuilder = new GsonBuilder();
        var gson = gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(address);
            fileWriter.write(gson.toJson(object));
            fileWriter.close();
        } catch (IOException ignored) {
        }
    }

//    public GameDeck getGameDeck(PlayerDeck playerDeck) {
//        //TODO this method or GameDeck's constructor?
//    }

}
