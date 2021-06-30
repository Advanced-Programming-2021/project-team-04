package yugioh.model.cards.specialcards;


import yugioh.controller.DuelController;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;


import java.util.ArrayList;


public class MessengerOfPeace extends SpellAndTrapCard {
    ArrayList<MonsterCard> deactivatedCards = new ArrayList<>();
    public MessengerOfPeace() {
        super();
        setName("Messenger of peace");
        isSpell = true;
        property = "Continuous";
        setAllowedNumber(3);
        setPrice(4000);
        setDescription();
    }
    private void setDescription() {
        description = "Monsters with 1500 or more ATK cannot declare an attack." +
                " Once per turn, during your Standby Phase, pay 100 LP or destroy this card.";
    }


    public void deactivateCards() {
        ArrayList<MonsterCard> cards = new ArrayList<>();
        cards.addAll(DuelController.getInstance().getGame().getCurrentPlayer().getField().getMonsterCards());
        cards.addAll(DuelController.getInstance().getGame().getTheOtherPlayer().getField().getMonsterCards());
        for (MonsterCard monsterCard : cards)
            if (monsterCard.getClassAttackPower() >= 1500 && monsterCard.isAbleToAttack()) {
                monsterCard.setAbleToAttack(false);
                deactivatedCards.add(monsterCard);
            }
    }


    public void reset() {
        for (MonsterCard monsterCard : deactivatedCards)
            monsterCard.setAbleToAttack(true);
        deactivatedCards = new ArrayList<>();
    }


}