package controller;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Account;
import model.MonsterCard;
import model.SpellAndTrapCard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.AcceptPendingException;

public class ImportAndExport {
    private static ImportAndExport singleInstance = null;
    public static ImportAndExport getInstance() {
        if (singleInstance == null)
            singleInstance = new ImportAndExport();
        return singleInstance;
    }

    public MonsterCard readMonsterCard(String address) {
        try {
            FileReader fileReader = new FileReader(address);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            return gson.fromJson(bufferedReader, MonsterCard.class);
        }
        catch (Exception FileNotFoundException) {
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
        }
        catch (Exception FileNotFoundException) {
            return null;
        }
    }

    public void writeFile(String address, Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
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
