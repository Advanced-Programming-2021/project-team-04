package model;

import java.util.ArrayList;

public class Field {
    private ArrayList<Card> graveyard;
    private ArrayList<Card> Deck;
    private ArrayList<SpellAndTrapCard> trapAndSpell;
    private ArrayList<MonsterCard> MonsterCards;
    private ArrayList<Card> hand;
    private Card fieldZone;

    public Field() {

    }

    public ArrayList<Card> getGraveyard() {
        return graveyard;
    }

    public void setGraveyard(ArrayList<Card> graveyard) {
        this.graveyard = graveyard;
    }

    public ArrayList<Card> getDeck() {
        return Deck;
    }

    public void setDeck(ArrayList<Card> deck) {
        Deck = deck;
    }

    public ArrayList<SpellAndTrapCard> getTrapAndSpell() {
        return trapAndSpell;
    }

    public void setTrapAndSpell(ArrayList<SpellAndTrapCard> trapAndSpell) {
        this.trapAndSpell = trapAndSpell;
    }

    public ArrayList<MonsterCard> getMonsterCards() {
        return MonsterCards;
    }

    public void setMonsterCards(ArrayList<MonsterCard> monsterCards) {
        MonsterCards = monsterCards;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public Card getFieldZone() {
        return fieldZone;
    }

    public void setFieldZone(Card fieldZone) {
        this.fieldZone = fieldZone;
    }

    public void shuffleDeck() {

    }

    public void addDeck() {

    }

    public boolean fieldHasEnoughCards(Enum cardStatusInField, int number) {
        return false;
    }

    public void finalField() {

    }

    public void removeCardFromHand(Card card) {

    }

    public void addCardToGraveyard(Card card) {

    }

    public void removeCardFromGraveyard(Card card) {

    }

    public void addCardToMonsterZone(Card card) {

    }

    public void addCardToSpellZone(Card card) {

    }

    public void setCardToFieldZone(Card card) {

    }

    public boolean isMonsterZoneFull() {
        return false;
    }

    public boolean isSpellZoneFull() {
        return false;
    }

    public boolean isCardInHand(Card card) {
        return true;
    }

    public boolean isCardInMosterZone(Card card) {
        return true;
    }

    public boolean hasQuickSpellOrTrap() {
        return false;
    }

    public boolean isTributesLevelSumValid(int sum, int n, ArrayList<MonsterCard> monsterCards) {
        return false;
    }

    public void showGraveyard() {

    }
}
