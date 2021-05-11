package model;

import java.util.ArrayList;
import java.util.Comparator;

public class AI extends Duelist {

    private ArrayList<MonsterCard> findTributes;

    public void attack() {

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
    public void attackDirectly() {

    }
    public void findTributesToRemove() {

    }

}
