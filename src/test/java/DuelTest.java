import controller.DuelController;
import controller.MainController;
import controller.ShopController;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class DuelTest {
    static Account thisPlayer = new Account("Bad Decisions", "The Strokes", "Why Do I Exist");
    static Account theOtherPlayer = new Account("Delilah", "Jessica", "Ricca");
    static SpellAndTrapCard card;

    @BeforeAll
    public static void setup() {
        ShopController.getInstance();
        MainController.getInstance().setLoggedIn(thisPlayer);
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
        DuelController.getInstance().rockPaperScissor("s", "r");
        DuelController.getInstance().rockPaperScissor("r", "p");
        System.setIn(backup);
        Assertions.assertEquals(thisPlayer, DuelController.getInstance().getGame().getCurrentPlayer());
    }

    @Test
    public void drawTest() {
        thisPlayer.setCanDraw(true);
        DuelController.getInstance().drawPhase();
        Assertions.assertEquals(6, thisPlayer.getField().getHand().size());
        Assertions.assertEquals(39, thisPlayer.getField().getDeckZone().size());
    }

    @Test
    public void battlePhaseTest() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        thisPlayer.setCanPlayerAttack(false);
        DuelController.getInstance().battlePhase();
        Assertions.assertTrue(thisPlayer.canPlayerAttack());
        Assertions.assertEquals(Phases.SECOND_MAIN_PHASE, DuelController.getInstance().getGame().getCurrentPhase());
    }

    @Test
    public void resetTest() {
        card.setOwner(thisPlayer);
        card.setHasBeenUsedInThisTurn(true);
        thisPlayer.getField().getTrapAndSpell().add(card);
        DuelController.getInstance().endPhase();
        thisPlayer.getField().getTrapAndSpell().remove(card);
        card.setOwner(null);
        Assertions.assertFalse(card.isHasBeenUsedInThisTurn());
    }

    @Test
    public void selectErrorsHandTest() {
        int number = thisPlayer.getField().getHand().size() + 1;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().selectCard(true, CardStatusInField.HAND, number);
        Assertions.assertEquals("invalid selection\r\n", outputStream.toString());
    }

    @Test
    public void selectErrorsMonsterTest() {
        int number = thisPlayer.getField().getMonsterCards().size() + 1;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().selectCard(true, CardStatusInField.MONSTER_FIELD, number);
        Assertions.assertEquals("invalid selection\r\n", outputStream.toString());
    }

    @Test
    public void selectErrorsSpellTest() {
        int number = thisPlayer.getField().getTrapAndSpell().size() + 1;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().selectCard(true, CardStatusInField.SPELL_FIELD, number);
        Assertions.assertEquals("invalid selection\r\n", outputStream.toString());
    }

    @Test
    public void selectErrorsField() {
        thisPlayer.getField().setFieldZone(null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().selectCard(true, CardStatusInField.FIELD_ZONE, 0);
        Assertions.assertEquals("no card found in the given position\r\n", outputStream.toString());
    }

    @Test
    public void selectTest() {
        DuelController.getInstance().selectCard(true, CardStatusInField.HAND, 0);
        Assertions.assertEquals(DuelController.getInstance().getGame().getSelectedCard(), card);
        DuelController.getInstance().deselectCard();
        Assertions.assertNull(DuelController.getInstance().getGame().getSelectedCard());
    }

    @Test
    public void nextPhaseTest() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.DRAW_PHASE);
        DuelController.getInstance().nextPhase();
        Assertions.assertEquals(Phases.STANDBY_PHASE, DuelController.getInstance().getGame().getCurrentPhase());
    }

    @Test
    public void texChangerTest() {
        MonsterCard texChanger = (MonsterCard) Card.getCardByName("Texchanger");
        ArrayList<Card> backupHand = theOtherPlayer.getField().getHand();
        theOtherPlayer.getField().setHand(new ArrayList<>());
        MonsterCard leotron = (MonsterCard) Card.getCardByName("Leotron");
        leotron.setOwner(theOtherPlayer);
        theOtherPlayer.getField().getHand().add(leotron);
        theOtherPlayer.getField().getMonsterCards().add(texChanger);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("1\r\n0\r\n".getBytes());
        System.setIn(input);
        DuelController.getInstance().texChanger(texChanger);
        System.setIn(backup);
        theOtherPlayer.getField().setHand(backupHand);
        leotron.setOwner(null);
        Assertions.assertTrue(theOtherPlayer.getField().getMonsterCards().contains(leotron));
    }

    @Test
    public void barbarosTestTwo() {
        MonsterCard barbaros = (MonsterCard) Card.getCardByName("Beast King Barbaros");
        DuelController.getInstance().getGame().setSelectedCard(barbaros);
        DuelController.getInstance().barbaros(2);
        Assertions.assertEquals(1900, barbaros.getThisCardAttackPower());
    }

    @Test
    public void barbarosTestThree() {
        theOtherPlayer.getField().getMonsterCards().add((MonsterCard) Card.getCardByName("Baby dragon"));
        MonsterCard barbaros = (MonsterCard) Card.getCardByName("Beast King Barbaros");
        DuelController.getInstance().getGame().setSelectedCard(barbaros);
        ArrayList<Card> backupCards = thisPlayer.getAllCards();
        thisPlayer.setAllCards(new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            ShopController.getInstance().buyCard("Baby dragon");
            thisPlayer.getField().getMonsterCards().add((MonsterCard) thisPlayer.getAllCards().get(0));
        }
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n0\r\n0\r\n".getBytes());
        System.setIn(input);
        DuelController.getInstance().barbaros(3);
        System.setIn(backup);
        thisPlayer.setAllCards(backupCards);
        Assertions.assertEquals(0, theOtherPlayer.getField().getMonsterCards().size());
    }

    @Test
    public void gateGuardianTest() {
        ShopController.getInstance().buyCard("Gate Guardian");
        MonsterCard gateGuardian = (MonsterCard) Card.getCardByName("Gate Guardian");
        gateGuardian.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(gateGuardian);
        ArrayList<Card> backupCards = thisPlayer.getAllCards();
        thisPlayer.setAllCards(new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            ShopController.getInstance().buyCard("Baby dragon");
            thisPlayer.getField().getMonsterCards().add((MonsterCard) thisPlayer.getAllCards().get(0));
        }
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n0\r\n0\r\n".getBytes());
        System.setIn(input);
        DuelController.getInstance().gateGuardian();
        System.setIn(backup);
        thisPlayer.setAllCards(backupCards);
        Assertions.assertNull(DuelController.getInstance().getGame().getSelectedCard());
    }

    @Test
    public void theTrickyTest() {
        MonsterCard theTricky = (MonsterCard) Card.getCardByName("The Tricky");
        theTricky.setOwner(thisPlayer);
        ArrayList<Card> backupCards = thisPlayer.getAllCards();
        ArrayList<Card> hand = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.setAllCards(new ArrayList<>());
        DuelController.getInstance().getGame().setSelectedCard(theTricky);
        ShopController.getInstance().buyCard("Baby dragon");
        thisPlayer.getField().getHand().add((MonsterCard) thisPlayer.getAllCards().get(0));
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0".getBytes());
        System.setIn(input);
        DuelController.getInstance().theTricky();
        System.setIn(backup);
        thisPlayer.setAllCards(backupCards);
        thisPlayer.getField().setHand(hand);
        Assertions.assertNull(DuelController.getInstance().getGame().getSelectedCard());
    }

    @Test
    public void heraldOfCreationTest() {
        MonsterCard spiralSerpent = (MonsterCard) Card.getCardByName("Spiral Serpent");
        spiralSerpent.setOwner(thisPlayer);
        ArrayList<Card> graveyardBackUp = thisPlayer.getField().getGraveyard();
        ArrayList<Card> backupCards = thisPlayer.getAllCards();
        ArrayList<Card> hand = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.setAllCards(new ArrayList<>());
        thisPlayer.getField().setGraveyard(new ArrayList<>());
        thisPlayer.getField().getGraveyard().add(spiralSerpent);
        ShopController.getInstance().buyCard("Baby dragon");
        thisPlayer.getField().getHand().add((MonsterCard) thisPlayer.getAllCards().get(0));
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n0\r\n".getBytes());
        System.setIn(input);
        DuelController.getInstance().heraldOfCreation();
        System.setIn(backup);
        boolean hasCard = thisPlayer.getField().getHand().contains(spiralSerpent);
        thisPlayer.setAllCards(backupCards);
        thisPlayer.getField().setHand(hand);
        thisPlayer.getField().setGraveyard(graveyardBackUp);
        Assertions.assertTrue(hasCard);
    }
}
