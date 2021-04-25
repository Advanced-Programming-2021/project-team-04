package controller;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.MonsterCard;
import model.SpellAndTrapCard;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

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
}
