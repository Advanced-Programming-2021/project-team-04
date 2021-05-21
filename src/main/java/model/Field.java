package model;

import java.util.ArrayList;

public class Field {
    private ArrayList<Card> graveyard = new ArrayList<>();
    private ArrayList<Card> deckZone = new ArrayList<>();
    private ArrayList<SpellAndTrapCard> trapAndSpell = new ArrayList<>();
    private ArrayList<MonsterCard> monsterCards = new ArrayList<>();
    private ArrayList<Card> hand = new ArrayList<>();
    private SpellAndTrapCard fieldZone;
    private ArrayList<Card> sideDeck = new ArrayList<>();

    public Field(GameDeck gameDeck) {
        deckZone.addAll(gameDeck.getMainDeck());
        sideDeck.addAll(gameDeck.getSideDeck());
    }

    public ArrayList<Scanner> getActiveScanners() {
        ArrayList<Scanner> scanners = new ArrayList<>();
        for (MonsterCard monsterCard : monsterCards)
            if (monsterCard.getName().equals("Scanner"))
                scanners.add((Scanner) monsterCard);
        return scanners;
    }

    public ArrayList<Card> getSideDeck() {
        return sideDeck;
    }

    public void setSideDeck(ArrayList<Card> sideDeck) {
        this.sideDeck = sideDeck;
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

    public SpellAndTrapCard getFieldZone() {
        return fieldZone;
    }

    public void setFieldZone(SpellAndTrapCard fieldZone) {
        this.fieldZone = fieldZone;
    }

    public boolean isTributesLevelSumValid(int sum, int n) {
        if (sum < 0) return false;
        if (sum > 0 && n == 0)
            return false;
        if (sum == 0)
            return true;
        return isTributesLevelSumValid(sum, n - 1) ||
                isTributesLevelSumValid(sum - monsterCards.get(n - 1).getLevel(), n - 1);
    }

    public String showGraveyard() {
        String toPrint = "";
        for (Card card : graveyard)
            toPrint += card.getName() + ":" + card.getDescription() + "\n";
        toPrint = toPrint.substring(0, toPrint.length() - 1);
        return toPrint;
    }

    public ArrayList<MonsterCard> ritualMonsterCards() {
        ArrayList<MonsterCard> rituals = new ArrayList<>();
        for (Card card : hand)
            if (card instanceof MonsterCard) {
                MonsterCard thisMonster = (MonsterCard) card;
                if (thisMonster.cardType.equals("Ritual"))
                    rituals.add(thisMonster);
            }
        return rituals;
    }

    public ArrayList<MonsterCard> ordinaryLowLevelCards() {
        ArrayList<MonsterCard> thisCards = new ArrayList<>();
        for (Card monsterCard : hand)
            if (monsterCard instanceof MonsterCard) {
                MonsterCard monsterCard1 = (MonsterCard) monsterCard;
                if (monsterCard1.getLevel() <= 4 && monsterCard1.cardType.equals("Normal"))
                    thisCards.add(monsterCard1);
            }
        return thisCards;
    }

    public SpellAndTrapCard hasThisCardActivated(String cardName) {
        for (SpellAndTrapCard spellAndTrapCard : trapAndSpell)
            if (spellAndTrapCard.getName().equals(cardName) && spellAndTrapCard.isActive())
                return spellAndTrapCard;
        return null;
    }

    public SpellAndTrapCard hasTrapCard(String cardName) {
        for (SpellAndTrapCard spellAndTrapCard : trapAndSpell)
            if (spellAndTrapCard.getName().equals(cardName))
                return spellAndTrapCard;
        return null;
    }

    public void resetAllCards() {
        ArrayList<Card> allCards = new ArrayList<>();
        allCards.addAll(monsterCards);
        allCards.addAll(trapAndSpell);
        allCards.add(fieldZone);
        for (Card card : allCards)
            if (card != null)
                card.reset();

    }

    public SpellAndTrapCard isSet(String name) {
        for (SpellAndTrapCard spellAndTrapCard : trapAndSpell)
            if (spellAndTrapCard.getName().equals(name)) return spellAndTrapCard;
        return null;
    }

    public void exchangeCards(String fromSide, String fromMain) {
        Card cardFromMain = null;
        Card cardFromSide = null;
        for (Card card : deckZone)
            if (card.getName().equals(fromMain)) {
                cardFromMain = card;
                break;
            }
        for (Card card : sideDeck)
            if (card.getName().equals(fromSide)) {
                cardFromSide = card;
                break;
            }
        deckZone.add(cardFromSide);
        deckZone.remove(cardFromMain);
        sideDeck.add(cardFromMain);
        sideDeck.remove(cardFromSide);
    }
}
