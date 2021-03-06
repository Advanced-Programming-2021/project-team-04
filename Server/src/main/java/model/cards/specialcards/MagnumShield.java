package model.cards.specialcards;


import model.cards.MonsterCard;
import model.cards.MonsterCardModeInField;
import model.cards.SpellAndTrapCard;


public class MagnumShield extends SpellAndTrapCard {


    private MonsterCard equippedMonster = null;


    public MagnumShield() {
        super();
    }


    public void setEquippedMonster(MonsterCard equippedMonster) {
        this.equippedMonster = equippedMonster;
        equipMonster();
    }


    public void equipMonster() {
        if (equippedMonster.getMonsterCardModeInField() == MonsterCardModeInField.ATTACK_FACE_UP)
            equippedMonster.setThisCardAttackPower(equippedMonster.getThisCardAttackPower() + equippedMonster.getThisCardDefensePower());
        else
            equippedMonster.setThisCardDefensePower(equippedMonster.getThisCardDefensePower() + equippedMonster.getThisCardAttackPower());
    }


    @Override
    public void reset() {
        if (equippedMonster != null) equippedMonster.reset();
        equippedMonster = null;
        isActive = false;
    }
}