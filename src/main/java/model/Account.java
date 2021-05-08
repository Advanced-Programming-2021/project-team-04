package model;

import controller.DuelController;

import java.util.ArrayList;

public class Account extends Duelist {

    private static ArrayList<Account> allAccounts;
    private static ArrayList<String> allUsernames;
    private static ArrayList<String> allNicknames;

    static {
        allAccounts = new ArrayList<>();
        allNicknames = new ArrayList<>();
        allUsernames = new ArrayList<>();
    }

    private String password;
    private int score, money = 100000;

    public Account(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        allAccounts.add(this);
    }

    public static void setAllAccounts(ArrayList<Account> allAccounts) {
        Account.allAccounts = allAccounts;
    }

    public static void setAllUsernames(ArrayList<String> allUsernames) {
        Account.allUsernames = allUsernames;
    }

    public static void setAllNicknames(ArrayList<String> allNicknames) {
        Account.allNicknames = allNicknames;
    }

    public void setAllDecks(ArrayList<Deck> allDecks) {
        this.allDecks = allDecks;
    }

    public void setAllCards(ArrayList<Card> allCards) {
        this.allCards = allCards;
    }

    public void setActiveDeck(Deck activeDeck) {
        this.activeDeck = activeDeck;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public static ArrayList<Account> getAllAccounts() {
        return allAccounts;
    }

    public static ArrayList<String> getAllUsernames() {
        return allUsernames;
    }

    public static ArrayList<String> getAllNicknames() {
        return allNicknames;
    }

    public String getPassword() {
        return password;
    }

    public int getScore() {
        return score;
    }

    public int getMoney() {
        return money;
    }

    public boolean hasEnoughMoney(int price) {
        return this.money >= price;
    }



    public static Account getAccountByUsername(String username) {
        for (Account account : allAccounts)
            if (account.getUsername().equals(username))
                return account;
        return null;
    }


}
