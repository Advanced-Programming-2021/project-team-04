package controller;

import com.google.gson.GsonBuilder;
import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImportAndExport {
    private static ImportAndExport singleInstance = null;

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
