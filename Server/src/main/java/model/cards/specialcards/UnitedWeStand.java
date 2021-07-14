package model.cards.specialcards;


import model.cards.MonsterCard;
import model.cards.MonsterCardModeInField;
import model.cards.SpellAndTrapCard;


public class UnitedWeStand extends SpellAndTrapCard {

    private MonsterCard equippedMonster = null;

    public UnitedWeStand() {
        super();
    }


    public void setEquippedMonster(MonsterCard equippedMonster) {
        this.equippedMonster = equippedMonster;
        equipMonster();
    }


    public void equipMonster() {
        equippedMonster.setThisCardAttackPower(equippedMonster.getThisCardAttackPower() + amountOfAttackToAdd());
    }


    private int amountOfAttackToAdd() {
        return (int) (this.getOwner().getField().getMonsterCards().stream()
                .filter(m -> m.getMonsterCardModeInField() != MonsterCardModeInField.DEFENSE_FACE_DOWN).count() * 800);
    }


    @Override
    public void reset() {
        if (equippedMonster != null) equippedMonster.reset();
        equippedMonster = null;
        isActive = false;
    }
}