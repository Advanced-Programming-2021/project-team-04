package yugioh.model.cards.specialcards;

import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;

public class SwordOfDarkDestruction extends SpellAndTrapCard {

    private MonsterCard equippedMonster = null;

    public SwordOfDarkDestruction() {
        super();
        setName("Sword of dark destruction");
        isSpell = true;
        property = "Equip";
        setDescription();
        isLimited = false;
        setPrice(4300);
    }

    private void setDescription() {
        description = "A DARK monster equipped with this card increases its ATK by 400 points and decreases its DEF by 200 points.";
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

    public void reset() {
        equippedMonster.reset();
        equippedMonster = null;
        isActive = false;
    }

}
