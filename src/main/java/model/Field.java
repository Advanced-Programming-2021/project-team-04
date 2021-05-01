package model;

import java.util.ArrayList;
import java.util.Collections;

public class Field {
    private ArrayList<Card> graveyard = new ArrayList<>();
    private ArrayList<Card> deckZone = new ArrayList<>();
    private ArrayList<SpellAndTrapCard> trapAndSpell = new ArrayList<>();
    private ArrayList<MonsterCard> monsterCards = new ArrayList<>();
    private ArrayList<Card> hand = new ArrayList<>();
    private SpellAndTrapCard fieldZone;

    public Field() {

    }

    public ArrayList<Card> getGraveyard() {
        return graveyard;
    }

    public void setGraveyard(ArrayList<Card> graveyard) {
        this.graveyard = graveyard;
    }

    public ArrayList<Card> getDeckZone() {
        return deckZone;
    }

    public void setDeckZone(ArrayList<Card> deckZone) {
        this.deckZone = deckZone;
    }

    public ArrayList<SpellAndTrapCard> getTrapAndSpell() {
        return trapAndSpell;
    }

    public void setTrapAndSpell(ArrayList<SpellAndTrapCard> trapAndSpell) {
        this.trapAndSpell = trapAndSpell;
    }

    public ArrayList<MonsterCard> getMonsterCards() {
        return monsterCards;
    }

    public void setMonsterCards(ArrayList<MonsterCard> monsterCards) {
        this.monsterCards = monsterCards;
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

    public void setFieldZone(SpellAndTrapCard fieldZone) {
        this.fieldZone = fieldZone;
    }

    public void shuffleDeck() {
        Collections.shuffle(deckZone);
    }

    public void removeCardFromHand(Card card) {
        hand.remove(card);
    }

    public void addCardToGraveyard(Card card) {
        graveyard.add(card);
    }

    public void removeCardFromGraveyard(Card card) {
        graveyard.remove(card);
    }

    public void addCardToMonsterZone(MonsterCard card) {
        monsterCards.add((card));
    }

    public void addCardToSpellZone(SpellAndTrapCard card) {
        trapAndSpell.add(card);
    }

    public void setCardToFieldZone(SpellAndTrapCard card) {
        fieldZone = card;
    }

    public boolean isMonsterZoneFull() {
        return monsterCards.size() == 5;
    }

    public boolean isSpellZoneFull() {
        return trapAndSpell.size() == 5;
    }

    public boolean isCardInHand(Card card) {
        return hand.contains(card);
    }

    public boolean isCardInMonsterZone(MonsterCard card) {
        return monsterCards.contains(card);
    }

    public boolean hasQuickSpellOrTrap() {
        for (SpellAndTrapCard card : trapAndSpell)
            if (card.getProperty().equals("")) //TODO
                return true;
        return false;
    }

    public boolean isTributesLevelSumValid(int sum, int n, ArrayList<MonsterCard> monsterCards) {
        if (sum > 0 && n == 0)
            return false;
        if (sum == 0)
            return true;
        return isTributesLevelSumValid(sum, n - 1, monsterCards) ||
                isTributesLevelSumValid(sum - monsterCards.get(n - 1).getLevel(), n - 1, monsterCards);
    }

    public String showGraveyard() {
        String toPrint = "";
        for (Card card : graveyard)
            toPrint += card.getName() + ":" + card.getDescription() + "\n";
        toPrint = toPrint.substring(0, toPrint.length() - 2);
        return toPrint;
    }

}
