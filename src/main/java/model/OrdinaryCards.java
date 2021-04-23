package model;

public class OrdinaryCards extends MonsterCard {
    public OrdinaryCards (String name, int level, int classAttackPower, int classDefensePower,
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

}
