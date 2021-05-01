package model;

import controller.DuelController;

public class Texchanger extends MonsterCard {
    public Texchanger() {
        super();
        setName("Texchanger");
        setLevel(1);
        setClassAttackPower(100);
        setClassDefensePower(100);
        setThisCardAttackPower(100);
        setThisCardDefensePower(100);
        setMonsterType("Cyberse");
        setPrice(200);
        setDescription();
    }

    private void setDescription() {
        this.description = "Once per turn, when your monster is targeted for an attack: You can negate that attack, " +
                "then Special Summon 1 Cyberse Normal Monster from your hand, Deck, or GY.";
    }

    public void specialMethod() {
        if (!this.hasBeenUsed) {
            MonsterCard monsterCard = DuelController.getInstance().forTexChanger();
            if (monsterCard == null) return;
            Field field = DuelController.getInstance().getGame().getCurrentPlayer().getField();
            DuelController.getInstance().chooseMonsterMode(monsterCard);
            field.getMonsterCards().add(monsterCard);
        }
    }
}
