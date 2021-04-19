package model;

import controller.DuelController;

public class Marshmallon extends MonsterCard {
    public Marshmallon() {
        setName("Marshmallon");
        setLevel(3);
        setClassAttackPower(300);
        setClassDefensePower(500);
        setThisCardAttackPower(300);
        setThisCardDefensePower(500);
        setPrice(700);
        setDescription();
    }

    private void setDescription() {
        this.description = "Cannot be destroyed by battle. After damage calculation, if this card was attacked, " +
                "and was face-down at the start of the Damage Step: The attacking player takes 1000 damage.";
    }

    public void specialMethod() {
        if (this.monsterCardModeInField.equals(MonsterCardModeInField.DEFENSE_FACE_DOWN)){
            DuelController.getInstance().getGame().getTheOtherPlayer().changeLP(-1000);
        }
    }
}
