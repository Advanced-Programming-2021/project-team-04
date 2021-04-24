package model;

import java.util.ArrayList;

public class MessengerOfPeace extends SpellAndTrapCard{
    public MessengerOfPeace() {
        super();
        setName("Messenger of peace");
        isSpell = true;
        type = SpellAndTrapTypes.CONTINUOUS;
        setAllowedNumber(3);
        setPrice(4000);
        setDescription();
    }
    private void setDescription() {
        description = "Monsters with 1500 or more ATK cannot declare an attack." +
                " Once per turn, during your Standby Phase, pay 100 LP or destroy this card.";
    }

    public ArrayList<MonsterCard> deactivateCards(ArrayList<MonsterCard> cards) {
        ArrayList<MonsterCard> deactivatedCards = new ArrayList();
        for (MonsterCard monsterCard : cards)
            if (monsterCard.classAttackPower >= 1500 && monsterCard.canAttack)
            {
                monsterCard.canAttack = false;
                deactivatedCards.add(monsterCard);
            }
        return deactivatedCards;
    }
}
