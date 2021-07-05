package yugioh.controller;


import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import yugioh.model.Account;
import yugioh.model.PlayerDeck;
import yugioh.model.cards.Card;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;
import yugioh.model.cards.specialcards.BlackPendant;
import yugioh.view.ImportAndExportView;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ImportAndExport {

    public static final String JSON = ".JSON";
    public static final String RESOURCES_USERS = "src/main/resources/users/";
    public static final String RESOURCES_CARDS = "src/main/resources/cards/";
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final ObjectMapper WRITER = new ObjectMapper();
    public static final CsvMapper CSV_MAPPER = new CsvMapper();

    private static ImportAndExport singleInstance = null;

    static {
        OBJECT_MAPPER.disable(MapperFeature.AUTO_DETECT_CREATORS, MapperFeature.AUTO_DETECT_FIELDS,
                MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_IS_GETTERS);
        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Card.class).allowIfBaseType(MonsterCard.class).allowIfBaseType(SpellAndTrapCard.class)
                .build();
        WRITER.disable(MapperFeature.AUTO_DETECT_CREATORS, MapperFeature.AUTO_DETECT_FIELDS,
                MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_IS_GETTERS);
        WRITER.activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL);
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
        return Objects.requireNonNull(OBJECT_MAPPER.readValue(new File(RESOURCES_CARDS + cardName + JSON), Card.class));
    }

    public Card readCard(File file) throws Exception {
        return Objects.requireNonNull(OBJECT_MAPPER.readValue(file, Card.class));
    }

    public ArrayList<Card> readAllCards() {
        return Arrays.stream(Objects.requireNonNull(new File(RESOURCES_CARDS).listFiles())).map(f -> {
            try {
                return readCard(f);
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
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
        ImportAndExportView.runImportPage(cards);
        cards.stream().filter(Objects::nonNull).forEach(c -> writeObjectToJson(RESOURCES_CARDS + c.getName() + JSON, c));
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
            WRITER.writeValue(new File(address), object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}