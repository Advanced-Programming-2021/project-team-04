package yugioh.model.cards.specialcards;

import yugioh.controller.DuelController;
import yugioh.model.Account;
import yugioh.model.MonsterCardModeInField;
import yugioh.model.cards.MonsterCard;

import java.util.stream.Stream;

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
        if (this.monsterCardModeInField.equals(MonsterCardModeInField.ATTACK_FACE_UP) ||
                this.monsterCardModeInField.equals(MonsterCardModeInField.DEFENSE_FACE_UP))
            Stream.concat(DuelController.getInstance().getGame().getCurrentPlayer().getField().getMonsterCards().stream(),
                    DuelController.getInstance().getGame().getTheOtherPlayer().getField().getMonsterCards().stream())
                    .forEach(m -> m.changeAttackPower(400));
    }

    private void isRemovable() {
        Account thisPlayer = (Account) DuelController.getInstance().getGame().getCurrentPlayer();
        if (!thisPlayer.getField().getMonsterCards().isEmpty()) {
            if (this.monsterCardModeInField.equals(MonsterCardModeInField.ATTACK_FACE_UP)
                    || this.monsterCardModeInField.equals(MonsterCardModeInField.DEFENSE_FACE_UP))
                this.isAbleToBeRemoved = false;
        } else this.isAbleToBeRemoved = true;
    }

    @Override
    public boolean isAbleToBeRemoved() {
        isRemovable();
        return isAbleToBeRemoved;
    }
}
