package yugioh;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import yugioh.controller.MainController;
import yugioh.controller.ShopController;
import yugioh.model.*;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;
import org.junit.jupiter.api.BeforeAll;

public class AIDuelTest {

    public static Account thisAccount = new Account("Pvt. Witt", "I_ACTUALLY_SAW_ANOTHER_WORLD_WITCH", "Witt");
    static SpellAndTrapCard card;

    @BeforeAll
    public static void setup() {
        ShopController.getInstance();
        MainController.getInstance().setLoggedIn(thisAccount);
        var cardName = "Silver Fang";
        PlayerDeck playerDeck = new PlayerDeck("Damaged");
        for (int i = 0; i < 40; i++)
            playerDeck.addCardToMainDeck(cardName);
        thisAccount.setActivePlayerDeck("Damaged");
        MainController.getInstance().newAIDuel(1, AI.AIDifficulty.EASY);
    }

    @Test
    public void strongestMonsterInHandNoT() {
        MonsterCard toughWitch = AI.getInstance().getStrongestMonsterCardInHandWithNoTributes();
        Assertions.assertNotNull(toughWitch);
    }
}
