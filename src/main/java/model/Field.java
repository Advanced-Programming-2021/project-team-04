package model;

import lombok.Getter;
import lombok.Setter;
import model.cards.Card;
import model.cards.MonsterCard;
import model.cards.SpellAndTrapCard;
import model.cards.specialcards.Scanner;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class Field {

    private ArrayList<Card> graveyard = new ArrayList<>();
    private ArrayList<Card> deckZone = new ArrayList<>();
    private ArrayList<SpellAndTrapCard> spellAndTrapCards = new ArrayList<>();
    private ArrayList<MonsterCard> monsterCards = new ArrayList<>();
    private ArrayList<Card> hand = new ArrayList<>();
    private SpellAndTrapCard fieldZone;
    private ArrayList<Card> sideDeck = new ArrayList<>();

    public Field(GameDeck gameDeck) {
        deckZone.addAll(gameDeck.getMainDeck());
        sideDeck.addAll(gameDeck.getSideDeck());
    }

    public ArrayList<Scanner> getActiveScanners() {
        return (ArrayList<Scanner>) monsterCards.stream().filter(m -> m.getName().equals("Scanner")).map(m -> (Scanner) m).collect(Collectors.toList());
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
        var toPrint = new StringBuilder();
        graveyard.forEach(c -> toPrint.append(c.getName()).append(": ").append(c.getDescription()).append("\n"));
//        toPrint.setLength(toPrint.length() - 1);
        return toPrint.toString();
    }

    public ArrayList<MonsterCard> ritualMonsterCards() {
        return (ArrayList<MonsterCard>) hand.stream().filter(c -> c instanceof MonsterCard).map(c -> (MonsterCard) c)
                .filter(m -> m.getMonsterType().equals("Ritual")).collect(Collectors.toList());
    }

    public ArrayList<MonsterCard> ordinaryLowLevelCards() {
        return (ArrayList<MonsterCard>) hand.stream().filter(c -> c instanceof MonsterCard).map(c -> (MonsterCard) c)
                .filter(m -> m.getLevel() <= 4 && m.getCardType().equals("Normal")).collect(Collectors.toList());
    }

    public SpellAndTrapCard getThisActivatedCard(String cardName) {
        return spellAndTrapCards.stream().filter(c -> c.isActive() && c.getName().equals(cardName)).findAny().orElse(null);
    }

    public SpellAndTrapCard getSpellAndTrapCard(String cardName) {
        return spellAndTrapCards.stream().filter(c -> c.getName().equals(cardName)).findAny().orElse(null);
    }

    public void resetAllCards() {
        monsterCards.stream().filter(Objects::nonNull).forEach(MonsterCard::reset);
        Stream.concat(spellAndTrapCards.stream(), Stream.of(fieldZone)).filter(Objects::nonNull).forEach(SpellAndTrapCard::reset);
//        for (MonsterCard monsterCard : monsterCards)
//            monsterCard.setHasBeenUsedInThisTurn(false);
//        for (SpellAndTrapCard spellAndTrapCard : spellAndTrapCards)
//            spellAndTrapCard.setHasBeenUsedInThisTurn(false);
//        for (MonsterCard monsterCard : monsterCards)
//            System.out.println(monsterCard.isHasBeenSetOrSummoned());
    }

    public SpellAndTrapCard getSetSpellAndTrapCard(String cardName) {
        return spellAndTrapCards.stream().filter(c -> c.getName().equals(cardName)).findAny().orElse(null);
    }

    public void exchangeCards(String fromSide, String fromMain) {
        var cardFromMain = deckZone.stream().filter(c -> c.getName().equals(fromMain)).findAny().orElse(null);
        var cardFromSide = sideDeck.stream().filter(c -> c.getName().equals(fromSide)).findAny().orElse(null);
        if (cardFromMain == null || cardFromSide == null) return;
        deckZone.add(cardFromSide);
        deckZone.remove(cardFromMain);
        sideDeck.add(cardFromMain);
        sideDeck.remove(cardFromSide);
    }
}