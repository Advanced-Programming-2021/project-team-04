package model;

import controller.DuelController;

public class Suijin extends MonsterCard {


    public Suijin() {
        super();
        setName("Suijin");
        setLevel(7);
        setMonsterCardTypes(MonsterCardTypes.AQUA);
        setMonsterCardEffectTypes(MonsterCardEffectTypes.TRIGGER);
        setClassAttackPower(2500);
        setClassDefensePower(2400);
        setThisCardAttackPower(2500);
        setThisCardDefensePower(2400);
        setPrice(8700);

    }

    public void specialMethod() {
        MonsterCard monsterCard = DuelController.getInstance().getMonsterAttacking();
                monsterCard.setThisCardAttackPower(0);
                this.hasBeenUsed = true;
    }
}
