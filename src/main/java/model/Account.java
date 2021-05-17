package model;

import com.google.gson.annotations.Expose;

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

    @Expose(serialize = true, deserialize = true)
    private String password;
    @Expose(serialize = true, deserialize = true)
    private int score;
    @Expose(serialize = true, deserialize = true)
    private int money = 100000;

    public Account(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        allAccounts.add(this);
        allUsernames.add(username);
        allNicknames.add(nickname);
    }

    public static void addAccount(Account account) {
        allAccounts.add(account);
        allUsernames.add(account.getUsername());
        allNicknames.add(account.getNickname());
    }


    public void setAllCards(ArrayList<Card> allCards) {
        this.allCards = allCards;
    }

    public void setActiveDeck(GameDeck activeGameDeck) {
        this.activeGameDeck = activeGameDeck;
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

    public static void removeAccount(Account account) {
        allAccounts.remove(account);
        allNicknames.remove(account.getNickname());
        allUsernames.remove(account.getUsername());
    }



    public static Account getAccountByUsername(String username) {
        for (Account account : allAccounts)
            if (account.getUsername().equals(username))
                return account;
        return null;
    }


}
