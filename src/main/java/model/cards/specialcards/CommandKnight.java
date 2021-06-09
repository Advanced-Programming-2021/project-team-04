package model.cards.specialcards;

import controller.DuelController;
import model.Account;
import model.MonsterCardModeInField;
import model.cards.MonsterCard;

import java.util.ArrayList;

public class CommandKnight extends MonsterCard {

    public CommandKnight() {
        super();
        setName("Command Knight");
        setLevel(4);
        setMonsterType("Warrior");
        setCardType("Effect");
        setClassAttackPower(1000);
        setClassDefensePower(1000);
        setThisCardAttackPower(1000);
        setThisCardDefensePower(1000);
        setPrice(2100);
        setDescription();
    }

    private void setDescription() {
        this.description = "All Warrior-Type monsters you control gain 400 ATK. " +
                "If you control another monster, monsters your opponent " +
                "controls cannot target this card for an attack.";
    }

    public void specialMethod() {
        ArrayList<MonsterCard> monsterCards = DuelController.getInstance().getGame().getCurrentPlayer().getField().getMonsterCards();
        ArrayList<MonsterCard> opponentMonsterCards = DuelController.getInstance().getGame().getTheOtherPlayer().getField().getMonsterCards();
        monsterCards.addAll(opponentMonsterCards);
            if (this.monsterCardModeInField.equals(MonsterCardModeInField.ATTACK_FACE_UP) ||
                    this.monsterCardModeInField.equals(MonsterCardModeInField.DEFENSE_FACE_UP))
                for (int i = 0; i < monsterCards.size(); i++)
                    monsterCards.get(i).changeAttackPower(400);
    }

    private void isRemovable() {
        //TODO is this method ok AI-wise?
        Account thisPlayer = (Account) DuelController.getInstance().getGame().getCurrentPlayer();
        if (!thisPlayer.getField().getMonsterCards().isEmpty()) {
            if (this.monsterCardModeInField.equals(MonsterCardModeInField.ATTACK_FACE_UP)
                    || this.monsterCardModeInField.equals(MonsterCardModeInField.DEFENSE_FACE_UP))
                this.isAbleToBeRemoved = false;
        }
        else this.isAbleToBeRemoved = true;
    }

    @Override
    public boolean isAbleToBeRemoved() {
        isRemovable();
        return isAbleToBeRemoved;
    }
}