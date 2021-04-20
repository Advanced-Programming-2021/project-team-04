package model;

import controller.DuelController;

public class YomiShip extends MonsterCard {
    public YomiShip() {
        super();
        setName("Yomi Ship");
        setLevel(3);
        setClassAttackPower(800);
        setClassDefensePower(1400);
        setThisCardAttackPower(800);
        setThisCardDefensePower(1400);
        setMonsterCardEffectTypes(MonsterCardEffectTypes.TRIGGER);
        setMonsterCardTypes(MonsterCardTypes.AQUA);
        setPrice(1700);
        setDescription();
    }

    private void setDescription() {
        this.description = "If this card is destroyed by battle " +
                "and sent to the GY: Destroy the monster that destroyed this card.";
    }

    public void specialMethod () {
        MonsterCard monsterCard = DuelController.getInstance().getMonsterAttacking();
        Account opponent = DuelController.getInstance().getGame().getTheOtherPlayer();
        if (this.isAttacked) {
            opponent.getField().addCardToGraveyard(monsterCard);
            opponent.getField().removeCardFromHand(monsterCard);
        }
    }
}
