package yugioh.controller;


import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import yugioh.model.Account;
import yugioh.model.Field;
import yugioh.model.PlayerDeck;
import yugioh.model.cards.Card;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;
import yugioh.view.ImportAndExportView;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ImportAndExport {

    public static final String JSON = ".JSON";
    public static final String RESOURCES_USERS = "src/main/resources/users/";
    public static final String RESOURCES_IMPORTANDEXPORT = "src/main/resources/importandexport/";
    public static final String RESOURCES_MONSTERS = "src/main/resources/monsters/";
    public static final String RESOURCES_SPELLANDTRAPS = "src/main/resources/spellandtraps/";
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final CsvMapper CSV_MAPPER = new CsvMapper();

    private static ImportAndExport singleInstance = null;

    static {
        OBJECT_MAPPER.disable(MapperFeature.AUTO_DETECT_CREATORS, MapperFeature.AUTO_DETECT_FIELDS,
                MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_IS_GETTERS);
    }

    public static ImportAndExport getInstance() {
        if (singleInstance == null)
            singleInstance = new ImportAndExport();
        return singleInstance;
    }

    public void writeAllUsers() {
        Account.getAllAccounts().forEach(a -> writeObjectToJson(RESOURCES_USERS + a.getUsername() + JSON, a));
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

    public void exportCard(File directory, Card card) {
        writeObjectToJson(directory.getAbsolutePath() + "/" + card.getName() + JSON, card);
    }

    public Account readAccount(String address) {
        try {
            var account = OBJECT_MAPPER.readValue(new File(address), Account.class);
            account.setAbleToDraw(true);
            account.setAbleToAttack(true);
            return account;
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<PlayerDeck> readAllDecks(String address) {
        return (ArrayList<PlayerDeck>) Arrays.stream(Objects.requireNonNull(new File(address).listFiles()))
                .filter(Objects::nonNull).map(f -> readDeck(address + f.getName())).collect(Collectors.toList());
    }

    public PlayerDeck readDeck(String address) {
        try {
            return OBJECT_MAPPER.readValue(new File(address), PlayerDeck.class);
        } catch (Exception e) {
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
            return OBJECT_MAPPER.readValue(new File(address), MonsterCard.class);
        } catch (Exception e) {
            return null;
        }
    }

    public SpellAndTrapCard readSpellAndTrapCard(String address) {
        try {
            return OBJECT_MAPPER.readValue(new File(address), SpellAndTrapCard.class);
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, String> readCardNameToDescriptionMap() {
        try {
            return OBJECT_MAPPER.readValue(new File("src/main/resources/utils/MapCardNameToCardDescription.JSON"), HashMap.class);
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, String> readSpecialCardNameToClassNameMap() {
        try {
            return OBJECT_MAPPER.readValue(new File("src/main/resources/utils/MapSpecialCardNameToClassName.JSON"), HashMap.class);
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, String> readAllMonsterEffects() {
        try {
            return OBJECT_MAPPER.readValue(new File("src/main/resources/utils/AllMonstersEffectMap.JSON"), HashMap.class);
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, String> readAllSpellAndTrapEffects() {
        try {
            return OBJECT_MAPPER.readValue(new File("src/main/resources/utils/AllSpellAndTrapEffectMap.JSON"), HashMap.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void importFile(File file) {
        List<Card> cards = readCardsFromFile(file);
        ImportAndExportView.showCards(cards);
        cards.stream().filter(Objects::nonNull).forEach(c -> writeObjectToJson(RESOURCES_IMPORTANDEXPORT + c.getName() + JSON, c));
    }

    public List readCardsFromFile(File file) {
        var fileExtension = getFileExtension(file);
        if (fileExtension.equalsIgnoreCase("csv"))
            try {
                return CSV_MAPPER.readerFor(Card.class).with(CsvSchema.emptySchema().withHeader()).readValues(file).readAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        if (fileExtension.equalsIgnoreCase("json"))
            try {
                return List.of(OBJECT_MAPPER.readValue(file, Card.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        return List.of();
    }

    public String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    public void writeObjectToJson(String address, Object object) {
        try {
            OBJECT_MAPPER.writeValue(new File(address), object);
        } catch (Exception ignored) {
        }
    }
}