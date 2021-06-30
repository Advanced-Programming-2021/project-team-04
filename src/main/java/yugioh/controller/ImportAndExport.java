package yugioh.controller;


import com.google.gson.GsonBuilder;
import yugioh.model.Account;
import yugioh.model.PlayerDeck;
import yugioh.model.cards.Card;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ImportAndExport {


    public static final String JSON = ".JSON";
    public static final String RESOURCES_USERS = "src/main/resources/users/";
    public static final String RESOURCES_IMPORTANDEXPORT = "src/main/resources/importandexport/";
    public static final String RESOURCES_MONSTERS = "src/main/resources/monsters/";
    public static final String RESOURCES_SPELLANDTRAPS = "src/main/resources/spellandtraps/";


    private static ImportAndExport singleInstance = null;


    public static ImportAndExport getInstance() {
        if (singleInstance == null)
            singleInstance = new ImportAndExport();
        return singleInstance;
    }


    public void writeAllUsers() {
        Account.getAllAccounts().forEach(a -> writeToJson(RESOURCES_USERS + a.getUsername() + JSON, a));
    }


    public void readAllUsers() {
        Arrays.stream(Objects.requireNonNull(new File(RESOURCES_USERS).listFiles())).filter(Objects::nonNull)
                .forEach(f -> Account.addAccount(readAccount(RESOURCES_USERS + f.getName())));
    }


    public void importCard(String cardName, String type) {
        if (type.equals("monster")) {
            var monsterCard = readMonsterCard(RESOURCES_IMPORTANDEXPORT + cardName + JSON);
            monsterCard.reset();
            ShopController.getAllCards().add(monsterCard);
        } else {
            var spellAndTrapCard = readSpellAndTrapCard(RESOURCES_IMPORTANDEXPORT + cardName + JSON);
            spellAndTrapCard.reset();
            ShopController.getAllCards().add(spellAndTrapCard);
        }
    }


    public void exportCard(String cardName) {
        writeToJson(RESOURCES_IMPORTANDEXPORT + cardName + JSON, Card.getCardByName(cardName));
    }


    public Account readAccount(String address) {
        try {
            var account = new GsonBuilder().create().fromJson(new BufferedReader(new FileReader(address)), Account.class);
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
            return new GsonBuilder().create().fromJson(new BufferedReader(new FileReader(address)), PlayerDeck.class);
        } catch (Exception fileNotFoundException) {
            return null;
        }
    }


    public Card readCard(String cardName) throws Exception {
        if (Card.isCardSpecial(cardName))
            return (Card) Class.forName(Card.getSpecialCardClassName(cardName)).getConstructor().newInstance();
        var monsterCard = readMonsterCard(RESOURCES_MONSTERS + cardName + JSON);
        var spellAndTrapCard = readSpellAndTrapCard(RESOURCES_SPELLANDTRAPS + cardName + JSON);
        if (monsterCard == null && spellAndTrapCard != null) return spellAndTrapCard;
        if (monsterCard != null && spellAndTrapCard == null) {
            monsterCard.reset();
            return monsterCard;
        }
        return null;
    }


    public ArrayList<Card> readAllCards() {
        return Stream.concat(
                Arrays.stream(Objects.requireNonNull(new File(RESOURCES_MONSTERS).listFiles()))
                        .map(f -> readMonsterCard(f.getPath())).filter(Objects::nonNull).peek(MonsterCard::reset),
                Arrays.stream(Objects.requireNonNull(new File(RESOURCES_SPELLANDTRAPS).listFiles()))
                        .map(f -> readSpellAndTrapCard(f.getPath())).filter(Objects::nonNull))
                .collect(Collectors.toCollection(ArrayList::new));
    }


    public MonsterCard readMonsterCard(String address) {
        try {
            return new GsonBuilder().create().fromJson(new BufferedReader(new FileReader(address)), MonsterCard.class);
        } catch (Exception fileNotFoundException) {
            return null;
        }
    }


    public SpellAndTrapCard readSpellAndTrapCard(String address) {
        try {
            return new GsonBuilder().create().fromJson(new BufferedReader(new FileReader(address)), SpellAndTrapCard.class);
        } catch (Exception fileNotFoundException) {
            return null;
        }
    }


    public TreeMap<String, String> readCardNameToDescriptionMap() {
        try {
            return new GsonBuilder().create().fromJson(new BufferedReader(new FileReader("src/main/resources/utils/MapCardNameToCardDescription.JSON")), TreeMap.class);
        } catch (Exception fileNotFoundException) {
            return null;
        }
    }


    public HashMap<String, String> readSpecialCardNameToClassNameMap() {
        try {
            return new GsonBuilder().create().fromJson(new BufferedReader(new FileReader("src/main/resources/utils/MapSpecialCardNameToClassName.JSON")), HashMap.class);
        } catch (Exception fileNotFoundException) {
            return null;
        }
    }


    public void writeToJson(String address, Object object) {
        try {
            var fileWriter = new FileWriter(address);
            fileWriter.write(new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(object));
            fileWriter.close();
        } catch (IOException ignored) { }
    }
}