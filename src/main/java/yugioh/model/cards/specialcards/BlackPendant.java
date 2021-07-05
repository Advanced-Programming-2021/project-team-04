package yugioh.model.cards.specialcards;


import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;


public class BlackPendant extends SpellAndTrapCard {


    private MonsterCard equippedMonster = null;


    public BlackPendant() {
        super();
    }


    public void setEquippedMonster(MonsterCard equippedMonster) {
        this.equippedMonster = equippedMonster;
        equipMonster();
    }


    public void equipMonster() {
        equippedMonster.setThisCardAttackPower(equippedMonster.getThisCardAttackPower() + 500);
    }


    @Override
    public void reset() {
        if (equippedMonster != null) equippedMonster.reset();
        equippedMonster = null;
        isActive = false;
    }


}