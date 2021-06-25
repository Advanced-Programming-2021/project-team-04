package yugioh.model.cards.specialcards;

import yugioh.model.MonsterCardModeInField;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;

public class MagnumShield extends SpellAndTrapCard {

    private MonsterCard equippedMonster = null;

    public MagnumShield() {
        super();
        setName("Magnum Shield");
        isSpell = true;
        property = "Equip";
        setDescription();
        isLimited = false;
        setPrice(4300);
    }

    private void setDescription() {
        description = "Equip only to a Warrior-Type monster. Apply this effect, depending on its battle position." +
                "\n-Attack Position: It gains ATK equal to its original DEF." +
                "\n-Defense Position: It gains DEF equal to its original ATK.";
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

    public void reset() {
        if (equippedMonster != null) equippedMonster.reset();
        equippedMonster = null;
        isActive = false;
    }
}
