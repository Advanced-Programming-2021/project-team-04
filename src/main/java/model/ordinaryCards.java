package model;

public class ordinaryCards extends MonsterCard {
    ordinaryCards (String name, String Id, int level, int classAttackPower, int classDefensePower,
                   MonsterCardEffectTypes monsterCardEffectType, MonsterCardTypes monsterCardType, String description) {
        setName(name);
        setId(Id);
        setLevel(level);
        setClassAttackPower(classAttackPower);
        setClassDefensePower(classDefensePower);
        setThisCardAttackPower(classAttackPower);
        setThisCardDefensePower(classDefensePower);
        setMonsterCardEffectTypes(monsterCardEffectType);
        setMonsterCardTypes(monsterCardType);
        setDescription(description);

    }

    private void setDescription(String description) {
        this.description = description;
    }

}
