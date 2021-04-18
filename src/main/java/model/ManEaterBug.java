package model;

public class ManEaterBug extends MonsterCard {
    public ManEaterBug() {
        setName("Man-Eater Bug");
        setLevel(2);
        setMonsterCardTypes(MonsterCardTypes.INSECT);
        setMonsterCardEffectTypes(MonsterCardEffectTypes.FLIP);
        setClassAttackPower(450);
        setClassDefensePower(600);
        setThisCardAttackPower(450);
        setThisCardDefensePower(600);
        setDescription();
    }
    private void setDescription() {
        this.description = "FLIP: Target 1 monster on the field; destroy that target.";
    }

    public void specialMethod (MonsterCard monsterCard) {

    }
}
