import controller.DuelController;
import controller.MainController;
import controller.ShopController;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import view.IO;
import view.Input;

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
        IO.getInstance().resetScanner();
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
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
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
        IO.getInstance().resetScanner();
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
        IO.getInstance().resetScanner();
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
        IO.getInstance().resetScanner();
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
        IO.getInstance().resetScanner();
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
        IO.getInstance().resetScanner();
        DuelController.getInstance().heraldOfCreation();
        System.setIn(backup);
        boolean hasCard = thisPlayer.getField().getHand().contains(spiralSerpent);
        thisPlayer.setAllCards(backupCards);
        thisPlayer.getField().setHand(hand);
        thisPlayer.getField().setGraveyard(graveyardBackUp);
        Assertions.assertTrue(hasCard);
    }

    @Test
    public void forScannerTest() {
        ArrayList<Card> opponentGY = theOtherPlayer.getField().getGraveyard();
        ArrayList<MonsterCard> thisMonsterZone = thisPlayer.getField().getMonsterCards();
        theOtherPlayer.getField().setGraveyard(new ArrayList<>());
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Wattaildragon");
        monsterCard.setOwner(theOtherPlayer);
        theOtherPlayer.getField().getGraveyard().add(monsterCard);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n0".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        Scanner scanner = (Scanner) Card.getCardByName("Scanner");
        scanner.setOwner(thisPlayer);
        DuelController.getInstance().forScanner(scanner);
        Assertions.assertTrue(thisPlayer.getField().getMonsterCards().contains(monsterCard));
        scanner.reset();
        Assertions.assertTrue(theOtherPlayer.getField().getGraveyard().contains(monsterCard));
        theOtherPlayer.getField().setGraveyard(opponentGY);
        thisPlayer.getField().setMonsterCards(thisMonsterZone);
        System.setIn(backup);
    }

    @Test
    public void forManEaterTest() {
        ArrayList<Card> opponentGY = theOtherPlayer.getField().getGraveyard();
        ArrayList<MonsterCard> monsterCards = theOtherPlayer.getField().getMonsterCards();
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Wattaildragon");
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        monsterCard.setOwner(theOtherPlayer);
        theOtherPlayer.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().forManEaterBug();
        Assertions.assertTrue(theOtherPlayer.getField().getGraveyard().contains(monsterCard));
        theOtherPlayer.getField().setGraveyard(opponentGY);
        theOtherPlayer.getField().setMonsterCards(monsterCards);
        System.setIn(backup);
    }

    @Test
    public void supplySquadTest() {
        ArrayList<MonsterCard> monsterCards = thisPlayer.getField().getMonsterCards();
        ArrayList<Card> myGY = thisPlayer.getField().getGraveyard();
        ArrayList<Card> deck = thisPlayer.getField().getDeckZone();
        ArrayList<SpellAndTrapCard> spellAndTrapCards = thisPlayer.getField().getTrapAndSpell();
        ArrayList<Card> hand = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().setDeckZone(new ArrayList<>());
        SpellAndTrapCard spellAndTrapCard = (SpellAndTrapCard) Card.getCardByName("Supply Squad");
        spellAndTrapCard.setOwner(thisPlayer);
        spellAndTrapCard.setActive(true);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Dark magician");
        monsterCard.setOwner(thisPlayer);
        MonsterCard toDie = (MonsterCard) Card.getCardByName("Dark magician");
        toDie.setOwner(thisPlayer);
        thisPlayer.getField().getMonsterCards().add(toDie);
        thisPlayer.getField().getTrapAndSpell().add(spellAndTrapCard);
        thisPlayer.getField().getDeckZone().add(monsterCard);
        DuelController.getInstance().handleSupplySquad(toDie, thisPlayer.getField());
        Assertions.assertTrue(thisPlayer.getField().getHand().contains(monsterCard));
        thisPlayer.getField().setMonsterCards(monsterCards);
        thisPlayer.getField().setGraveyard(myGY);
        thisPlayer.getField().setDeckZone(deck);
        thisPlayer.getField().setTrapAndSpell(spellAndTrapCards);
        thisPlayer.getField().setHand(hand);
    }

//    @Test
//    public void surrenderTest() {
//        DuelController.getInstance().surrender();
//        Assertions.assertTrue(DuelController.getInstance().getGame().isGameFinished()); //TODO just do sth
//    }

    @Test
    public void decreaseLPTest() {
        int LP = theOtherPlayer.getLP();
        theOtherPlayer.setLP(8000);
        DuelController.getInstance().cheatDecreaseLP(500);
        Assertions.assertEquals(7500, theOtherPlayer.getLP());
        theOtherPlayer.setLP(LP);
    }

    @Test
    public void seeMyDeckTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().cheatSeeMyDeck();
        Assertions.assertEquals("Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\r\n", outputStream.toString());
    }

    @Test
    public void increaseLPTest() {
        int LP = thisPlayer.getLP();
        DuelController.getInstance().cheatIncreaseLP(500);
        Assertions.assertEquals(LP + 500, thisPlayer.getLP());
        thisPlayer.setLP(LP);
    }

    @Test
    public void showRivalHandTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().cheatShowRivalHand();
        Assertions.assertEquals("Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\n" +
                "Dark Hole\r\n", outputStream.toString());
    }

    @Test
    public void terraTigerTest() {
        Card selectedCard = DuelController.getInstance().getGame().getSelectedCard();
        ArrayList<MonsterCard> monsterCards = thisPlayer.getField().getMonsterCards();
        ArrayList<Card> hand = thisPlayer.getField().getHand();
        MonsterCard abbas = (MonsterCard) Card.getCardByName("Haniwa");
        MonsterCard selected = (MonsterCard) Card.getCardByName("Skull Guardian");
        DuelController.getInstance().getGame().setSelectedCard(selected);
        abbas.setOwner(thisPlayer);
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().getHand().add(abbas);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().terraTigerMethod();
        Assertions.assertNull(DuelController.getInstance().getGame().getSelectedCard());
        thisPlayer.getField().setMonsterCards(monsterCards);
        thisPlayer.getField().setHand(hand);
        DuelController.getInstance().getGame().setSelectedCard(selectedCard);
        System.setIn(backup);
    }

    @Test
    public void showSelectedCard() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Silver Fang");
        monsterCard.setOwner(thisPlayer);
        Card already = DuelController.getInstance().getGame().getSelectedCard();
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().showSelectedCard();
        Assertions.assertEquals("Name: Silver Fang\n" +
                "Level: 3\n" +
                "Type: Beast\n" +
                "ATK: 1200\n" +
                "DEF: 800\n" +
                "Description: A snow wolf that's beautiful to the eye, but absolutely vicious in battle.\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(already);
    }

    @Test
    public void showSelectedError1() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Silver Fang");
        monsterCard.setOwner(theOtherPlayer);
        Card already = DuelController.getInstance().getGame().getSelectedCard();
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_DOWN);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().showSelectedCard();
        Assertions.assertEquals("card is not visible\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(already);
    }
    @Test
    public void showSelectedError2() {
        SpellAndTrapCard spellAndTrapCard = (SpellAndTrapCard) Card.getCardByName("Spell Absorption");
        spellAndTrapCard.setOwner(theOtherPlayer);
        Card already = DuelController.getInstance().getGame().getSelectedCard();
        DuelController.getInstance().getGame().setSelectedCard(spellAndTrapCard);
        spellAndTrapCard.setActive(false);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().showSelectedCard();
        Assertions.assertEquals("card is not visible\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(already);
    }
    @Test
    public void showGY() {
        ArrayList<Card> GY = thisPlayer.getField().getGraveyard();
        SpellAndTrapCard spellAndTrapCard = (SpellAndTrapCard) Card.getCardByName("Spell Absorption");
        spellAndTrapCard.setOwner(thisPlayer);
        thisPlayer.getField().setGraveyard(new ArrayList<>());
        thisPlayer.getField().getGraveyard().add(spellAndTrapCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().showGraveyard();
        Assertions.assertEquals("Spell Absorption:Each time a Spell Card is activated," +
                " gain 500 Life Points immediately after it resolves.\r\n", outputStream.toString());
        thisPlayer.getField().setGraveyard(GY);
    }
}
