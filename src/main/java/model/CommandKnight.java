package model;

import java.util.ArrayList;

public class CommandKnight extends MonsterCard {
    public CommandKnight() {
        setName("Command Knight");
        setLevel(4);
        setMonsterCardTypes(MonsterCardTypes.WARRIOR);
        setMonsterCardEffectTypes(MonsterCardEffectTypes.CONTINUOUS);
        setId("LDK2-ENJ20");
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

    public void specialMethod(ArrayList<MonsterCard> monsterCards) {
        if (this.monsterCardModeInField.equals(MonsterCardModeInField.ATTACK_FACE_UP) ||
                this.monsterCardModeInField.equals(MonsterCardModeInField.DEFENSE_FACE_UP))
            for (MonsterCard monsterCard : monsterCards)
                monsterCard.changeAttackPower(400);
    }

    public void isRemovable(Account thisPlayer) {
        if (!thisPlayer.getField().getMonsterCards().isEmpty())
            for (MonsterCard monsterCard : thisPlayer.getField().getMonsterCards())
                if (monsterCard.getMonsterCardModeInField().equals(MonsterCardModeInField.ATTACK_FACE_UP)
                        || monsterCard.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_UP))
                    this.canBeRemoved = false;
    }
}
