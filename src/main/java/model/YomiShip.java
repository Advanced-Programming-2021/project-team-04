package model;

public class YomiShip extends MonsterCard {
    public YomiShip() {
        setName("Yomi Ship");
        setLevel(3);
        setId("SBAD-EN024");
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

    public void specialMethod (MonsterCard monsterCard, Account opponent) {
        opponent.getField().addCardToGraveyard(monsterCard);
        opponent.getField().removeCardFromHand(monsterCard);
    }
}
