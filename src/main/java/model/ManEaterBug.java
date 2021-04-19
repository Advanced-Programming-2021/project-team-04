package model;

import controller.DuelController;

public class ManEaterBug extends MonsterCard {
    private boolean hasDestroyedACard = false;

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

    public void specialMethod () {
        if (!hasDestroyedACard) {
            MonsterCard monsterCard = DuelController.getInstance().forManEaterBug();
            Field field = DuelController.getInstance().getGame().getTheOtherPlayer().getField();
            field.getMonsterCards().remove(monsterCard);
            field.addCardToGraveyard(monsterCard);
            hasDestroyedACard = true;
        }
    }
}
