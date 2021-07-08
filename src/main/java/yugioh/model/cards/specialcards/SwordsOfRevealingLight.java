package yugioh.model.cards.specialcards;


import yugioh.controller.DuelController;
import yugioh.model.AI;
import yugioh.model.Duelist;
import yugioh.model.cards.MonsterCardModeInField;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;


import java.util.ArrayList;


public class SwordsOfRevealingLight extends SpellAndTrapCard {

    public int counter = 0;

    public SwordsOfRevealingLight() {
        super();
    }

    public void specialMethod(Duelist opponent) {
        if (!(opponent instanceof AI))
            DuelController.getInstance().makeChain(DuelController.getInstance().getGame().getCurrentPlayer(),
                    DuelController.getInstance().getGame().getTheOtherPlayer());
        ArrayList<MonsterCard> monsterCards = opponent.getField().getMonsterCards();
        for (MonsterCard monsterCard : monsterCards)
            if (monsterCard.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_DOWN))
                monsterCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
    }
}