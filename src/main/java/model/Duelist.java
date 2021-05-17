package model;

import com.google.gson.annotations.Expose;
import controller.DuelController;

import java.util.ArrayList;

public abstract class Duelist {

    @Expose(serialize = true, deserialize = true)
    protected ArrayList<GameDeck> allGameDecks = new ArrayList<GameDeck>();
    @Expose(serialize = true, deserialize = true)
    protected ArrayList<Card> allCards = new ArrayList<Card>();
    protected GameDeck activeGameDeck;
    @Expose(serialize = true, deserialize = true)
    protected String username;
    @Expose(serialize = true, deserialize = true)
    protected String nickname;
    protected Field field;
    protected int LP, countForRPS;
    protected int maxLPofThreeRounds;
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

    public int getMaxLPofThreeRounds() {
        return maxLPofThreeRounds;
    }

    public void setMaxLPofThreeRounds(int LP) {
        maxLPofThreeRounds = LP;
    }

    public void checkMaxLPofThreeRounds() {
        if (maxLPofThreeRounds < LP) maxLPofThreeRounds = LP;
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

    public ArrayList<GameDeck> getAllDecks() {
        return allGameDecks;
    }

    public ArrayList<Card> getAllCards() {
        return allCards;
    }

    public GameDeck getActiveDeck() {
        return activeGameDeck;
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

    public GameDeck getDeckByName(String deckName) {
        for (GameDeck gameDeck : getAllDecks())
            if (gameDeck.getDeckName().equals(deckName))
                return gameDeck;
        return null;
    }

    public Card getCardByName(String cardName) {
        for (Card card : allCards)
            if (card.getName().equals(cardName))
                return card;
        return null;
    }

    public void setAllDecks(ArrayList<GameDeck> allGameDecks) {
        this.allGameDecks = allGameDecks;
    }

    public void addDeck(GameDeck gameDeck) {
        this.getAllDecks().add(gameDeck);
    }

    public void deleteDeck(GameDeck gameDeck) {
        this.getAllDecks().remove(gameDeck);
    }

    public void addCard(Card card) {
        this.getAllCards().add(card);
        card.setOwner(this);
    }

    public boolean hasDeck(String deck) {
        for (GameDeck thisGameDeck : allGameDecks)
            if (thisGameDeck.getDeckName().equals(deck))
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
        for (GameDeck thisGameDeck : allGameDecks)
            if (thisGameDeck.getDeckName().equals(deckName))
                activeGameDeck = thisGameDeck;
    }

    private boolean hasActiveDeck() {
        return activeGameDeck != null;
    }

    private boolean hasEnoughCardInHand(int amount) { //TODO check while writing the code
        return true;
    }

    public void deleteField() {
        field = null;
    }

    public void reset() {
        setLP(8000);
        countForRPS = 0;
    }

    public void changeLP(int amount) {
        this.LP += amount;
        if (this.LP <= 0) DuelController.getInstance().getGame().finishGame(this);
    }

}
