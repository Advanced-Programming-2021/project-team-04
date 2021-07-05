package yugioh.model.cards.specialcards;


import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;


public class SwordOfDarkDestruction extends SpellAndTrapCard {


    private MonsterCard equippedMonster = null;


    public SwordOfDarkDestruction() {
        super();
    }


    public void setEquippedMonster(MonsterCard equippedMonster) {
        this.equippedMonster = equippedMonster;
        equipMonster();
    }


    public void equipMonster() {
        if (equippedMonster.getMonsterType().equals("Fiend") || equippedMonster.getMonsterType().equals("Spellcaster")) {
            equippedMonster.setThisCardAttackPower(equippedMonster.getThisCardAttackPower() + 400);
            equippedMonster.setThisCardDefensePower(equippedMonster.getThisCardDefensePower() - 200);
        }
    }


    @Override
    public void reset() {
        if (equippedMonster != null) equippedMonster.reset();
        equippedMonster = null;
        isActive = false;
    }
}