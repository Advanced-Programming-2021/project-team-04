package yugioh.model.cards.specialcards;

import yugioh.model.cards.MonsterCard;

public class Suijin extends MonsterCard {

    private boolean hasBeenUsedInGeneral = false;

    public Suijin() {
        super();
        setName("Suijin");
        setLevel(7);
        setMonsterType("Aqua");
        setCardType("Effect");
        setClassAttackPower(2500);
        setClassDefensePower(2400);
        setThisCardAttackPower(2500);
        setThisCardDefensePower(2400);
        setPrice(8700);
        setDescription();
    }
    private void setDescription() {
        this.description = "During damage calculation in your opponent's turn, if this card is being attacked:" +
                " You can target the attacking monster;" +
                " make that target's ATK 0 during damage calculation only (this is a Quick Effect). " +
                "This effect can only be used once while this card is face-up on the field.";
    }
    public void specialMethod(MonsterCard monsterCard) {
        if (!this.hasBeenUsedInGeneral) {
            monsterCard.setThisCardAttackPower(0);
            this.hasBeenUsedInGeneral = true;
        }
    }
}
