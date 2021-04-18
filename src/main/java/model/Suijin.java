package model;

public class Suijin extends MonsterCard {
    private boolean canBeUsed = true;
    public Suijin() {
        setName("Suijin");
        setId("LCJW-EN226");
        setLevel(7);
        setMonsterCardTypes(MonsterCardTypes.AQUA);
        setMonsterCardEffectTypes(MonsterCardEffectTypes.TRIGGER);
        setClassAttackPower(2500);
        setClassDefensePower(2400);
        setThisCardAttackPower(2500);
        setThisCardDefensePower(2400);
        setPrice(8700);

    }

    public void specialMethod(MonsterCard monsterCard) {
        if (canBeUsed) {
            monsterCard.setThisCardAttackPower(0);
            canBeUsed = false;
        }
    }
}
