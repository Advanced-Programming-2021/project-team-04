package model;

public class MonsterCard extends Card {
    protected int classAttackPower, classDefensePower;
    protected int thisCardAttackPower, thisCardDefensePower;
    protected int level;
    protected String monsterType;
    protected String cardType;
    protected MonsterCardModeInField monsterCardModeInField;
    protected boolean canBeRemoved = true;
    protected boolean canAttack = true;

    public MonsterCard() {
        super();
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public boolean canAttack() {
        return canAttack;
    }

    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    public boolean canBeRemoved() {
        return canBeRemoved;
    }

    public void setCanBeRemoved(boolean canBeRemoved) {
        this.canBeRemoved = canBeRemoved;
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

    public String getMonsterType() {
        return monsterType;
    }

    public void setMonsterType(String monsterType) {
        this.monsterType = monsterType;
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

    public void changeAttackPower(int amount) {
        this.thisCardAttackPower += amount;
    }

    public void changeDefensePower(int amount) {
        this.thisCardDefensePower += amount;
    }

    public void reset() {
        this.thisCardDefensePower = classDefensePower;
        this.thisCardAttackPower = classAttackPower;
        this.hasBeenUsed = false;
        this.canBeRemoved = true;
        //TODO is it enough?
    }
}
