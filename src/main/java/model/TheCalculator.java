package model;

import controller.DuelController;

public class TheCalculator extends MonsterCard {
    public TheCalculator() {
        super();
        setName("The Calculator");
        setLevel(2);
        setClassAttackPower(0);
        setClassDefensePower(0);
        setThisCardDefensePower(0);
        setMonsterCardEffectTypes(MonsterCardEffectTypes.CONTINUOUS);
        setMonsterCardTypes(MonsterCardTypes.THUNDER);
        setPrice(8000);
        setDescription();
    }
    private void setDescription() {
        this.description = "The ATK of this card is the combined Levels of all face-up monsters you control x 300.";
    }
    public void specialMethod() {
        int count = 0;
        Account player = this.getOwner();
        for (MonsterCard monsterCard : player.getField().getMonsterCards())
            if (monsterCard.getMonsterCardModeInField().equals(MonsterCardModeInField.ATTACK_FACE_UP)
            || monsterCard.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_UP))
               count++;
        setThisCardAttackPower(count * 300);
    }
}
