package yugioh.model.cards.specialcards;


import lombok.Getter;
import lombok.Setter;
import yugioh.controller.DuelController;
import yugioh.model.AI;
import yugioh.model.Duelist;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.MonsterCardModeInField;
import yugioh.model.cards.SpellAndTrapCard;

import java.util.ArrayList;


@Getter
@Setter
public class SwordsOfRevealingLight extends SpellAndTrapCard {

    private int counter = 0;

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