package model;

import controller.DuelController;

public class Scanner extends MonsterCard {
    public Scanner() {
        super();
        reset();
    }
    private void setDescription() {
        this.description = "Once per turn, you can select 1 of your opponent's monsters that is removed from play." +
                " Until the End Phase, this card's name is treated as the selected monster's name, " +
                "and this card has the same Attribute, Level, ATK, and DEF as the selected monster. " +
                "If this card is removed from the field while this effect is applied, remove it from play.";
    }
    public void specialMethod() {
        MonsterCard monsterCard = DuelController.getInstance().forScanner();
        //TODO
    }
    public void reset() {
        setName("Scanner");
        setLevel(1);
        setMonsterCardEffectTypes(MonsterCardEffectTypes.TRIGGER);
        setMonsterCardTypes(MonsterCardTypes.MACHINE);
        setPrice(8000);
        setClassAttackPower(0);
        setClassDefensePower(0);
        setDescription();
    }
}
