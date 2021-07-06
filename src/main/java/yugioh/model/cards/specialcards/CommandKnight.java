package yugioh.model.cards.specialcards;


import yugioh.controller.DuelController;
import yugioh.model.Account;
import yugioh.model.MonsterCardModeInField;
import yugioh.model.cards.MonsterCard;


import java.util.stream.Stream;


public class CommandKnight extends MonsterCard {


    public CommandKnight() {
        super();
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