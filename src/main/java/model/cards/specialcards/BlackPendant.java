package model.cards.specialcards;

import model.cards.MonsterCard;
import model.cards.SpellAndTrapCard;

public class BlackPendant extends SpellAndTrapCard {

    private MonsterCard equippedMonster = null;

    public BlackPendant() {
        super();
        setName("Black Pendant");
        isSpell = true;
        property = "Equip";
        setDescription();
        isLimited = false;
        setPrice(4300);
    }

    private void setDescription() {
        description = "The equipped monster gains 500 ATK. When this card is sent from the field to the Graveyard: Inflict 500 damage to your opponent.";
    }

    public void setEquippedMonster(MonsterCard equippedMonster) {
        this.equippedMonster = equippedMonster;
        equipMonster();
    }

    public void equipMonster() {
        equippedMonster.setThisCardAttackPower(equippedMonster.getThisCardAttackPower() + 500);

    }

    public void reset() {
        equippedMonster.reset();
        equippedMonster = null;
        isActive = false;
    }

}
