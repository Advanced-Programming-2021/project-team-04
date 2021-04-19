package model;

public class MonsterCard extends Card {
    protected int classAttackPower, classDefensePower;
    protected int thisCardAttackPower, thisCardDefensePower;
    protected int level;
    protected MonsterCardTypes monsterCardTypes;
    protected MonsterCardEffectTypes monsterCardEffectTypes;
    protected MonsterCardModeInField monsterCardModeInField;
    protected boolean canBeRemoved = true;
    public MonsterCard() {
        super();
    }
    public MonsterCardEffectTypes getMonsterCardEffectTypes() {
        return monsterCardEffectTypes;
    }

    public void setMonsterCardEffectTypes(MonsterCardEffectTypes monsterCardEffectTypes) {
        this.monsterCardEffectTypes = monsterCardEffectTypes;
    }

    public MonsterCardModeInField getMonsterCardModeInField() {
        return monsterCardModeInField;
    }

    public void setMonsterCardModeInField(MonsterCardModeInField monsterCardModeInField) {
        this.monsterCardModeInField = monsterCardModeInField;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public MonsterCardTypes getMonsterCardTypes() {
        return monsterCardTypes;
    }

    public void setMonsterCardTypes(MonsterCardTypes monsterCardTypes) {
        this.monsterCardTypes = monsterCardTypes;
    }

    public int getClassAttackPower() {
        return classAttackPower;
    }

    public void setClassAttackPower(int classAttackPower) {
        this.classAttackPower = classAttackPower;
    }

    public int getClassDefensePower() {
        return classDefensePower;
    }

    public void setClassDefensePower(int classDefensePower) {
        this.classDefensePower = classDefensePower;
    }

    public int getThisCardAttackPower() {
        return thisCardAttackPower;
    }

    public void setThisCardAttackPower(int thisCardAttackPower) {
        this.thisCardAttackPower = thisCardAttackPower;
    }

    public int getThisCardDefensePower() {
        return thisCardDefensePower;
    }

    public void setThisCardDefensePower(int thisCardDefensePower) {
        this.thisCardDefensePower = thisCardDefensePower;
    }

    @Override
    public String toString() {
        return super.toString();
    }
    public void changeAttackPower(int amount) {
        this.thisCardAttackPower += amount;
    }
    public void changeDefensePower(int amount) {
        this.thisCardDefensePower += amount;
    }
}
