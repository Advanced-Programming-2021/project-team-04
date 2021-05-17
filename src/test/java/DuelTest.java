import controller.DuelController;
import controller.MainController;
import controller.ShopController;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import view.IO;

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
        GameDeck gameDeck = new GameDeck("Damaged");
        for (int i = 0; i < 40; i++)
            gameDeck.getMainDeck().add(card);
        thisPlayer.setActiveDeck(gameDeck);
        theOtherPlayer.setActiveDeck(gameDeck);
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

    @Test
    public void changeOfHeartTest() {
        ChangeOfHeart changeOfHeart = (ChangeOfHeart) Card.getCardByName("Change of Heart");
        changeOfHeart.setOwner(thisPlayer);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Spiral Serpent");
        monsterCard.setOwner(theOtherPlayer);
        ArrayList<MonsterCard> opponentMonsters = theOtherPlayer.getField().getMonsterCards();
        ArrayList<MonsterCard> thisPlayerMonsters = thisPlayer.getField().getMonsterCards();
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        theOtherPlayer.getField().getMonsterCards().add(monsterCard);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().changeOfHeart(changeOfHeart);
        Assertions.assertTrue(thisPlayer.getField().getMonsterCards().contains(monsterCard));
        theOtherPlayer.getField().setMonsterCards(opponentMonsters);
        thisPlayer.getField().setMonsterCards(thisPlayerMonsters);
    }

    @Test
    public void twinTwistersTest() {
        SpellAndTrapCard twinTwister = (SpellAndTrapCard) Card.getCardByName("Twin Twisters");
        SpellAndTrapCard cardFromHand = (SpellAndTrapCard) Card.getCardByName("Yami");
        SpellAndTrapCard toRemove = (SpellAndTrapCard) Card.getCardByName("Mind Crush");
        twinTwister.setOwner(thisPlayer);
        cardFromHand.setOwner(thisPlayer);
        toRemove.setOwner(theOtherPlayer);
        ArrayList<SpellAndTrapCard> thisPlayerSpells = thisPlayer.getField().getTrapAndSpell();
        thisPlayer.getField().setTrapAndSpell(new ArrayList<>());
        thisPlayer.getField().getTrapAndSpell().add(twinTwister);
        ArrayList<Card> hand = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().getHand().add(cardFromHand);
        ArrayList<SpellAndTrapCard> opponentSpells = theOtherPlayer.getField().getTrapAndSpell();
        theOtherPlayer.getField().setTrapAndSpell(new ArrayList<>());
        theOtherPlayer.getField().getTrapAndSpell().add(toRemove);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("no\r\nno\r\n0\r\n1\r\nno\r\n0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().twinTwisters(twinTwister);
        Assertions.assertFalse(theOtherPlayer.getField().getTrapAndSpell().contains(toRemove));
        thisPlayer.getField().setTrapAndSpell(thisPlayerSpells);
        thisPlayer.getField().setHand(hand);
        theOtherPlayer.getField().setTrapAndSpell(opponentSpells);
    }

    @Test
    public void mirrorForceTest() {
        SpellAndTrapCard mirrorForce = (SpellAndTrapCard) Card.getCardByName("Mirror Force");
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Curtain of the dark ones");
        mirrorForce.setOwner(thisPlayer);
        monsterCard.setOwner(theOtherPlayer);
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getTrapAndSpell();
        ArrayList<MonsterCard> opponentMonsters = theOtherPlayer.getField().getMonsterCards();
        thisPlayer.getField().setTrapAndSpell(new ArrayList<>());
        thisPlayer.getField().getTrapAndSpell().add(mirrorForce);
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        theOtherPlayer.getField().getMonsterCards().add(monsterCard);
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        DuelController.getInstance().mirrorForce(mirrorForce, theOtherPlayer);
        Assertions.assertFalse(theOtherPlayer.getField().getMonsterCards().contains(monsterCard));
        thisPlayer.getField().setTrapAndSpell(mySpells);
        theOtherPlayer.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void magicCylinderTest() {
        SpellAndTrapCard magicCylinder = (SpellAndTrapCard) Card.getCardByName("Magic Cylinder");
        MonsterCard attacker = (MonsterCard) Card.getCardByName("Feral Imp");
        magicCylinder.setOwner(thisPlayer);
        attacker.setOwner(theOtherPlayer);
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getTrapAndSpell();
        ArrayList<MonsterCard> opponentMonsters = theOtherPlayer.getField().getMonsterCards();
        thisPlayer.getField().setTrapAndSpell(new ArrayList<>());
        thisPlayer.getField().getTrapAndSpell().add(magicCylinder);
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        theOtherPlayer.getField().getMonsterCards().add(attacker);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        theOtherPlayer.setLP(8000);
        DuelController.getInstance().magicCylinder(attacker, magicCylinder);
        Assertions.assertEquals(8000 - attacker.getThisCardAttackPower(), theOtherPlayer.getLP());
        thisPlayer.getField().setTrapAndSpell(mySpells);
        theOtherPlayer.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void swordOfDarkDestruction() {
        SpellAndTrapCard swordOfDarkDestruction = (SpellAndTrapCard) Card.getCardByName("Sword of dark destruction");
        MonsterCard hornImp = (MonsterCard) Card.getCardByName("Horn Imp");
        swordOfDarkDestruction.setOwner(thisPlayer);
        hornImp.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getTrapAndSpell();
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setTrapAndSpell(new ArrayList<>());
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getTrapAndSpell().add(swordOfDarkDestruction);
        thisPlayer.getField().getMonsterCards().add(hornImp);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().equipMonster(swordOfDarkDestruction);
        Assertions.assertEquals(400 + hornImp.getClassAttackPower(), hornImp.getThisCardAttackPower());
        hornImp.reset();
        thisPlayer.getField().setTrapAndSpell(mySpells);
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void blackPendantTest() {
        SpellAndTrapCard blackPendant = (SpellAndTrapCard) Card.getCardByName("Black Pendant");
        MonsterCard hornImp = (MonsterCard) Card.getCardByName("Horn Imp");
        blackPendant.setOwner(thisPlayer);
        hornImp.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getTrapAndSpell();
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setTrapAndSpell(new ArrayList<>());
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getTrapAndSpell().add(blackPendant);
        thisPlayer.getField().getMonsterCards().add(hornImp);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().equipMonster(blackPendant);
        Assertions.assertEquals(500 + hornImp.getClassAttackPower(), hornImp.getThisCardAttackPower());
        hornImp.reset();
        thisPlayer.getField().setTrapAndSpell(mySpells);
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void unitedWeStandTest() {
        SpellAndTrapCard unitedWeStand = (SpellAndTrapCard) Card.getCardByName("United We Stand");
        MonsterCard hornImp = (MonsterCard) Card.getCardByName("Horn Imp");
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Dark Blade") ;
        unitedWeStand.setOwner(thisPlayer);
        hornImp.setOwner(thisPlayer);
        monsterCard.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getTrapAndSpell();
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setTrapAndSpell(new ArrayList<>());
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getTrapAndSpell().add(unitedWeStand);
        thisPlayer.getField().getMonsterCards().add(hornImp);
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().equipMonster(unitedWeStand);
        Assertions.assertEquals(1600 + hornImp.getClassAttackPower(), hornImp.getThisCardAttackPower());
        monsterCard.reset();
        thisPlayer.getField().setTrapAndSpell(mySpells);
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void magnumShield() {
        SpellAndTrapCard magnumShield = (SpellAndTrapCard) Card.getCardByName("Magnum Shield");
        MonsterCard darkBlade = (MonsterCard) Card.getCardByName("Dark Blade");
        magnumShield.setOwner(thisPlayer);
        darkBlade.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getTrapAndSpell();
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setTrapAndSpell(new ArrayList<>());
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getTrapAndSpell().add(magnumShield);
        thisPlayer.getField().getMonsterCards().add(darkBlade);
        darkBlade.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().equipMonster(magnumShield);
        Assertions.assertEquals(darkBlade.getClassAttackPower() + darkBlade.getClassDefensePower(), darkBlade.getThisCardAttackPower());
        thisPlayer.getField().setTrapAndSpell(mySpells);
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void mindCrushTest() {
        SpellAndTrapCard mindCrush = (SpellAndTrapCard) Card.getCardByName("Mind Crush");
        Card opponentCard = Card.getCardByName("Closed Forest");
        mindCrush.setOwner(thisPlayer);
        opponentCard.setOwner(theOtherPlayer);
        ArrayList<Card> opponentHand = theOtherPlayer.getField().getHand();
        theOtherPlayer.getField().setHand(new ArrayList<>());
        theOtherPlayer.getField().getHand().add(opponentCard);
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getTrapAndSpell();
        thisPlayer.getField().setTrapAndSpell(new ArrayList<>());
        thisPlayer.getField().getTrapAndSpell().add(mindCrush);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\nClosed Forest\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().mindCrush(mindCrush, theOtherPlayer);
        theOtherPlayer.getField().setHand(opponentHand);
        thisPlayer.getField().setTrapAndSpell(mySpells);
    }

    @Test
    public void removeRandomTest() {
        thisPlayer.getField().getHand().add(Card.getCardByName("Closed Forest"));
        int size = thisPlayer.getField().getHand().size();
        DuelController.getInstance().randomlyRemoveFromHand(thisPlayer);
        Assertions.assertEquals(size - 1, thisPlayer.getField().getHand().size());
    }

    @Test
    public void torrentialTributeTest() {
        SpellAndTrapCard torrential = (SpellAndTrapCard) Card.getCardByName("Torrential Tribute");
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Dark magician");
        torrential.setOwner(theOtherPlayer);
        monsterCard.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> opponentSpells = theOtherPlayer.getField().getTrapAndSpell();
        theOtherPlayer.getField().setTrapAndSpell(new ArrayList<>());
        theOtherPlayer.getField().getTrapAndSpell().add(torrential);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().torrentialTribute();
        Assertions.assertTrue(thisPlayer.getField().getMonsterCards().isEmpty());
        theOtherPlayer.getField().setTrapAndSpell(opponentSpells);
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void timeSealTest() {
        SpellAndTrapCard timeSeal = (SpellAndTrapCard) Card.getCardByName("Time Seal");
        timeSeal.setOwner(thisPlayer);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().timeSeal(timeSeal, theOtherPlayer);
        Assertions.assertFalse(theOtherPlayer.canDraw());
        theOtherPlayer.setCanDraw(true);
    }

    @Test
    public void magicJamamerTest() {
        SpellAndTrapCard spellAndTrapCard = (SpellAndTrapCard) Card.getCardByName("Umiiruka");
        spellAndTrapCard.setOwner(thisPlayer);
        SpellAndTrapCard magicJamamer = (SpellAndTrapCard) Card.getCardByName("Magic Jamamer");
        magicJamamer.setOwner(theOtherPlayer);
        Card toRemove = Card.getCardByName("Fireyarou");
        toRemove.setOwner(theOtherPlayer);
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getTrapAndSpell();
        ArrayList<SpellAndTrapCard> opponentSpells = theOtherPlayer.getField().getTrapAndSpell();
        ArrayList<Card> opponentHand = theOtherPlayer.getField().getHand();
        thisPlayer.getField().setTrapAndSpell(new ArrayList<>());
        thisPlayer.getField().getTrapAndSpell().add(spellAndTrapCard);
        theOtherPlayer.getField().setTrapAndSpell(new ArrayList<>());
        theOtherPlayer.getField().getTrapAndSpell().add(magicJamamer);
        theOtherPlayer.getField().setHand(new ArrayList<>());
        theOtherPlayer.getField().getHand().add(toRemove);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().magicJamamer(spellAndTrapCard);
        Assertions.assertTrue(thisPlayer.getField().getGraveyard().contains(spellAndTrapCard));
        thisPlayer.getField().setTrapAndSpell(mySpells);
        theOtherPlayer.getField().setTrapAndSpell(opponentSpells);
        theOtherPlayer.getField().setHand(opponentHand);
    }

    @Test
    public void solemnWarningTest() {
        theOtherPlayer.setLP(8000);
        SpellAndTrapCard solemnWarning = (SpellAndTrapCard) Card.getCardByName("Solemn Warning");
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Exploder Dragon");
        solemnWarning.setOwner(theOtherPlayer);
        monsterCard.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> opponentSpells = theOtherPlayer.getField().getTrapAndSpell();
        ArrayList<Card> myHand = thisPlayer.getField().getHand();
        theOtherPlayer.getField().setTrapAndSpell(new ArrayList<>());
        thisPlayer.getField().setHand(new ArrayList<>());
        theOtherPlayer.getField().getTrapAndSpell().add(solemnWarning);
        thisPlayer.getField().getHand().add(monsterCard);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().solemnWarning(monsterCard);
        Assertions.assertEquals(6000, theOtherPlayer.getLP());
        theOtherPlayer.getField().setTrapAndSpell(opponentSpells);
        thisPlayer.getField().setHand(myHand);
    }

    @Test
    public void callOfTheHauntedTest() {
        SpellAndTrapCard callOfTheHaunted = (SpellAndTrapCard) Card.getCardByName("Call of The Haunted");
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Wattkid");
        callOfTheHaunted.setOwner(thisPlayer);
        monsterCard.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getTrapAndSpell();
        ArrayList<Card> myGY = thisPlayer.getField().getGraveyard();
        thisPlayer.getField().setTrapAndSpell(new ArrayList<>());
        thisPlayer.getField().setGraveyard(new ArrayList<>());
        thisPlayer.getField().getTrapAndSpell().add(callOfTheHaunted);
        thisPlayer.getField().getGraveyard().add(monsterCard);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().callOfTheHaunted(callOfTheHaunted, thisPlayer.getField(), true);
        Assertions.assertTrue(thisPlayer.getField().getMonsterCards().contains(monsterCard));
        thisPlayer.getField().setTrapAndSpell(mySpells);
        thisPlayer.getField().setGraveyard(myGY);
    }

    @Test
    public void spellAbsorptionTest() {
        SpellAndTrapCard callOfTheHaunted = (SpellAndTrapCard) Card.getCardByName("Spell Absorption");
        callOfTheHaunted.setOwner(thisPlayer);
        callOfTheHaunted.setActive(true);
        int LP = thisPlayer.getLP();
        thisPlayer.setLP(8000);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = thisPlayer.getField().getTrapAndSpell();
        thisPlayer.getField().setTrapAndSpell(new ArrayList<>());
        thisPlayer.getField().getTrapAndSpell().add(callOfTheHaunted);
        DuelController.getInstance().selfAbsorption();
        Assertions.assertEquals(8500, thisPlayer.getLP());
        thisPlayer.setLP(LP);
        thisPlayer.getField().setTrapAndSpell(spellAndTrapCards);
        callOfTheHaunted.reset();
    }

    @Test
    public void errorForActivation1() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isActivatingSpellValid();
        Assertions.assertEquals("no card is selected yet\r\n", outputStream.toString());
    }

    @Test
    public void errorForActivation2() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Wattkid");
        monsterCard.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isActivatingSpellValid();
        Assertions.assertEquals("activate effect is only for spell cards.\r\n", outputStream.toString());
    }

    @Test
    public void errorForActivating3() {
        SpellAndTrapCard callOfTheHaunted = (SpellAndTrapCard) Card.getCardByName("Spell Absorption");
        callOfTheHaunted.setOwner(thisPlayer);
        Phases phase = DuelController.getInstance().getGame().getCurrentPhase();
        DuelController.getInstance().getGame().setCurrentPhase(Phases.DRAW_PHASE);
        DuelController.getInstance().getGame().setSelectedCard(callOfTheHaunted);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isActivatingSpellValid();
        Assertions.assertEquals("action not allowed in this phase\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setCurrentPhase(phase);
    }

    @Test
    public void errorForActivating4() {
        SpellAndTrapCard callOfTheHaunted = (SpellAndTrapCard) Card.getCardByName("Spell Absorption");
        callOfTheHaunted.setOwner(thisPlayer);
        callOfTheHaunted.setActive(true);
        Phases phase = DuelController.getInstance().getGame().getCurrentPhase();
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        DuelController.getInstance().getGame().setSelectedCard(callOfTheHaunted);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isActivatingSpellValid();
        Assertions.assertEquals("you have already activated this card\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setCurrentPhase(phase);
    }

    @Test
    public void errorForActivating5() {
        SpellAndTrapCard callOfTheHaunted = (SpellAndTrapCard) Card.getCardByName("Spell Absorption");
        callOfTheHaunted.setOwner(thisPlayer);
        callOfTheHaunted.setActive(false);
        Phases phase = DuelController.getInstance().getGame().getCurrentPhase();
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        ArrayList<Card> handBackUp = thisPlayer.getField().getHand();
        ArrayList<SpellAndTrapCard> spellAndTrapCards = thisPlayer.getField().getTrapAndSpell();
        thisPlayer.getField().setTrapAndSpell(new ArrayList<>());
        thisPlayer.getField().setHand(new ArrayList<>());
        for (int i = 0; i < 5; i++) {
            SpellAndTrapCard reki = (SpellAndTrapCard) Card.getCardByName("Raigeki");
            reki.setOwner(thisPlayer);
            thisPlayer.getField().getTrapAndSpell().add(reki);
        }
        thisPlayer.getField().getHand().add(callOfTheHaunted);
        DuelController.getInstance().getGame().setSelectedCard(callOfTheHaunted);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isActivatingSpellValid();
        Assertions.assertEquals("spell card zone is full\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setCurrentPhase(phase);
        thisPlayer.getField().setHand(handBackUp);
        thisPlayer.getField().setTrapAndSpell(spellAndTrapCards);
        DuelController.getInstance().getGame().setSelectedCard(null);
    }

    @Test
    public void activateTest() {
        SpellAndTrapCard inTheCloset = (SpellAndTrapCard) Card.getCardByName("Closed Forest");
        inTheCloset.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(inTheCloset);
        ArrayList<Card> handBackUp = thisPlayer.getField().getHand();
        SpellAndTrapCard field = thisPlayer.getField().getFieldZone();
        Phases phase = DuelController.getInstance().getGame().getCurrentPhase();
        thisPlayer.getField().setFieldZone(null);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().getHand().add(inTheCloset);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().activateSpell();
        Assertions.assertEquals("spell activated\r\n", outputStream.toString());
        thisPlayer.getField().setHand(handBackUp);
        thisPlayer.getField().setFieldZone(field);
        DuelController.getInstance().getGame().setCurrentPhase(phase);
    }

    @Test
    public void directAttackError1() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isDirectAttackValid();
        Assertions.assertEquals("no card is selected yet\r\n", outputStream.toString());
    }

    @Test
    public void directAttackError2() {
        SpellAndTrapCard inTheCloset = (SpellAndTrapCard) Card.getCardByName("Closed Forest");
        inTheCloset.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(inTheCloset);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isDirectAttackValid();
        Assertions.assertEquals("you can’t attack with this card\r\n", outputStream.toString());
    }

    @Test
    public void directAttackError3() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Wattkid");
        monsterCard.setOwner(thisPlayer);
        Phases phase = DuelController.getInstance().getGame().getCurrentPhase();
        DuelController.getInstance().getGame().setCurrentPhase(Phases.DRAW_PHASE);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> monsterCards = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isDirectAttackValid();
        Assertions.assertEquals("action not allowed in this phase\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setCurrentPhase(phase);
        thisPlayer.getField().setMonsterCards(monsterCards);
    }

    @Test
    public void directAttackError4() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Wattkid");
        monsterCard.setOwner(thisPlayer);
        monsterCard.setHasAttacked(true);
        Phases phase = DuelController.getInstance().getGame().getCurrentPhase();
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> monsterCards = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isDirectAttackValid();
        Assertions.assertEquals("this card already attacked\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setCurrentPhase(phase);
        thisPlayer.getField().setMonsterCards(monsterCards);
        monsterCard.setHasAttacked(false);
    }

    @Test
    public void directAttackError5() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Wattkid");
        monsterCard.setOwner(thisPlayer);
        MonsterCard monsterCard1 = (MonsterCard) Card.getCardByName("Wattkid");
        monsterCard.setOwner(thisPlayer);
        Phases phase = DuelController.getInstance().getGame().getCurrentPhase();
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> monsterCards = thisPlayer.getField().getMonsterCards();
        ArrayList<MonsterCard> monsterCards1 = theOtherPlayer.getField().getMonsterCards();
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        theOtherPlayer.getField().getMonsterCards().add(monsterCard1);
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isDirectAttackValid();
        Assertions.assertEquals("you can’t attack the opponent directly\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setCurrentPhase(phase);
        thisPlayer.getField().setMonsterCards(monsterCards);
        thisPlayer.getField().setMonsterCards(monsterCards1);
    }

    @Test
    public void directAttack() {
        theOtherPlayer.setLP(8000);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Wattkid");
        monsterCard.setOwner(thisPlayer);
        monsterCard.setHasAttacked(false);
        Phases phase = DuelController.getInstance().getGame().getCurrentPhase();
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> monsterCards = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().directAttack();
        Assertions.assertEquals("you opponent receives " + monsterCard.getThisCardAttackPower() + " battle damage\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setCurrentPhase(phase);
        thisPlayer.getField().setMonsterCards(monsterCards);
    }

    @Test
    public void attackError1() {
        DuelController.getInstance().getGame().setSelectedCard(null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("no card is selected yet\r\n", outputStream.toString());
    }

    @Test
    public void attackError2() {
        SpellAndTrapCard spellAndTrapCard = (SpellAndTrapCard) Card.getCardByName("Raigeki");
        DuelController.getInstance().getGame().setSelectedCard(spellAndTrapCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("you can’t attack with this card\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
    }

    @Test
    public void attackError3() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Slot Machine");
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("action not allowed in this phase\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void attackError4() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Slot Machine");
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        monsterCard.setHasAttacked(true);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("this card already attacked\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
        thisPlayer.getField().setMonsterCards(myMonsters);
        monsterCard.setHasAttacked(false);
    }

    @Test
    public void attackError5() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Slot Machine");
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        ArrayList<MonsterCard> opponentMonsters = theOtherPlayer.getField().getMonsterCards();
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("there is no card to attack here\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
        thisPlayer.getField().setMonsterCards(myMonsters);
        theOtherPlayer.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackError6() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Slot Machine");
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Spiral Serpent") ;
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        ArrayList<MonsterCard> opponentMonsters = theOtherPlayer.getField().getMonsterCards();
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        theOtherPlayer.getField().getMonsterCards().add(attacked);
        attacked.setCanBeRemoved(false);
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("you can’t attack this card\r\n", outputStream.toString());
        attacked.setCanBeRemoved(true);
        DuelController.getInstance().getGame().setSelectedCard(null);
        thisPlayer.getField().setMonsterCards(myMonsters);
        theOtherPlayer.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackError7() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Slot Machine");
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Spiral Serpent") ;
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        ArrayList<MonsterCard> opponentMonsters = theOtherPlayer.getField().getMonsterCards();
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        theOtherPlayer.getField().getMonsterCards().add(attacked);
        monsterCard.setCanAttack(false);
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("you can’t attack with this card\r\n", outputStream.toString());
        monsterCard.setCanAttack(true);
        DuelController.getInstance().getGame().setSelectedCard(null);
        thisPlayer.getField().setMonsterCards(myMonsters);
        theOtherPlayer.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackInAttackWinTest() {
        theOtherPlayer.setLP(8000);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Wattkid");
        MonsterCard attacker = (MonsterCard) Card.getCardByName("Baby dragon");
        attacked.reset();
        attacker.reset();
        attacked.setOwner(theOtherPlayer);
        attacker.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(attacker);
        attacked.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        ArrayList<MonsterCard> opponentMonsters = theOtherPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(attacker);
        theOtherPlayer.getField().getMonsterCards().add(attacked);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("your opponent’s monster is destroyed and your opponent receives 200 battle damage\r\n", outputStream.toString());
        thisPlayer.getField().setMonsterCards(myMonsters);
        theOtherPlayer.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackInAttackDrawTest() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Silver Fang");
        MonsterCard attacker = (MonsterCard) Card.getCardByName("Baby dragon");
        attacked.reset();
        attacker.reset();
        attacked.setOwner(theOtherPlayer);
        attacker.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(attacker);
        attacked.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        ArrayList<MonsterCard> opponentMonsters = theOtherPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(attacker);
        theOtherPlayer.getField().getMonsterCards().add(attacked);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("both you and your opponent monster cards are destroyed and no one receives damage\r\n", outputStream.toString());
        thisPlayer.getField().setMonsterCards(myMonsters);
        theOtherPlayer.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackInAttackLossTest() {
        thisPlayer.setLP(8000);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        MonsterCard attacker = (MonsterCard) Card.getCardByName("Wattkid");
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Baby dragon");
        attacked.reset();
        attacker.reset();
        attacked.setOwner(theOtherPlayer);
        attacker.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(attacker);
        attacked.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        ArrayList<MonsterCard> opponentMonsters = theOtherPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(attacker);
        theOtherPlayer.getField().getMonsterCards().add(attacked);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("Your monster card is destroyed and you received 200 battle damage\r\n", outputStream.toString());
        thisPlayer.getField().setMonsterCards(myMonsters);
        theOtherPlayer.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackInDefenseWinTest() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Wattkid");
        MonsterCard attacker = (MonsterCard) Card.getCardByName("Baby dragon");
        attacked.reset();
        attacker.reset();
        attacked.setOwner(theOtherPlayer);
        attacker.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(attacker);
        attacked.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_DOWN);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        ArrayList<MonsterCard> opponentMonsters = theOtherPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(attacker);
        theOtherPlayer.getField().getMonsterCards().add(attacked);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("opponent’s monster card was Wattkid and the defense position monster is destroyed\r\n", outputStream.toString());
        thisPlayer.getField().setMonsterCards(myMonsters);
        theOtherPlayer.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackInDefenseDrawTest() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Fireyarou");
        MonsterCard attacker = (MonsterCard) Card.getCardByName("Wattkid");
        attacked.reset();
        attacker.reset();
        attacked.setOwner(theOtherPlayer);
        attacker.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(attacker);
        attacked.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        ArrayList<MonsterCard> opponentMonsters = theOtherPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(attacker);
        theOtherPlayer.getField().getMonsterCards().add(attacked);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("no card is destroyed\r\n", outputStream.toString());
        thisPlayer.getField().setMonsterCards(myMonsters);
        theOtherPlayer.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackInDefenseLossTest() {
        thisPlayer.setLP(8000);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        MonsterCard attacker = (MonsterCard) Card.getCardByName("Wattkid");
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Axe Raider");
        attacked.reset();
        attacker.reset();
        attacked.setOwner(theOtherPlayer);
        attacker.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(attacker);
        attacked.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        ArrayList<MonsterCard> opponentMonsters = theOtherPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(attacker);
        theOtherPlayer.getField().getMonsterCards().add(attacked);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("no card is destroyed and you received 150 battle damage\r\n", outputStream.toString());
        thisPlayer.getField().setMonsterCards(myMonsters);
        theOtherPlayer.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void flipSummonError1() {
        DuelController.getInstance().getGame().setSelectedCard(null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().flipSummon();
        Assertions.assertEquals("no card is selected yet\r\n", outputStream.toString());
    }

    @Test
    public void flipSummonError2() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().flipSummon();
        Assertions.assertEquals("you can’t change this card's position\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
    }

    @Test
    public void flipSummonError3() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().flipSummon();
        Assertions.assertEquals("action not allowed in this phase\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void flipSummonError4() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.SECOND_MAIN_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().flipSummon();
        Assertions.assertEquals("you can’t flip summon this card\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void flipSummon() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.SECOND_MAIN_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_DOWN);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().flipSummon();
        Assertions.assertEquals(monsterCard.getMonsterCardModeInField(), MonsterCardModeInField.ATTACK_FACE_UP);
        DuelController.getInstance().getGame().setSelectedCard(null);
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void setPositionErrorTest1() {
        DuelController.getInstance().getGame().setSelectedCard(null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().setPosition(true);
        Assertions.assertEquals("no card is selected yet\r\n", outputStream.toString());
    }

    @Test
    public void setPositionErrorTest2() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().setPosition(true);
        Assertions.assertEquals("you can’t change this card's position\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
    }

    @Test
    public void setPositionErrorTest3() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().setPosition(true);
        Assertions.assertEquals("action not allowed in this phase\r\n", outputStream.toString());
        thisPlayer.getField().setMonsterCards(myMonsters);
        DuelController.getInstance().getGame().setSelectedCard(null);
    }

    @Test
    public void setPositionErrorTest4() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.SECOND_MAIN_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().setPosition(true);
        Assertions.assertEquals("this card is already in the wanted position\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void setPositionErrorTest5() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.SECOND_MAIN_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        monsterCard.setHasChangedPosition(true);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().setPosition(true);
        Assertions.assertEquals("you already changed this card position in this turn\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void setPositionErrorTest6() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.SECOND_MAIN_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        monsterCard.setHasChangedPosition(false);
        monsterCard.setHasBeenSetOrSummoned(true);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().setPosition(true);
        Assertions.assertEquals("you already summoned/set on this turn\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void setPosition() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.SECOND_MAIN_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        monsterCard.setHasChangedPosition(false);
        monsterCard.setHasBeenSetOrSummoned(false);
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        DuelController.getInstance().setPosition(true);
        Assertions.assertEquals(MonsterCardModeInField.ATTACK_FACE_UP, monsterCard.getMonsterCardModeInField());
        DuelController.getInstance().getGame().setSelectedCard(null);
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

}
