package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class ImportAndExport {
    private static ImportAndExport singleInstance = null;

    public static ImportAndExport getInstance() {
        if (singleInstance == null)
            singleInstance = new ImportAndExport();
        return singleInstance;
    }

//    public static void main(String[] args) {
//        Account account = new Account("BoJack Horseman", "the password", "BoJack");
//        Deck deck = new Deck("First AI Deck");
//        MainController.getInstance().setLoggedIn(account);
//        account.addDeck(deck);
//        account.setActiveDeck(deck);
//        getInstance().readAllMonsterCards(account, deck);
//        getInstance().writeToJson("src/main/resources/decks/First AI Deck.JSON", deck);
//        Deck deck = getInstance().readDeck("src/main/resources/decks/First AI Deck.JSON");
//        System.out.println(deck.isDeckValid());
//    }
//
//    public void readAllMonsterCards(Account account, Deck deck) {
//        File folder = new File("src/main/resources/monsters");
//        MonsterCard monsterCard;
//        for (File file : Objects.requireNonNull(folder.listFiles()))
//            for (int i = 0; i < 2; i++) {
//                monsterCard = readMonsterCard("src/main/resources/monsters/" + file.getName());
//                account.addCard(monsterCard);
//                DeckController.getInstance().addCardToDeck(deck.getDeckName(), monsterCard.getName(), !deck.isMainDeckFull());
//            }
//    }

    public static void main(String[] args) {
        Account account = new Account("BoJack Horseman", "the password", "BoJack");
        GameDeck deck = new GameDeck("First AI Deck");
        MainController.getInstance().setLoggedIn(account);
        account.addDeck(deck);
        account.setActiveDeck(deck);
        deck.setMainDeck(getInstance().readAllCards());
        getInstance().writeAllUsers();
    }

    public void writeAllUsers() {
        ArrayList<Account> allAccounts = Account.getAllAccounts();
        for (Account account : allAccounts)
            writeToJson("src/main/resources/users/" + account.getUsername() + ".JSON", account);
    }

    public void readAllUsers() {
        var folder = new File("src/main/resources/users");
        for (File file : Objects.requireNonNull(folder.listFiles()))
            Account.addAccount(readAccount("src/main/resources/users/" + file.getName()));
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
            return gson.fromJson(bufferedReader, Account.class);
        } catch (Exception fileNotFoundException) {
            return null;
        }
    }

    public ArrayList<GameDeck> readAllDecks(String address) {
        ArrayList<GameDeck> gameDecks = new ArrayList<>();
        var folder = new File(address);
        for (File file : Objects.requireNonNull(folder.listFiles()))
            gameDecks.add(readDeck(address + file.getName()));
        return gameDecks;
    }

    public GameDeck readDeck(String address) {
        try {
            var fileReader = new FileReader(address);
            var gsonBuilder = new GsonBuilder();
            var gson = gsonBuilder.create();
            var bufferedReader = new BufferedReader(fileReader);
            return gson.fromJson(bufferedReader, GameDeck.class);
        } catch (Exception fileNotFoundException) {
            return null;
        }
    }

    public Card readCard(String cardName) {
        var monsterCard = readMonsterCard("src/main/resources/monsters/" + cardName + ".JSON");
        var spellAndTrapCard = readSpellAndTrapCard("src/main/resources/spellandtraps/" + cardName + ".JSON");
        if (monsterCard == null && spellAndTrapCard != null) return spellAndTrapCard;
        if (monsterCard != null && spellAndTrapCard == null) return monsterCard;
        return null;
    }

    public ArrayList<Card> readAllCards() {
        ArrayList<Card> cards = new ArrayList<>();
        var folder = new File("src/main/resources/monsters/");
        MonsterCard monsterCard;
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if ((monsterCard = readMonsterCard(file.getPath())) == null) continue;
            monsterCard.reset();
            cards.add(monsterCard);
        }
        SpellAndTrapCard spellAndTrapCard;
        folder = new File("src/main/resources/spellandtraps/");
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if ((spellAndTrapCard = readSpellAndTrapCard(file.getPath())) == null) continue;
            cards.add(spellAndTrapCard);
        }
        return cards;
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
