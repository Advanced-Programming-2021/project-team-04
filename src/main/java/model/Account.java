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
    private ArrayList<Card> allCards = new ArrayList<Card>();
    private Deck activeDeck;
    private String username, password, nickname;
    private int score, money = 100000, countForRPS;
    private Field field;
    private int LP;
    private int countOfRoundsWon; //TODO should reset this
    private boolean canDraw = true; //TODO should reset this
    private boolean canPlayerAttack = true;

    public Account(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        allAccounts.add(this);
    }

    public boolean canPlayerAttack() {
        return canPlayerAttack;
    }

    public void setCanPlayerAttack(boolean canPlayerAttack) {
        this.canPlayerAttack = canPlayerAttack;
    }

    public boolean canDraw() {
        return canDraw;
    }

    public void setCanDraw(boolean canDraw) {
        this.canDraw = canDraw;
    }

    public int getCountForRPS() {
        return countForRPS;
    }

    public void setCountForRPS(int countForRPS) {
        this.countForRPS = countForRPS;
    }

    public void increaseCountForRPS() {
        this.countForRPS++;
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

    public ArrayList<Card> getAllCards() {
        return allCards;
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

    public boolean hasCardInHand(String cardName) {
        for (Card card : field.getHand())
            if (card.getName().equals(cardName)) return true;
        return false;
    }

    public MonsterCard hasMirageDragon() {
        for (MonsterCard monsterCard : field.getMonsterCards())
            if (monsterCard.getName().equals("Mirage Dragon")) return monsterCard;
        return null;
    }

    public Deck getDeckByName(String deckName) {
        for (Deck deck : getAllDecks())
            if (deck.getDeckName().equals(deckName))
                return deck;
        return null;
    }

    public Card getCardByName(String cardName) {
        for (Card card : allCards)
            if (card.getName().equals(cardName))
                return card;
        return null;
    }

    public void addDeck(Deck deck) {
        this.getAllDecks().add(deck);
    }

    public void deleteDeck(Deck deck) {
        this.getAllDecks().remove(deck);
    }

    public void addCard(Card card) {
        this.getAllCards().add(card);
    }

    public boolean hasDeck(String deck) {
        for (Deck thisDeck : allDecks)
            if (thisDeck.getDeckName().equals(deck))
                return true;
        return false;
    }

    public boolean hasCard(String card) {
        for (Card thisCard : allCards)
            if (thisCard.getName().equals(card))
                return true;
        return false;
    }

    private void activateDeck(String deckName) {
        for (Deck thisDeck : allDecks)
            if (thisDeck.getDeckName().equals(deckName))
                activeDeck = thisDeck;
    }


    private boolean hasEnoughMoney(int price) {
        return this.money >= price;
    }

    private boolean hasActiveDeck() {
        return activeDeck != null;
    }

    private boolean hasEnoughCardInHand(int amount) { //TODO check while writing the code
        return true;
    }

    private void deleteField() {
        field = null;
    }

    private void reset() {
        setLP(8000);
        deleteField();
        //TODO not sure if it's all but duh
    }


    private boolean isPlayerDead() { //TODO this is not enough for game
        return LP == 0;
    }

    public void changeLP(int amount) {
        this.LP += amount;
    }

    public static Account getAccountByUsername(String username) {
        for (Account account : allAccounts)
            if (account.getUsername().equals(username))
                return account;
        return null;
    }

    public static Account getAccountByNickname(String nickName) {
        for (Account account : allAccounts)
            if (account.getNickname().equals(nickName))
                return account;
        return null;
    }

}
