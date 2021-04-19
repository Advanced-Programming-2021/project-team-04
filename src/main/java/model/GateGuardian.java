package model;

public class GateGuardian extends MonsterCard {
    public GateGuardian() {
        setName("Gate Guardian");
        setLevel(11);
        setMonsterCardEffectTypes(MonsterCardEffectTypes.IGNITION);
        setMonsterCardTypes(MonsterCardTypes.WARRIOR);
        setClassAttackPower(3750);
        setClassDefensePower(3400);
        setThisCardAttackPower(3750);
        setThisCardDefensePower(3400);
        setDescription();
        setPrice(20000);
    }
    private void setDescription() {
        this.description = "Cannot be Normal Summoned/Set. Must first be Special Summoned " +
                "(from your hand) by Tributing 1 \"Sanga of the Thunder\", \"Kazejin\", and \"Suijin\".";
    }
}
