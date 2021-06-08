package model.cards.specialcards;

import model.MonsterCardModeInField;
import model.cards.MonsterCard;
import model.cards.SpellAndTrapCard;

public class UnitedWeStand extends SpellAndTrapCard {
    private MonsterCard equippedMonster = null;

    public UnitedWeStand() {
        super();
        setName("United We Stand");
        isSpell = true;
        property = "Equip";
        setDescription();
        isLimited = false;
        setPrice(4300);
    }

    private void setDescription() {
        description = "The equipped monster gains 800 ATK/DEF for each face-up monster you control.";
    }

    public void setEquippedMonster(MonsterCard equippedMonster) {
        this.equippedMonster = equippedMonster;
        equipMonster();
    }

    public void equipMonster() {
        equippedMonster.setThisCardAttackPower(equippedMonster.getThisCardAttackPower() + amountOfAttackToAdd());
    }

    private int amountOfAttackToAdd() {
        int counter = 0;
        for (MonsterCard monsterCard : this.getOwner().getField().getMonsterCards())
            if (monsterCard.getMonsterCardModeInField() != MonsterCardModeInField.DEFENSE_FACE_DOWN)
                counter++;
        return counter * 800;
    }

    public void reset() {
        equippedMonster.reset();
        equippedMonster = null;
        isActive = false;
    }

}
