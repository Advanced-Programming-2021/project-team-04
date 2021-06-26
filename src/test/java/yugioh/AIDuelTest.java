package yugioh;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import yugioh.controller.MainController;
import yugioh.controller.ShopController;
import yugioh.model.*;
import yugioh.model.cards.Card;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;
import org.junit.jupiter.api.BeforeAll;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

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
        thisAccount.addDeck(playerDeck);
        thisAccount.setActivePlayerDeck("Damaged");
        MainController.getInstance().newAIDuel(1, AI.AIDifficulty.EASY);
    }

    @Test
    public void strongestMonsterInHandNoT() {
        MonsterCard toughWitch = AI.getInstance().getStrongestMonsterCardInHandWithNoTributes();
        Assertions.assertNotNull(toughWitch);
    }

    @Test
    public void summon() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        AI.getInstance().summonMonster();
        Assertions.assertEquals("you can’t summon this card\r\n", outputStream.toString());
    }

    @Test
    public void canTribute1M() {
        boolean toCheck = AI.getInstance().canTributeOneMonster();
        Assertions.assertFalse(toCheck);
    }

    @Test
    public void canTribute2M() {
        boolean toCheck = AI.getInstance().canTributeTwoMonsters();
        Assertions.assertFalse(toCheck);
    }

    @Test
    public void weakestMInCZone() {
        ArrayList<Card> handyWitch = new ArrayList<>();
        MonsterCard hornImp = (MonsterCard) Card.getCardByName("Horn Imp");
        hornImp.setOwner(AI.getInstance());
        for (int i = 0; i < 5; i++)
            handyWitch.add(hornImp);
        AI.getInstance().getField().setHand(handyWitch);
        AI.getInstance().summonMonster();
        Assertions.assertTrue(AI.getInstance().getField().getMonsterCards().contains(hornImp));
    }
}
