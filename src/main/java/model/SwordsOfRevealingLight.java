package model;

import java.util.ArrayList;

public class SwordsOfRevealingLight extends SpellAndTrapCard {
    public int counter = 0;
    public SwordsOfRevealingLight() {
        setName("Swords of Revealing Light");
        isSpell = true;
        property = "Normal";
        isLimited = false;
        price = 2500;
        setDescription();
    }
    private void setDescription() {
        description = "After this card's activation, it remains on the field, but destroy it during the End Phase of your opponent's 3rd turn." +
                " When this card is activated: If your opponent controls a face-down monster," +
                " flip all monsters they control face-up. While this card is face-up on the field, your opponent's monsters cannot declare an attack.";
    }

    public void specialMethod(Duelist opponent) {
        ArrayList<MonsterCard> monsterCards = opponent.getField().getMonsterCards();
        for (MonsterCard monsterCard : monsterCards)
            if (monsterCard.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_DOWN))
                monsterCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
    }


}
