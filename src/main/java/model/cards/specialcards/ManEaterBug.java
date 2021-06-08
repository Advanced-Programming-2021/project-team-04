package model.cards.specialcards;

import controller.DuelController;
import model.MonsterCardModeInField;
import model.cards.MonsterCard;

public class ManEaterBug extends MonsterCard {

    public ManEaterBug() {
        super();
        setName("Man-Eater Bug");
        setLevel(2);
        setMonsterType("Insect");
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


    public void setMonsterCardModeInField(MonsterCardModeInField newMode) {
        if (this.monsterCardModeInField == MonsterCardModeInField.DEFENSE_FACE_DOWN &&
                (newMode == MonsterCardModeInField.ATTACK_FACE_UP ||
                        newMode == MonsterCardModeInField.DEFENSE_FACE_UP)) {
            DuelController.getInstance().forManEaterBug();
        }
        this.monsterCardModeInField = newMode;
    }
}
