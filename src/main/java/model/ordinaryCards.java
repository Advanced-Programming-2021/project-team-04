package model;

public class ordinaryCards extends MonsterCard {
    public ordinaryCards (String name, String Id, int level, int classAttackPower, int classDefensePower,
                   MonsterCardEffectTypes monsterCardEffectType, MonsterCardTypes monsterCardType, String description, int price) {
        super();
        setName(name);
        setLevel(level);
        setClassAttackPower(classAttackPower);
        setClassDefensePower(classDefensePower);
        setThisCardAttackPower(classAttackPower);
        setThisCardDefensePower(classDefensePower);
        setMonsterCardEffectTypes(monsterCardEffectType);
        setMonsterCardTypes(monsterCardType);
        setDescription(description);
        setPrice(price);

    }

    private void setDescription(String description) {
        this.description = description;
    }
    public void specialMethod() {

    }
}
