package model;

import controller.DuelController;

import java.util.ArrayList;

public abstract class Duelist {

    protected ArrayList<Deck> allDecks = new ArrayList<Deck>();
    protected ArrayList<Card> allCards = new ArrayList<Card>();
    protected Deck activeDeck;
    protected String username, nickname;
    protected Field field;
    protected int LP, countForRPS;
    protected int countOfRoundsWon; //TODO should reset this
    protected boolean canDraw = true; //TODO should reset this
    protected boolean canPlayerAttack = true;

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

    public int getCountOfRoundsWon() {
        return countOfRoundsWon;
    }

    public int getLP() {
        return LP;
    }

    public void setField(Field field) {
        this.field = field;
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

    public String getNickname() {
        return nickname;
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

    private boolean hasActiveDeck() {
        return activeDeck != null;
    }

    private boolean hasEnoughCardInHand(int amount) { //TODO check while writing the code
        return true;
    }

    private void deleteField() {
        field = null;
    }

    public void reset() {
        setLP(8000);
        deleteField();
        countForRPS = 0;
    }

    public void changeLP(int amount) {
        this.LP += amount;
        if (this.LP <= 0) DuelController.getInstance().getGame().finishGame(this);
    }

}
