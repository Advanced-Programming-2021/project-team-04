package model;

import javax.swing.*;
import java.util.ArrayList;

public class CommandKnight extends MonsterCard {
    public CommandKnight() {
        super();
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
        String thisDescription = "All Warrior-Type monsters you control gain 400 ATK. " +
                "If you control another monster, monsters your opponent " +
                "controls cannot target this card for an attack.";
        this.description = thisDescription;
    }

    public void specialMethod(ArrayList<MonsterCard> monsterCards) {
        if (this.monsterCardModeInField.equals(MonsterCardModeInField.ATTACK_FACE_UP) ||
                this.monsterCardModeInField.equals(MonsterCardModeInField.DEFENSE_FACE_UP))
            for (MonsterCard monsterCard : monsterCards)
                monsterCard.setThisCardAttackPower(monsterCard.getClassAttackPower() + 400);
    }

    public boolean isRemovable(Account thisPlayer) {
        if (thisPlayer.getField().getMonsterCards().isEmpty())
            return true;
        for (MonsterCard monsterCard : thisPlayer.getField().getMonsterCards())
            if (monsterCard.getMonsterCardModeInField().equals(MonsterCardModeInField.ATTACK_FACE_UP)
            || monsterCard.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_UP))
                return false;
        return true;
    }
}
