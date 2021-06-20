package yugioh.model;

import yugioh.controller.DuelController;
import yugioh.controller.ImportAndExport;
import lombok.Getter;
import lombok.Setter;
import yugioh.model.cards.Card;
import yugioh.model.cards.MonsterCard;

import java.util.ArrayList;
import java.util.Comparator;

@Getter
@Setter
public class AI extends Duelist {

    private static AI singleInstance = null;

    private AI() {
        username = "AI";
        setAllPlayerDecks(ImportAndExport.getInstance().readAllDecks("src/main/resources/decks/"));
    }

    public static AI getInstance() {
        if (singleInstance == null)
            singleInstance = new AI();
        return singleInstance;
    }

    private ArrayList<MonsterCard> findTributes;

    public void attack(Duelist opponent) {
        MonsterCard strongestAIMonster = getStrongestMonsterCardInZone();
        if (strongestAIMonster == null) return;
        DuelController.getInstance().selectCard(true,
                CardStatusInField.MONSTER_FIELD, getField().getMonsterCards().indexOf(strongestAIMonster));
        MonsterCard opponentsWeakestAttackCard = getOpponentsWeakestAttackCard(opponent);
        MonsterCard opponentsWeakestDefenseCard = getOpponentsWeakestDefenseCard(opponent);
        if (opponentsWeakestAttackCard == null) {
            if (opponentsWeakestDefenseCard == null)
                DuelController.getInstance().directAttack();
            else if (strongestAIMonster.getThisCardAttackPower() > opponentsWeakestDefenseCard.getThisCardDefensePower())
                DuelController.getInstance().attack(opponent.getField().getMonsterCards().indexOf(opponentsWeakestDefenseCard));
        }
        else if (strongestAIMonster.getThisCardAttackPower() > opponentsWeakestAttackCard.getThisCardAttackPower())
            DuelController.getInstance().attack(opponent.getField().getMonsterCards().indexOf(opponentsWeakestAttackCard));
        else if (strongestAIMonster.getThisCardAttackPower() > opponentsWeakestDefenseCard.getThisCardDefensePower())
            DuelController.getInstance().attack(opponent.getField().getMonsterCards().indexOf(opponentsWeakestDefenseCard));
        else return;
        attack(opponent);
    }

    public MonsterCard getStrongestMonsterCardInZone() {
        return getField().getMonsterCards().stream()
                .filter(m -> m.getMonsterCardModeInField().equals(MonsterCardModeInField.ATTACK_FACE_UP))
                .filter(MonsterCard::isAbleToAttack).max(Comparator.comparing(MonsterCard::getThisCardAttackPower)).orElse(null);
    }

    public MonsterCard getOpponentsWeakestAttackCard(Duelist opponent) {
        return opponent.getField().getMonsterCards().stream()
                .filter(m -> m.getMonsterCardModeInField().equals(MonsterCardModeInField.ATTACK_FACE_UP))
                .min(Comparator.comparing(MonsterCard::getThisCardAttackPower)).orElse(null);
    }

    public MonsterCard getOpponentsWeakestDefenseCard(Duelist opponent) {
        return opponent.getField().getMonsterCards().stream()
                .filter(m -> m.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_DOWN) ||
                        m.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_UP))
                .min(Comparator.comparing(MonsterCard::getThisCardDefensePower)).orElse(null);
    }

    public MonsterCard getStrongestMonsterCardInHand() {
        ArrayList<MonsterCard> monstersInHand = new ArrayList<>();
        for (Card card : getField().getHand())
            if (card instanceof MonsterCard) monstersInHand.add((MonsterCard) card);
        return monstersInHand.stream().max(Comparator.comparing(MonsterCard::getThisCardAttackPower)).orElse(null);
    }

    public void summonMonster() {
        MonsterCard strongestMonsterCardInHand = getStrongestMonsterCardInHand();
        if (strongestMonsterCardInHand == null)
            DuelController.getInstance().summon();
    }

    public void findTributesToRemove() {

    }

}