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
import java.util.HashMap;

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

    @Test
    public void ritualTestErrors1() {
        ArrayList<MonsterCard> backUpCards = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().ritualSummon();
        Assertions.assertEquals("there is no way you could ritual summon a monster\r\n", outputStream.toString());
        thisPlayer.getField().setMonsterCards(backUpCards);
    }

    @Test
    public void ritualTestErrors2() {
        ArrayList<Card> handBackUp = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        MonsterCard ritualCard = (MonsterCard) Card.getCardByName("Skull Guardian");
        ritualCard.setOwner(thisPlayer);
        thisPlayer.getField().getHand().add(ritualCard);
        MonsterCard firstSacrifice = (MonsterCard) Card.getCardByName("Wattkid");
        firstSacrifice.setOwner(thisPlayer);
        MonsterCard secondSacrifice = (MonsterCard) Card.getCardByName("Wattaildragon");
        secondSacrifice.setOwner(thisPlayer);
        ArrayList<MonsterCard> backUpCards = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(firstSacrifice);
        thisPlayer.getField().getMonsterCards().add(secondSacrifice);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().ritualSummon();
        Assertions.assertEquals("there is no way you could ritual summon a monster\r\n", outputStream.toString());
        thisPlayer.getField().setMonsterCards(backUpCards);
        thisPlayer.getField().setHand(handBackUp);
    }

    @Test
    public void ritualTest() {
        SpellAndTrapCard spell = (SpellAndTrapCard) Card.getCardByName("Advanced Ritual Art");
        spell.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(spell);
        ArrayList<Card> handBackUp = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        MonsterCard ritualCard = (MonsterCard) Card.getCardByName("Skull Guardian"); //7
        ritualCard.setOwner(thisPlayer);
        thisPlayer.getField().getHand().add(ritualCard);
        MonsterCard firstSacrifice = (MonsterCard) Card.getCardByName("Wattkid");
        firstSacrifice.setOwner(thisPlayer);
        MonsterCard secondSacrifice = (MonsterCard) Card.getCardByName("Warrior Dai Grepher");
        secondSacrifice.setOwner(thisPlayer);
        ArrayList<MonsterCard> backUpCards = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(firstSacrifice);
        thisPlayer.getField().getMonsterCards().add(secondSacrifice);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n0 1\r\nattack\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().ritualSummon();
        Assertions.assertEquals("enter the number of the ritual card you choose:\r\n" +
                "enter the numbers of the card you want to tribute divided by a space:\r\n" +
                "choose the monster mode (Attack or Defense):\r\n" +
                "summoned successfully\r\n", outputStream.toString());
        thisPlayer.getField().setHand(handBackUp);
        thisPlayer.getField().setMonsterCards(backUpCards);
    }

    @Test
    public void setMonsterTest() {
        MonsterCard silverFag = (MonsterCard) Card.getCardByName("Silver Fang");
        silverFag.setOwner(thisPlayer);
        ArrayList<Card> hand = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().getHand().add(silverFag);
        DuelController.getInstance().getGame().setSelectedCard(silverFag);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        ArrayList<MonsterCard> monsterBackUp = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        DuelController.getInstance().getGame().setHasSummonedInThisTurn(false);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().set();
        Assertions.assertEquals("set successfully\r\n", outputStream.toString());
        thisPlayer.getField().setHand(hand);
        thisPlayer.getField().setMonsterCards(monsterBackUp);
    }

    @Test
    public void setTrapSpellTest() {
        SpellAndTrapCard vanity = (SpellAndTrapCard) Card.getCardByName("Vanity's Emptiness");
        vanity.setOwner(thisPlayer);
        ArrayList<Card> hand = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().getHand().add(vanity);
        DuelController.getInstance().getGame().setSelectedCard(vanity);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = thisPlayer.getField().getTrapAndSpell();
        thisPlayer.getField().setTrapAndSpell(new ArrayList<>());
        DuelController.getInstance().getGame().setHasSummonedInThisTurn(false);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().set();
        Assertions.assertEquals("set successfully\r\n", outputStream.toString());
        thisPlayer.getField().setHand(hand);
        thisPlayer.getField().setTrapAndSpell(spellAndTrapCards);
    }

    @Test
    public void monsterRebornTest() {
        SpellAndTrapCard monsterReborn = (SpellAndTrapCard) Card.getCardByName("Monster Reborn");
        monsterReborn.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> spellZone = thisPlayer.getField().getTrapAndSpell();
        ArrayList<Card> GY = thisPlayer.getField().getGraveyard();
        MonsterCard marshmallon = (MonsterCard) Card.getCardByName("Marshmallon");
        marshmallon.setOwner(thisPlayer);
        thisPlayer.getField().setGraveyard(new ArrayList<>());
        thisPlayer.getField().getGraveyard().add(marshmallon);
        thisPlayer.getField().setTrapAndSpell(new ArrayList<>());
        thisPlayer.getField().getTrapAndSpell().add(monsterReborn);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().monsterReborn(monsterReborn);
        Assertions.assertTrue(thisPlayer.getField().getGraveyard().contains(monsterReborn));
        thisPlayer.getField().setTrapAndSpell(spellZone);
        thisPlayer.getField().setGraveyard(GY);
    }

    @Test
    public void terraFormingTest() {
        SpellAndTrapCard terraForming = (SpellAndTrapCard) Card.getCardByName("Terraforming");
        terraForming.setOwner(thisPlayer);
        ArrayList<Card> handBackUp = thisPlayer.getField().getHand();
        ArrayList<Card> GY = thisPlayer.getField().getGraveyard();
        thisPlayer.getField().setHand(new ArrayList<>());
        ArrayList<Card> deckBackUp = thisPlayer.getField().getDeckZone();
        SpellAndTrapCard witch = (SpellAndTrapCard) Card.getCardByName("Forest");
        witch.setOwner(thisPlayer);
        thisPlayer.getField().setDeckZone(new ArrayList<>());
        thisPlayer.getField().getDeckZone().add(witch);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().terraforming(terraForming);
        Assertions.assertTrue(thisPlayer.getField().getGraveyard().contains(terraForming));
        thisPlayer.getField().setHand(handBackUp);
        thisPlayer.getField().setDeckZone(deckBackUp);
        thisPlayer.getField().setGraveyard(GY);
    }

    @Test
    public void potOfGreedTest() {
        SpellAndTrapCard potOfGreed = (SpellAndTrapCard) Card.getCardByName("Pot of Greed");
        potOfGreed.setOwner(thisPlayer);
        ArrayList<Card> handBackUp = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        ArrayList<Card> deckBackUp = thisPlayer.getField().getDeckZone();
        thisPlayer.getField().setDeckZone(new ArrayList<>());
        SpellAndTrapCard joe = (SpellAndTrapCard) Card.getCardByName("Forest");
        joe.setOwner(thisPlayer);
        SpellAndTrapCard cherry = (SpellAndTrapCard) Card.getCardByName("Forest");
        cherry.setOwner(thisPlayer);
        thisPlayer.getField().setDeckZone(new ArrayList<>());
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().getDeckZone().add(joe);
        thisPlayer.getField().getDeckZone().add(cherry);
        DuelController.getInstance().potOfGreed(potOfGreed);
        Assertions.assertTrue(thisPlayer.getField().getHand().contains(joe) &&
                thisPlayer.getField().getHand().contains(cherry));
        thisPlayer.getField().setHand(handBackUp);
        thisPlayer.getField().setDeckZone(deckBackUp);
    }

    @Test
    public void raigekiTest() {
        SpellAndTrapCard reki = (SpellAndTrapCard) Card.getCardByName("Raigeki");
        reki.setOwner(thisPlayer);
        ArrayList<MonsterCard> opponentMonsterCards = theOtherPlayer.getField().getMonsterCards();
        ArrayList<Card> myGY = thisPlayer.getField().getGraveyard();
        ArrayList<SpellAndTrapCard> spellAndTrapCards = thisPlayer.getField().getTrapAndSpell();
        MonsterCard spiralSerpent = (MonsterCard) Card.getCardByName("Spiral Serpent");
        spiralSerpent.setOwner(theOtherPlayer);
        MonsterCard hornImp = (MonsterCard) Card.getCardByName("Horn Imp");
        hornImp.setOwner(theOtherPlayer);
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        theOtherPlayer.getField().getMonsterCards().add(hornImp);
        theOtherPlayer.getField().getMonsterCards().add(spiralSerpent);
        DuelController.getInstance().raigeki(reki);
        Assertions.assertTrue(theOtherPlayer.getField().getMonsterCards().isEmpty());
        theOtherPlayer.getField().setMonsterCards(opponentMonsterCards);
        thisPlayer.getField().setGraveyard(myGY);
        thisPlayer.getField().setTrapAndSpell(spellAndTrapCards);
    }

    @Test
    public void harpiesFeatherDusterTest() {
        SpellAndTrapCard harpie = (SpellAndTrapCard) Card.getCardByName("Harpie's Feather Duster");
        harpie.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> spellCards = theOtherPlayer.getField().getTrapAndSpell();
        ArrayList<Card> myGY = thisPlayer.getField().getGraveyard();
        ArrayList<SpellAndTrapCard> spellAndTrapCards = thisPlayer.getField().getTrapAndSpell();
        SpellAndTrapCard solemnWarning = (SpellAndTrapCard) Card.getCardByName("Solemn Warning");
        solemnWarning.setOwner(theOtherPlayer);
        SpellAndTrapCard timeSeal = (SpellAndTrapCard) Card.getCardByName("Time Seal");
        timeSeal.setOwner(theOtherPlayer);
        theOtherPlayer.getField().setTrapAndSpell(new ArrayList<>());
        theOtherPlayer.getField().getTrapAndSpell().add(solemnWarning);
        theOtherPlayer.getField().getTrapAndSpell().add(timeSeal);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("no\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().harpiesFeatherDuster(harpie);
        Assertions.assertTrue(theOtherPlayer.getField().getTrapAndSpell().isEmpty());
        theOtherPlayer.getField().setTrapAndSpell(spellCards);
        thisPlayer.getField().setGraveyard(myGY);
        thisPlayer.getField().setTrapAndSpell(spellAndTrapCards);
    }

    @Test
    public void darkHoleTest() {
        SpellAndTrapCard darkHole = (SpellAndTrapCard) Card.getCardByName("Dark Hole");
        darkHole.setOwner(thisPlayer);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        ArrayList<MonsterCard> opponentsMonsters = theOtherPlayer.getField().getMonsterCards();
        ArrayList<Card> myHand = thisPlayer.getField().getHand();
        ArrayList<Card> myGY = thisPlayer.getField().getGraveyard();
        ArrayList<Card> opponentGY = theOtherPlayer.getField().getGraveyard();
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        MonsterCard bitron = (MonsterCard) Card.getCardByName("Bitron");
        bitron.setOwner(thisPlayer);
        thisPlayer.getField().getMonsterCards().add(bitron);
        MonsterCard battleOx = (MonsterCard) Card.getCardByName("Battle OX");
        battleOx.setOwner(theOtherPlayer);
        theOtherPlayer.getField().getMonsterCards().add(battleOx);
        DuelController.getInstance().darkHole(darkHole);
        Assertions.assertTrue(theOtherPlayer.getField().getMonsterCards().isEmpty() &&
                thisPlayer.getField().getMonsterCards().isEmpty());
        thisPlayer.getField().setMonsterCards(myMonsters);
        theOtherPlayer.getField().setMonsterCards(opponentsMonsters);
        theOtherPlayer.getField().setGraveyard(opponentGY);
        thisPlayer.getField().setGraveyard(myGY);
        thisPlayer.getField().setHand(myHand);
    }

    @Test
    public void mysticalSpaceTyphoonTest() {
        SpellAndTrapCard mystical = (SpellAndTrapCard) Card.getCardByName("Mystical space typhoon");
        mystical.setOwner(thisPlayer);
        SpellAndTrapCard umiiruka = (SpellAndTrapCard) Card.getCardByName("Umiiruka");
        umiiruka.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = thisPlayer.getField().getTrapAndSpell();
        ArrayList<Card> GY = thisPlayer.getField().getGraveyard();
        thisPlayer.getField().setTrapAndSpell(new ArrayList<>());
        thisPlayer.getField().getTrapAndSpell().add(umiiruka);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().mysticalSpaceTyphoon(mystical);
        Assertions.assertTrue(thisPlayer.getField().getTrapAndSpell().isEmpty());
        thisPlayer.getField().setTrapAndSpell(spellAndTrapCards);
        thisPlayer.getField().setGraveyard(GY);
    }

    @Test
    public void getAllMonstersTest() {
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        ArrayList<MonsterCard> opponentsMonsters = theOtherPlayer.getField().getMonsterCards();
        MonsterCard silverFag = (MonsterCard) Card.getCardByName("Silver Fang");
        silverFag.setOwner(thisPlayer);
        MonsterCard gateGuardian = (MonsterCard) Card.getCardByName("Gate Guardian");
        gateGuardian.setOwner(theOtherPlayer);
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        theOtherPlayer.getField().getMonsterCards().add(gateGuardian);
        thisPlayer.getField().getMonsterCards().add(silverFag);
        ArrayList<MonsterCard> toCompare = new ArrayList<>();
        toCompare.add(silverFag);
        toCompare.add(gateGuardian);
        ArrayList<MonsterCard> monsterCards = DuelController.getInstance().getAllMonsterCards();
        Assertions.assertEquals(monsterCards, toCompare);
        thisPlayer.getField().setMonsterCards(myMonsters);
        theOtherPlayer.getField().setMonsterCards(opponentsMonsters);
    }

    @Test
    public void yamiTest() {
        MonsterCard marshmallon = (MonsterCard) Card.getCardByName("Marshmallon");
        marshmallon.setOwner(thisPlayer);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(marshmallon);
        DuelController.getInstance().yami();
        Assertions.assertEquals(marshmallon.getThisCardAttackPower(), 100);
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void forestTest() {
        MonsterCard silverFag = (MonsterCard) Card.getCardByName("Silver Fang");
        silverFag.setOwner(thisPlayer);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(silverFag);
        DuelController.getInstance().forest();
        Assertions.assertEquals(1400, silverFag.getThisCardAttackPower());
        silverFag.reset();
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void closedForest() {
        MonsterCard silverFag = (MonsterCard) Card.getCardByName("Silver Fang");
        SpellAndTrapCard inTheCloset = (SpellAndTrapCard) Card.getCardByName("Closed Forest");
        inTheCloset.setOwner(thisPlayer);
        silverFag.setOwner(thisPlayer);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        ArrayList<Card> GY = thisPlayer.getField().getGraveyard();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().setGraveyard(new ArrayList<>());
        thisPlayer.getField().getGraveyard().add(inTheCloset);
        thisPlayer.getField().getMonsterCards().add(silverFag);
        DuelController.getInstance().closedForest();
        Assertions.assertEquals(1300, silverFag.getThisCardAttackPower());
        silverFag.reset();
        thisPlayer.getField().setMonsterCards(myMonsters);
        thisPlayer.getField().setGraveyard(GY);
    }

    @Test
    public void umiirukaTest() {
        MonsterCard yomi = (MonsterCard) Card.getCardByName("Yomi Ship");
        yomi.setOwner(thisPlayer);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(yomi);
        DuelController.getInstance().umiiruka();
        Assertions.assertEquals(1300, yomi.getThisCardAttackPower());
        Assertions.assertEquals(1000, yomi.getThisCardDefensePower());
        thisPlayer.getField().setMonsterCards(myMonsters);
        yomi.reset();
    }
}
