package model;

import controller.DuelController;

public class MirageDragon extends MonsterCard {
    public MirageDragon() {
        setName("Mirage Dragon");
        setLevel(4);
        setMonsterCardTypes(MonsterCardTypes.DRAGON);
        setMonsterCardEffectTypes(MonsterCardEffectTypes.CONTINUOUS);
        setClassAttackPower(1600);
        setClassDefensePower(600);
        setThisCardAttackPower(1600);
        setThisCardDefensePower(600);
        setPrice(2500);
        setDescription();
    }

    private void setDescription() {
        this.description = "Your opponent cannot activate Trap Cards during the Battle Phase.";
    }

    public void specialMethod() {
        SpellAndTrapCard trapCard = DuelController.getInstance().chosenTrapCard();
        Account opponent = DuelController.getInstance().getGame().getTheOtherPlayer();
        if (this.Owner.equals(opponent))
            if (this.monsterCardModeInField.equals(MonsterCardModeInField.ATTACK_FACE_UP)
                    || this.monsterCardModeInField.equals(MonsterCardModeInField.DEFENSE_FACE_UP))
            trapCard.canBeActivated = false;
    }
}
