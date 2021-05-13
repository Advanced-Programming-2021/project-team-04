package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Account;
import model.Card;
import model.MonsterCard;
import model.SpellAndTrapCard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.AcceptPendingException;
import java.util.ArrayList;

public class ImportAndExport {
    private static ImportAndExport singleInstance = null;

    public static ImportAndExport getInstance() {
        if (singleInstance == null)
            singleInstance = new ImportAndExport();
        return singleInstance;
    }

    public void writeAllUsers() {
        ArrayList<Account> allAccounts = Account.getAllAccounts();
        for (Account account : allAccounts)
            writeToJson("src/main/resources/users/" + account.getUsername() + ".JSON", account);
    }

    public void readAllUsers() {
        //TODO how to read all of the files
    }

    public void importCard(String cardName, String type) {
        if (type.equals("monster")) {
            MonsterCard monsterCard = readMonsterCard("src/main/resources/importandexport/" + cardName + ".JSON");
            monsterCard.reset();
            ShopController.getAllCards().add(monsterCard);
        } else {
            SpellAndTrapCard spellAndTrapCard = readSpellAndTrapCard("src/main/resources/importandexport/" + cardName + ".JSON");
            spellAndTrapCard.reset();
            ShopController.getAllCards().add(spellAndTrapCard);
        }
    }

    public void exportCard(String cardName) {
        writeToJson("src/main/resources/importandexport/" + cardName + ".JSON", Card.getCardByName(cardName));
    }

    public MonsterCard readMonsterCard(String address) {
        try {
            FileReader fileReader = new FileReader(address);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            return gson.fromJson(bufferedReader, MonsterCard.class);
        } catch (Exception FileNotFoundException) {
            return null;
        }
    }

    public SpellAndTrapCard readSpellAndTrapCard(String address) {
        try {
            FileReader fileReader = new FileReader(address);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            return gson.fromJson(bufferedReader, SpellAndTrapCard.class);
        } catch (Exception FileNotFoundException) {
            return null;
        }
    }

    public void writeToJson(String address, Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(address);
            fileWriter.write(gson.toJson(object));
            fileWriter.close();
        } catch (IOException ignored) {
        }
    }

    public Account readAccount(String fileAddress) {
        try (FileReader fileReader = new FileReader(fileAddress)) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            return gson.fromJson(bufferedReader, Account.class);
        } catch (IOException e) {
            return null;
        }
    }
}
