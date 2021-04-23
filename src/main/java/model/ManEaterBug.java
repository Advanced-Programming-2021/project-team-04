package model;

import controller.DuelController;

public class ManEaterBug extends MonsterCard {

    public ManEaterBug() {
        super();
        setName("Man-Eater Bug");
        setLevel(2);
        setMonsterCardTypes(MonsterCardTypes.INSECT);
        setMonsterCardEffectTypes(MonsterCardEffectTypes.FLIP);
        setClassAttackPower(450);
        setClassDefensePower(600);
        setThisCardAttackPower(450);
        setThisCardDefensePower(600);
        setPrice(600);
        setDescription();
    }
    private void setDescription() {
        this.description = "FLIP: Target 1 monster on the field; destroy that target.";
    }
    @Override
    public void setMonsterCardModeInField(MonsterCardModeInField monsterCardModeInField) {
        if (this.monsterCardModeInField.equals(MonsterCardModeInField.DEFENSE_FACE_DOWN))

    }
    }
