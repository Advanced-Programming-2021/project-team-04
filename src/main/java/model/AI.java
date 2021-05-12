package model;

import controller.DuelController;
import java.util.ArrayList;
import java.util.Comparator;

public class AI extends Duelist {

    private ArrayList<MonsterCard> findTributes;

    public void attack(Duelist opponent) {
        MonsterCard strongestAIMonster = getStrongestMonster(getField().getMonsterCards());
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

    public MonsterCard getStrongestMonster(ArrayList<MonsterCard> monsterCards) {
        return monsterCards.stream()
                .filter(m -> m.getMonsterCardModeInField().equals(MonsterCardModeInField.ATTACK_FACE_UP))
                .filter(MonsterCard::canAttack).max(Comparator.comparing(m -> m.thisCardAttackPower)).orElse(null);
    }

    public MonsterCard getOpponentsWeakestAttackCard(Duelist opponent) {
        return opponent.getField().getMonsterCards().stream()
                .filter(m -> m.getMonsterCardModeInField().equals(MonsterCardModeInField.ATTACK_FACE_UP))
                .min(Comparator.comparing(m -> m.thisCardAttackPower)).orElse(null);
    }

    public MonsterCard getOpponentsWeakestDefenseCard(Duelist opponent) {
        return opponent.getField().getMonsterCards().stream()
                .filter(m -> m.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_DOWN) ||
                        m.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_UP))
                .min(Comparator.comparing(m -> m.thisCardDefensePower)).orElse(null);
    }

    public void summonMonster() {

    }
    public void findTributesToRemove() {

    }

}
