import controller.DuelController;
import controller.ShopController;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class DuelTest {
    static Account thisPlayer = new Account("Bad Decisions", "The Strokes", "Why Do I Exist");
    static Account theOtherPlayer = new Account("Delilah", "Jessica","Ricca");
    static SpellAndTrapCard card;
    @BeforeAll
    public static void setup() {
        ShopController.getInstance();
        card = (SpellAndTrapCard) Card.getCardByName("Dark Hole");
        Deck deck = new Deck("Damaged");
        for (int i = 0; i < 40; i++)
            deck.getMainDeck().add(card);
        thisPlayer.setActiveDeck(deck);
        theOtherPlayer.setActiveDeck(deck);
        DuelController.getInstance().setGame(new Game(thisPlayer, theOtherPlayer, 3, false));
    }
    @Test
    public void rpsTest() {
        InputStream backup = System.in;
        ByteArrayInputStream in = new ByteArrayInputStream("Bad Decisions".getBytes());
        System.setIn(in);
        DuelController.getInstance().rockPaperScissor("s","r");
        DuelController.getInstance().rockPaperScissor("r","p");
        Assertions.assertEquals(thisPlayer, DuelController.getInstance().getGame().getCurrentPlayer());
        System.setIn(backup);
    }
    @Test
    public void drawTest() {
        thisPlayer.setCanDraw(true);
        DuelController.getInstance().drawPhase();
        Assertions.assertEquals(6,thisPlayer.getField().getHand().size());
        Assertions.assertEquals(39, thisPlayer.getField().getDeckZone().size());
    }
    @Test
    public void battlePhaseTest() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        thisPlayer.setCanPlayerAttack(false);
        DuelController.getInstance().battlePhase();
        Assertions.assertTrue(thisPlayer.canPlayerAttack());
        Assertions.assertEquals(Phases.SECOND_MAIN_PHASE,DuelController.getInstance().getGame().getCurrentPhase());
    }
    @Test
    public void resetTest() {
        card.setOwner(thisPlayer);
        card.setHasBeenUsedInThisTurn(true);
        thisPlayer.getField().getTrapAndSpell().add(card);
        DuelController.getInstance().endPhase();
        Assertions.assertFalse(card.isHasBeenUsedInThisTurn());
        thisPlayer.getField().getTrapAndSpell().remove(card);
        card.setOwner(null);
    }
}
