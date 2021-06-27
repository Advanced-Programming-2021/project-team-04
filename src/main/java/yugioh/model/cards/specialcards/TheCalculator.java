package yugioh.model.cards.specialcards;

import yugioh.model.Duelist;
import yugioh.model.MonsterCardModeInField;
import yugioh.model.cards.MonsterCard;

public class TheCalculator extends MonsterCard {
    public TheCalculator() {
        super();
        setName("The Calculator");
        setLevel(2);
        setClassAttackPower(0);
        setClassDefensePower(0);
        setThisCardDefensePower(0);
        setMonsterType("Thunder");
        setCardType("Effect");
        setPrice(8000);
        setDescription();
    }
    private void setDescription() {
        this.description = "The ATK of this card is the combined Levels of all face-up monsters you control x 300.";
    }

    @Override
    public int getThisCardAttackPower() {
        int amount = 0;
        Duelist player = this.getOwner();
        for (MonsterCard monsterCard : player.getField().getMonsterCards())
            if (monsterCard.getMonsterCardModeInField().equals(MonsterCardModeInField.ATTACK_FACE_UP)
            || monsterCard.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_UP))
               amount += monsterCard.getLevel();
        setThisCardAttackPower(amount * 300);
        return thisCardAttackPower;
    }
}
