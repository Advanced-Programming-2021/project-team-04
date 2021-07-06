package yugioh.model.cards.specialcards;


import yugioh.model.MonsterCardModeInField;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;


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