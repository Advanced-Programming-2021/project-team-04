package model;

import java.util.ArrayList;

public class Account {
    private static ArrayList<Account> allAccounts;
    private static ArrayList<String> allUsernames;
    private static ArrayList<String> allNicknames;
    static {
        allAccounts = new ArrayList<>();
        allNicknames = new ArrayList<>();
        allUsernames = new ArrayList<>();
    }
    private ArrayList<Deck> allDecks = new ArrayList<Deck>();
    private ArrayList<Card> unusedCards = new ArrayList<Card>();
    private Deck activeDeck;
    private String username, password, nickname;
    private int score, money;
    private Field field;
    private int LP;
    private int countOfRoundsWon;

    public Account(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        allAccounts.add(this);
    }

    public void setCountOfRoundsWon(int countOfRoundsWon) {
        this.countOfRoundsWon = countOfRoundsWon;
    }

    public void setLP(int LP) {
        this.LP = LP;
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

    public void setUnusedCards(ArrayList<Card> unusedCards) {
        this.unusedCards = unusedCards;
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

    public int getCountOfRoundsWon() {
        return countOfRoundsWon;
    }

    public int getLP() {
        return LP;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setField(Field field) {
        this.field = field;
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

    public ArrayList<Deck> getAllDecks() {
        return allDecks;
    }

    public ArrayList<Card> getUnusedCards() {
        return unusedCards;
    }

    public Deck getActiveDeck() {
        return activeDeck;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public int getScore() {
        return score;
    }

    public int getMoney() {
        return money;
    }

    public Field getField() {
        return field;
    }

    private Deck getDeckByName(String deckName) {

    }
    private Card getCardByName(String cardName) {

    }
    private void addDeck (Deck deck) {

    }
    private void deleteDeck(Deck deck) {

    }
    private void addCard(Card card) {

    }
    private boolean hasDeck (String deck) {
        return true;
    }
    private boolean hasCard (String card) {
        return true;
    }
    private void activateDeck (String deckName) {

    }
    private void printAllDecks() {

    }
    private boolean hasEnoughMoney(int price) {
        return true;
    }
    private boolean hasActiveDeck() {
        return true;
    }
    private boolean hasEnoughCardInHand(int amount) {
        return true;
    }
    private void deleteField(){

    }
    private void reset(){

    }
    private void reduceLP(int amount) {

    }
    private boolean isPlayerDead(){
        return true;
    }
}
