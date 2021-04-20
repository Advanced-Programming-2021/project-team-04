package model;

import controller.DuelController;

import java.util.ArrayList;

public class HeraldOfCreation extends MonsterCard {
    public HeraldOfCreation() {
        super();
        setName("Herald of Creation");
        setLevel(4);
        setMonsterCardTypes(MonsterCardTypes.SPELLCASTER);
        setClassAttackPower(1800);
        setClassDefensePower(600);
        setThisCardAttackPower(1800);
        setThisCardDefensePower(600);
        setPrice(2700);
        setDescription();
    }

    private void setDescription() {
        this.description = "Once per turn: You can discard 1 card, then target 1 Level 7 or higher monster in your Graveyard; add that target to your hand.";
    }

    public void specialMethod() {
        Game game = DuelController.getInstance().getGame();
        if (!game.getCardsWhichAttacked().contains(this)) {
            game.getCurrentPlayer().getField().getHand().remove(DuelController.getInstance().selectToRemove());
            game.getCurrentPlayer().getField().getHand().add(DuelController.getInstance().selectFromGraveYard());
        }

    }
}
