package yugioh;

import yugioh.controller.DuelController;
import yugioh.controller.ImportAndExport;
import yugioh.controller.MainController;
import yugioh.controller.ShopController;
import yugioh.model.*;
import yugioh.model.cards.Card;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;
import yugioh.model.cards.specialcards.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import yugioh.view.IO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class DuelTest {
    static Account thisPlayer = new Account("Bad Decisions", "The Strokes", "Why Do I Exist");
    static Account theOtherPlayer = new Account("Delilah", "Jessica", "Ricca");
    static SpellAndTrapCard card;

    static {
        try {
            card = (SpellAndTrapCard) ImportAndExport.getInstance().readCard("Dark Hole");
        } catch (Exception ignored) { }
    }

    @BeforeAll
    public static void setup() {
        ShopController.getInstance();
        MainController.getInstance().setLoggedIn(thisPlayer);
        var cardName = "Dark Hole";
        PlayerDeck playerDeck = new PlayerDeck("Damaged");
        for (int i = 0; i < 40; i++)
            playerDeck.addCardToMainDeck(cardName);
        thisPlayer.addDeck(playerDeck);
        theOtherPlayer.addDeck(playerDeck);
        thisPlayer.setActivePlayerDeck("Damaged");
        theOtherPlayer.setActivePlayerDeck("Damaged");
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
        thisPlayer.setAbleToDraw(true);
        DuelController.getInstance().drawPhase();
        Assertions.assertEquals(6, thisPlayer.getField().getHand().size());
        Assertions.assertEquals(39, thisPlayer.getField().getDeckZone().size());
    }

    @Test
    public void battlePhaseTest() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        thisPlayer.setAbleToAttack(false);
        DuelController.getInstance().battlePhase();
        Assertions.assertTrue(thisPlayer.isAbleToAttack());
        Assertions.assertEquals(Phases.SECOND_MAIN_PHASE, DuelController.getInstance().getGame().getCurrentPhase());
    }

    @Test
    public void resetTest() {
        card.setOwner(thisPlayer);
        card.setHasBeenUsedInThisTurn(true);
        thisPlayer.getField().getSpellAndTrapCards().add(card);
        DuelController.getInstance().endPhase();
        thisPlayer.getField().getSpellAndTrapCards().remove(card);
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
        int number = thisPlayer.getField().getSpellAndTrapCards().size() + 1;
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
        // TODO: 6/20/2021 Mfing allCards
        theOtherPlayer.getField().getMonsterCards().add((MonsterCard) Card.getCardByName("Baby dragon"));
        MonsterCard barbaros = (MonsterCard) Card.getCardByName("Beast King Barbaros");
        DuelController.getInstance().getGame().setSelectedCard(barbaros);
        ArrayList<Card> backupCards = thisPlayer.getAllCardsArrayList();
        thisPlayer.setAllCardsArrayList(new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            ShopController.getInstance().buyCard("Baby dragon");
            thisPlayer.getField().getMonsterCards().add((MonsterCard) thisPlayer.getAllCardsArrayList().get(0));
        }
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n0\r\n0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().barbaros(3);
        System.setIn(backup);
        thisPlayer.setAllCardsArrayList(backupCards);
        Assertions.assertEquals(0, theOtherPlayer.getField().getMonsterCards().size());
    }

    @Test
    public void gateGuardianTest() {
        // TODO: 6/20/2021 Mfing allCards again.
        ShopController.getInstance().buyCard("Gate Guardian");
        MonsterCard gateGuardian = (MonsterCard) Card.getCardByName("Gate Guardian");
        gateGuardian.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(gateGuardian);
        ArrayList<Card> backupCards = thisPlayer.getAllCardsArrayList();
        thisPlayer.setAllCardsArrayList(new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            ShopController.getInstance().buyCard("Baby dragon");
            thisPlayer.getField().getMonsterCards().add((MonsterCard) thisPlayer.getAllCardsArrayList().get(0));
        }
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n0\r\n0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().gateGuardian();
        System.setIn(backup);
        thisPlayer.setAllCardsArrayList(backupCards);
        Assertions.assertNull(DuelController.getInstance().getGame().getSelectedCard());
    }

    @Test
    public void theTrickyTest() {
        // TODO: 6/20/2021 Same problem with this mfing allCards. also we don't check if the number we give is a monster card or spell and trap card. should we check it for the number that the player gives in the middle of a game?
        MonsterCard theTricky = (MonsterCard) Card.getCardByName("The Tricky");
        theTricky.setOwner(thisPlayer);
        ArrayList<Card> backupCards = thisPlayer.getAllCardsArrayList();
        ArrayList<Card> hand = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.setAllCardsArrayList(new ArrayList<>());
        DuelController.getInstance().getGame().setSelectedCard(theTricky);
        ShopController.getInstance().buyCard("Baby dragon");
        thisPlayer.getField().getHand().add(thisPlayer.getAllCardsArrayList().get(0));
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().theTricky();
        System.setIn(backup);
        thisPlayer.setAllCardsArrayList(backupCards);
        thisPlayer.getField().setHand(hand);
        Assertions.assertNull(DuelController.getInstance().getGame().getSelectedCard());
    }

    @Test
    public void heraldOfCreationTest() {
        MonsterCard spiralSerpent = (MonsterCard) Card.getCardByName("Spiral Serpent");
        spiralSerpent.setOwner(thisPlayer);
        ArrayList<Card> graveyardBackUp = thisPlayer.getField().getGraveyard();
        ArrayList<Card> backupCards = thisPlayer.getAllCardsArrayList();
        ArrayList<Card> hand = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.setAllCardsArrayList(new ArrayList<>());
        thisPlayer.getField().setGraveyard(new ArrayList<>());
        thisPlayer.getField().getGraveyard().add(spiralSerpent);
        ShopController.getInstance().buyCard("Baby dragon");
        // TODO: 6/20/2021 Shit got pretty complicated since I used a HashMap<String, Short> instead of ArrayList<Card> for allCards. Change tests and then remove the mfing ArrayList.
        thisPlayer.getField().getHand().add(thisPlayer.getAllCardsArrayList().get(0));
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().heraldOfCreation();
        System.setIn(backup);
        boolean hasCard = thisPlayer.getField().getHand().contains(spiralSerpent);
        thisPlayer.setAllCardsArrayList(backupCards);
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
        ArrayList<SpellAndTrapCard> spellAndTrapCards = thisPlayer.getField().getSpellAndTrapCards();
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
        thisPlayer.getField().getSpellAndTrapCards().add(spellAndTrapCard);
        thisPlayer.getField().getDeckZone().add(monsterCard);
        DuelController.getInstance().handleSupplySquad(toDie, thisPlayer.getField());
        Assertions.assertTrue(thisPlayer.getField().getHand().contains(monsterCard));
        thisPlayer.getField().setMonsterCards(monsterCards);
        thisPlayer.getField().setGraveyard(myGY);
        thisPlayer.getField().setDeckZone(deck);
        thisPlayer.getField().setSpellAndTrapCards(spellAndTrapCards);
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
        Assertions.assertEquals(("Dark Hole" + System.lineSeparator()).repeat(DuelController.getInstance().getGame().getCurrentPlayer().getField().getDeckZone().size()), outputStream.toString());
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
        Assertions.assertEquals("Spell Absorption: Each time a Spell Card is activated," +
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
        Assertions.assertEquals("""
                enter the number of the ritual card you choose:\r
                enter the numbers of the card you want to tribute divided by a space:\r
                choose the monster mode (Attack or Defense):\r
                summoned successfully\r
                """, outputStream.toString());
        thisPlayer.getField().setHand(handBackUp);
        thisPlayer.getField().setMonsterCards(backUpCards);
    }

    @Test
    public void setMonsterTest() {
        // TODO: 6/20/2021 WTF does happen here? I think hand is empty for some reason. same with drawTest and attackInAttackDrawTest.
        MonsterCard silverFag = (MonsterCard) Card.getCardByName("Silver Fang");
        silverFag.setOwner(thisPlayer);
        ArrayList<Card> hand = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().getHand().add(silverFag);
        DuelController.getInstance().getGame().setSelectedCard(silverFag);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        ArrayList<MonsterCard> monsterBackUp = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        DuelController.getInstance().getGame().setSummonedInThisTurn(false);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().set();
        Assertions.assertEquals("set successfully\r\n", outputStream.toString());
        thisPlayer.getField().setHand(hand);
        thisPlayer.getField().setMonsterCards(monsterBackUp);
    }

    @Test
    public void setTrapSpellTest() {
        // TODO: 6/20/2021 Map seems weird. Is it ok?
        SpellAndTrapCard vanity = (SpellAndTrapCard) Card.getCardByName("Vanity's Emptiness");
        vanity.setOwner(thisPlayer);
        ArrayList<Card> hand = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().getHand().add(vanity);
        DuelController.getInstance().getGame().setSelectedCard(vanity);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = thisPlayer.getField().getSpellAndTrapCards();
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        DuelController.getInstance().getGame().setSummonedInThisTurn(false);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().set();
        Assertions.assertEquals("set successfully\r\n", outputStream.toString());
        thisPlayer.getField().setHand(hand);
        thisPlayer.getField().setSpellAndTrapCards(spellAndTrapCards);
    }

    @Test
    public void monsterRebornTest() {
        SpellAndTrapCard monsterReborn = (SpellAndTrapCard) Card.getCardByName("Monster Reborn");
        monsterReborn.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> spellZone = thisPlayer.getField().getSpellAndTrapCards();
        ArrayList<Card> GY = thisPlayer.getField().getGraveyard();
        MonsterCard marshmallon = (MonsterCard) Card.getCardByName("Marshmallon");
        marshmallon.setOwner(thisPlayer);
        thisPlayer.getField().setGraveyard(new ArrayList<>());
        thisPlayer.getField().getGraveyard().add(marshmallon);
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().getSpellAndTrapCards().add(monsterReborn);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().monsterReborn(monsterReborn);
        Assertions.assertTrue(thisPlayer.getField().getGraveyard().contains(monsterReborn));
        thisPlayer.getField().setSpellAndTrapCards(spellZone);
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
        ArrayList<SpellAndTrapCard> spellAndTrapCards = thisPlayer.getField().getSpellAndTrapCards();
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
        thisPlayer.getField().setSpellAndTrapCards(spellAndTrapCards);
    }

    @Test
    public void harpiesFeatherDusterTest() {
        SpellAndTrapCard harpie = (SpellAndTrapCard) Card.getCardByName("Harpie's Feather Duster");
        harpie.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> spellCards = theOtherPlayer.getField().getSpellAndTrapCards();
        ArrayList<Card> myGY = thisPlayer.getField().getGraveyard();
        ArrayList<SpellAndTrapCard> spellAndTrapCards = thisPlayer.getField().getSpellAndTrapCards();
        SpellAndTrapCard solemnWarning = (SpellAndTrapCard) Card.getCardByName("Solemn Warning");
        solemnWarning.setOwner(theOtherPlayer);
        SpellAndTrapCard timeSeal = (SpellAndTrapCard) Card.getCardByName("Time Seal");
        timeSeal.setOwner(theOtherPlayer);
        theOtherPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        theOtherPlayer.getField().getSpellAndTrapCards().add(solemnWarning);
        theOtherPlayer.getField().getSpellAndTrapCards().add(timeSeal);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("no\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().harpiesFeatherDuster(harpie);
        Assertions.assertTrue(theOtherPlayer.getField().getSpellAndTrapCards().isEmpty());
        theOtherPlayer.getField().setSpellAndTrapCards(spellCards);
        thisPlayer.getField().setGraveyard(myGY);
        thisPlayer.getField().setSpellAndTrapCards(spellAndTrapCards);
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
        ArrayList<SpellAndTrapCard> spellAndTrapCards = thisPlayer.getField().getSpellAndTrapCards();
        ArrayList<Card> GY = thisPlayer.getField().getGraveyard();
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().getSpellAndTrapCards().add(umiiruka);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().mysticalSpaceTyphoon(mystical);
        Assertions.assertTrue(thisPlayer.getField().getSpellAndTrapCards().isEmpty());
        thisPlayer.getField().setSpellAndTrapCards(spellAndTrapCards);
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
        ArrayList<SpellAndTrapCard> thisPlayerSpells = thisPlayer.getField().getSpellAndTrapCards();
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().getSpellAndTrapCards().add(twinTwister);
        ArrayList<Card> hand = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().getHand().add(cardFromHand);
        ArrayList<SpellAndTrapCard> opponentSpells = theOtherPlayer.getField().getSpellAndTrapCards();
        theOtherPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        theOtherPlayer.getField().getSpellAndTrapCards().add(toRemove);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("no\r\nno\r\n0\r\n1\r\nno\r\n0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().twinTwisters(twinTwister);
        Assertions.assertFalse(theOtherPlayer.getField().getSpellAndTrapCards().contains(toRemove));
        thisPlayer.getField().setSpellAndTrapCards(thisPlayerSpells);
        thisPlayer.getField().setHand(hand);
        theOtherPlayer.getField().setSpellAndTrapCards(opponentSpells);
    }

    @Test
    public void mirrorForceTest() {
        SpellAndTrapCard mirrorForce = (SpellAndTrapCard) Card.getCardByName("Mirror Force");
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Curtain of the dark ones");
        mirrorForce.setOwner(thisPlayer);
        monsterCard.setOwner(theOtherPlayer);
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getSpellAndTrapCards();
        ArrayList<MonsterCard> opponentMonsters = theOtherPlayer.getField().getMonsterCards();
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().getSpellAndTrapCards().add(mirrorForce);
        theOtherPlayer.getField().setMonsterCards(new ArrayList<>());
        theOtherPlayer.getField().getMonsterCards().add(monsterCard);
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        DuelController.getInstance().mirrorForce(mirrorForce, theOtherPlayer);
        Assertions.assertFalse(theOtherPlayer.getField().getMonsterCards().contains(monsterCard));
        thisPlayer.getField().setSpellAndTrapCards(mySpells);
        theOtherPlayer.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void magicCylinderTest() {
        SpellAndTrapCard magicCylinder = (SpellAndTrapCard) Card.getCardByName("Magic Cylinder");
        MonsterCard attacker = (MonsterCard) Card.getCardByName("Feral Imp");
        magicCylinder.setOwner(thisPlayer);
        attacker.setOwner(theOtherPlayer);
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getSpellAndTrapCards();
        ArrayList<MonsterCard> opponentMonsters = theOtherPlayer.getField().getMonsterCards();
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().getSpellAndTrapCards().add(magicCylinder);
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
        thisPlayer.getField().setSpellAndTrapCards(mySpells);
        theOtherPlayer.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void swordOfDarkDestruction() {
        SpellAndTrapCard swordOfDarkDestruction = (SpellAndTrapCard) Card.getCardByName("Sword of dark destruction");
        MonsterCard hornImp = (MonsterCard) Card.getCardByName("Horn Imp");
        swordOfDarkDestruction.setOwner(thisPlayer);
        hornImp.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getSpellAndTrapCards();
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getSpellAndTrapCards().add(swordOfDarkDestruction);
        thisPlayer.getField().getMonsterCards().add(hornImp);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().equipMonster(swordOfDarkDestruction);
        Assertions.assertEquals(400 + hornImp.getClassAttackPower(), hornImp.getThisCardAttackPower());
        hornImp.reset();
        thisPlayer.getField().setSpellAndTrapCards(mySpells);
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void blackPendantTest() {
        SpellAndTrapCard blackPendant = (SpellAndTrapCard) Card.getCardByName("Black Pendant");
        MonsterCard hornImp = (MonsterCard) Card.getCardByName("Horn Imp");
        blackPendant.setOwner(thisPlayer);
        hornImp.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getSpellAndTrapCards();
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getSpellAndTrapCards().add(blackPendant);
        thisPlayer.getField().getMonsterCards().add(hornImp);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().equipMonster(blackPendant);
        Assertions.assertEquals(500 + hornImp.getClassAttackPower(), hornImp.getThisCardAttackPower());
        hornImp.reset();
        thisPlayer.getField().setSpellAndTrapCards(mySpells);
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void unitedWeStandTest() {
        SpellAndTrapCard unitedWeStand = (SpellAndTrapCard) Card.getCardByName("United We Stand");
        MonsterCard hornImp = (MonsterCard) Card.getCardByName("Horn Imp");
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Dark Blade");
        unitedWeStand.setOwner(thisPlayer);
        hornImp.setOwner(thisPlayer);
        monsterCard.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getSpellAndTrapCards();
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getSpellAndTrapCards().add(unitedWeStand);
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
        thisPlayer.getField().setSpellAndTrapCards(mySpells);
        thisPlayer.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void magnumShield() {
        SpellAndTrapCard magnumShield = (SpellAndTrapCard) Card.getCardByName("Magnum Shield");
        MonsterCard darkBlade = (MonsterCard) Card.getCardByName("Dark Blade");
        magnumShield.setOwner(thisPlayer);
        darkBlade.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getSpellAndTrapCards();
        ArrayList<MonsterCard> myMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getSpellAndTrapCards().add(magnumShield);
        thisPlayer.getField().getMonsterCards().add(darkBlade);
        darkBlade.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().equipMonster(magnumShield);
        Assertions.assertEquals(darkBlade.getClassAttackPower() + darkBlade.getClassDefensePower(), darkBlade.getThisCardAttackPower());
        thisPlayer.getField().setSpellAndTrapCards(mySpells);
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
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getSpellAndTrapCards();
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().getSpellAndTrapCards().add(mindCrush);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\nClosed Forest\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().mindCrush(mindCrush, theOtherPlayer);
        theOtherPlayer.getField().setHand(opponentHand);
        thisPlayer.getField().setSpellAndTrapCards(mySpells);
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
        ArrayList<SpellAndTrapCard> opponentSpells = theOtherPlayer.getField().getSpellAndTrapCards();
        theOtherPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        theOtherPlayer.getField().getSpellAndTrapCards().add(torrential);
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
        theOtherPlayer.getField().setSpellAndTrapCards(opponentSpells);
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
        Assertions.assertFalse(theOtherPlayer.isAbleToDraw());
        theOtherPlayer.setAbleToDraw(true);
    }

    @Test
    public void magicJamamerTest() {
        // TODO: 6/20/2021 Empty hand again
        SpellAndTrapCard spellAndTrapCard = (SpellAndTrapCard) Card.getCardByName("Umiiruka");
        spellAndTrapCard.setOwner(thisPlayer);
        SpellAndTrapCard magicJamamer = (SpellAndTrapCard) Card.getCardByName("Magic Jamamer");
        magicJamamer.setOwner(theOtherPlayer);
        Card toRemove = Card.getCardByName("Fireyarou");
        toRemove.setOwner(theOtherPlayer);
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getSpellAndTrapCards();
        ArrayList<SpellAndTrapCard> opponentSpells = theOtherPlayer.getField().getSpellAndTrapCards();
        ArrayList<Card> opponentHand = theOtherPlayer.getField().getHand();
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().getSpellAndTrapCards().add(spellAndTrapCard);
        theOtherPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        theOtherPlayer.getField().getSpellAndTrapCards().add(magicJamamer);
        theOtherPlayer.getField().setHand(new ArrayList<>());
        theOtherPlayer.getField().getHand().add(toRemove);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().magicJamamer(spellAndTrapCard);
        Assertions.assertTrue(thisPlayer.getField().getGraveyard().contains(spellAndTrapCard));
        thisPlayer.getField().setSpellAndTrapCards(mySpells);
        theOtherPlayer.getField().setSpellAndTrapCards(opponentSpells);
        theOtherPlayer.getField().setHand(opponentHand);
    }

    @Test
    public void solemnWarningTest() {
        theOtherPlayer.setLP(8000);
        SpellAndTrapCard solemnWarning = (SpellAndTrapCard) Card.getCardByName("Solemn Warning");
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Exploder Dragon");
        solemnWarning.setOwner(theOtherPlayer);
        monsterCard.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> opponentSpells = theOtherPlayer.getField().getSpellAndTrapCards();
        ArrayList<Card> myHand = thisPlayer.getField().getHand();
        theOtherPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().setHand(new ArrayList<>());
        theOtherPlayer.getField().getSpellAndTrapCards().add(solemnWarning);
        thisPlayer.getField().getHand().add(monsterCard);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().solemnWarning(monsterCard);
        Assertions.assertEquals(6000, theOtherPlayer.getLP());
        theOtherPlayer.getField().setSpellAndTrapCards(opponentSpells);
        thisPlayer.getField().setHand(myHand);
    }

    @Test
    public void callOfTheHauntedTest() {
        SpellAndTrapCard callOfTheHaunted = (SpellAndTrapCard) Card.getCardByName("Call of The Haunted");
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Wattkid");
        callOfTheHaunted.setOwner(thisPlayer);
        monsterCard.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> mySpells = thisPlayer.getField().getSpellAndTrapCards();
        ArrayList<Card> myGY = thisPlayer.getField().getGraveyard();
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().setGraveyard(new ArrayList<>());
        thisPlayer.getField().getSpellAndTrapCards().add(callOfTheHaunted);
        thisPlayer.getField().getGraveyard().add(monsterCard);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().callOfTheHaunted(callOfTheHaunted, thisPlayer.getField(), true);
        Assertions.assertTrue(thisPlayer.getField().getMonsterCards().contains(monsterCard));
        thisPlayer.getField().setSpellAndTrapCards(mySpells);
        thisPlayer.getField().setGraveyard(myGY);
    }

    @Test
    public void spellAbsorptionTest() {
        SpellAndTrapCard callOfTheHaunted = (SpellAndTrapCard) Card.getCardByName("Spell Absorption");
        callOfTheHaunted.setOwner(thisPlayer);
        callOfTheHaunted.setActive(true);
        int LP = thisPlayer.getLP();
        thisPlayer.setLP(8000);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = thisPlayer.getField().getSpellAndTrapCards();
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().getSpellAndTrapCards().add(callOfTheHaunted);
        DuelController.getInstance().selfAbsorption();
        Assertions.assertEquals(8500, thisPlayer.getLP());
        thisPlayer.setLP(LP);
        thisPlayer.getField().setSpellAndTrapCards(spellAndTrapCards);
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
        ArrayList<SpellAndTrapCard> spellAndTrapCards = thisPlayer.getField().getSpellAndTrapCards();
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().setHand(new ArrayList<>());
        for (int i = 0; i < 5; i++) {
            SpellAndTrapCard reki = (SpellAndTrapCard) Card.getCardByName("Raigeki");
            reki.setOwner(thisPlayer);
            thisPlayer.getField().getSpellAndTrapCards().add(reki);
        }
        thisPlayer.getField().getHand().add(callOfTheHaunted);
        DuelController.getInstance().getGame().setSelectedCard(callOfTheHaunted);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isActivatingSpellValid();
        Assertions.assertEquals("spell card zone is full\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setCurrentPhase(phase);
        thisPlayer.getField().setHand(handBackUp);
        thisPlayer.getField().setSpellAndTrapCards(spellAndTrapCards);
        DuelController.getInstance().getGame().setSelectedCard(null);
    }

    @Test
    public void activateTest() {
        // TODO: 6/20/2021 Actually this one might have a real problem, but I don't have the brain to find out
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
        monsterCard.setAttacked(true);
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
        monsterCard.setAttacked(false);
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
        // TODO: 6/20/2021 Again with the empty hand.
        theOtherPlayer.setLP(8000);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Wattkid");
        monsterCard.setOwner(thisPlayer);
        monsterCard.setAttacked(false);
        Phases phase = DuelController.getInstance().getGame().getCurrentPhase();
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> monsterCards = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().directAttack();
        Assertions.assertEquals("you opponent receives " + monsterCard.getThisCardAttackPower() + " battle damage\r\n" +
                "Ricca: 7000\n" +
                "\tc \tc \tc \tc \tc \n" +
                "35\n" +
                "\tE \tE \tE \tE \tE \n" +
                "\tE \tE \tE \tE \tE \n" +
                "0\t\t\t\t\t\tE \n" +
                "\n" +
                "--------------------------\n" +
                "\n" +
                "E \t\t\t\t\t\t0\n" +
                "\tE \tE \tDH\tE \tE \n" +
                "\tE \tE \tE \tE \tE \n" +
                "  \t\t\t\t\t\t35\n" +
                "c \tc \tc \tc \tc \t\n" +
                "Why Do I Exist: 8000\r\n", outputStream.toString());
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
        monsterCard.setAttacked(true);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("this card already attacked\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
        thisPlayer.getField().setMonsterCards(myMonsters);
        monsterCard.setAttacked(false);
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
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Spiral Serpent");
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
        attacked.setAbleToBeRemoved(false);
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("you can’t attack this card\r\n", outputStream.toString());
        attacked.setAbleToBeRemoved(true);
        DuelController.getInstance().getGame().setSelectedCard(null);
        thisPlayer.getField().setMonsterCards(myMonsters);
        theOtherPlayer.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackError7() {
        // TODO: 6/20/2021 This one needs actual controller debugging
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Slot Machine");
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Spiral Serpent");
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
        monsterCard.setAbleToAttack(false);
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("you can’t attack with this card\r\n", outputStream.toString());
        monsterCard.setAbleToAttack(true);
        DuelController.getInstance().getGame().setSelectedCard(null);
        thisPlayer.getField().setMonsterCards(myMonsters);
        theOtherPlayer.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackInAttackWinTest() {
        // TODO: 6/20/2021 Empty hand
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
        Assertions.assertEquals("Your monster card is destroyed and you received 200 battle damage" + System.lineSeparator() +
                "Ricca: 8000" + System.lineSeparator() +
                "\tc \tc \tc \tc \tc " + System.lineSeparator() +
                "35" + System.lineSeparator() +
                "\tE \tE \tE \tE \tE " + System.lineSeparator() +
                "\tE \tE \tOO\tE \tE " + System.lineSeparator() +
                "0\t\t\t\t\t\tE " + System.lineSeparator() +
                System.lineSeparator() +
                "--------------------------" + System.lineSeparator() +
                System.lineSeparator() +
                "E \t\t\t\t\t\t1" + System.lineSeparator() +
                "\tE \tE \tE \tE \tE " + System.lineSeparator() +
                "\tE \tE \tE \tE \tE " + System.lineSeparator() +
                "  \t\t\t\t\t\t35" + System.lineSeparator() +
                "c \tc \tc \tc \tc \t" + System.lineSeparator() +
                "Why Do I Exist: 7800" + System.lineSeparator(), outputStream.toString());
        thisPlayer.getField().setMonsterCards(myMonsters);
        theOtherPlayer.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackInDefenseWinTest() {
        // TODO: 6/20/2021 Map once again
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
        // TODO: 6/20/2021 Again just map
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
        // TODO: 6/20/2021 Empty hand
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
        monsterCard.setChangedPosition(true);
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
        monsterCard.setChangedPosition(false);
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
        monsterCard.setChangedPosition(false);
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

    @Test
    public void errorForSummon1() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isSummonValid();
        Assertions.assertEquals("no card is selected yet\r\n", outputStream.toString());
    }

    @Test
    public void errorForSummon2() {
        SpellAndTrapCard blackPendant = (SpellAndTrapCard) Card.getCardByName("Black Pendant");
        blackPendant.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(blackPendant);
        ArrayList<Card> backUpHand = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isSummonValid();
        Assertions.assertEquals("you can’t summon this card\r\n", outputStream.toString());
        thisPlayer.getField().setHand(backUpHand);
        DuelController.getInstance().getGame().setSelectedCard(null);
    }

    @Test
    public void errorForSummon3() {
        SpellAndTrapCard blackPendant = (SpellAndTrapCard) Card.getCardByName("Black Pendant");
        blackPendant.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(blackPendant);
        ArrayList<Card> backUpHand = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().getHand().add(blackPendant);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isSummonValid();
        Assertions.assertEquals("you can’t summon this card\r\n", outputStream.toString());
        thisPlayer.getField().setHand(backUpHand);
        DuelController.getInstance().getGame().setSelectedCard(null);
    }

    @Test
    public void errorForSummon4() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.END_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        monsterCard.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<Card> backUpHand = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().getHand().add(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isSummonValid();
        Assertions.assertEquals("action not allowed in this phase\r\n", outputStream.toString());
        thisPlayer.getField().setHand(backUpHand);
        DuelController.getInstance().getGame().setSelectedCard(null);
    }

    @Test
    public void errorForSummon5() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Crab Turtle");
        monsterCard.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<Card> backUpHand = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().getHand().add(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isSummonValid();
        Assertions.assertEquals("you can’t summon this card\r\n", outputStream.toString());
        thisPlayer.getField().setHand(backUpHand);
        DuelController.getInstance().getGame().setSelectedCard(null);
    }

    @Test
    public void errorForSummon6() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        monsterCard.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<Card> backUpHand = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().getHand().add(monsterCard);
        ArrayList<MonsterCard> backUpMonsters = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        for (int i = 0; i < 5; i++) {
            MonsterCard monster = (MonsterCard) Card.getCardByName("Axe Raider");
            monster.setOwner(thisPlayer);
            thisPlayer.getField().getMonsterCards().add(monster);

        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isSummonValid();
        Assertions.assertEquals("monster card zone is full\r\n", outputStream.toString());
        thisPlayer.getField().setHand(backUpHand);
        DuelController.getInstance().getGame().setSelectedCard(null);
        thisPlayer.getField().setMonsterCards(backUpMonsters);
    }

    @Test
    public void tributeValidationTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isTributeValid(6);
        Assertions.assertEquals("there no monsters one this address\r\n", outputStream.toString());
    }

    @Test
    public void tributeTest() {
        MonsterCard slotMachine = (MonsterCard) Card.getCardByName("Slot Machine");
        slotMachine.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSelectedCard(slotMachine);
        MonsterCard warriorDaiGrepher = (MonsterCard) Card.getCardByName("Warrior Dai Grepher");
        MonsterCard yomi = (MonsterCard) Card.getCardByName("Yomi Ship");
        warriorDaiGrepher.setOwner(thisPlayer);
        yomi.setOwner(thisPlayer);
        ArrayList<MonsterCard> monsterCards = thisPlayer.getField().getMonsterCards();
        ArrayList<Card> GY = thisPlayer.getField().getGraveyard();
        ArrayList<Card> handBackUp = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().getHand().add(slotMachine);
        thisPlayer.getField().setGraveyard(new ArrayList<>());
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(yomi);
        thisPlayer.getField().getMonsterCards().add(warriorDaiGrepher);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().summonWithTribute();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        Assertions.assertTrue(thisPlayer.getField().getGraveyard().contains(yomi) &&
                thisPlayer.getField().getGraveyard().contains(warriorDaiGrepher));
        thisPlayer.getField().setMonsterCards(monsterCards);
        thisPlayer.getField().setGraveyard(GY);
        thisPlayer.getField().setHand(handBackUp);
        DuelController.getInstance().getGame().setSelectedCard(null);
    }

    @Test
    public void trapHoleTest() {
        MonsterCard silverFag = (MonsterCard) Card.getCardByName("Silver Fang");
        DuelController.getInstance().getGame().setSelectedCard(silverFag);
        silverFag.setOwner(thisPlayer);
        SpellAndTrapCard trapHole = (SpellAndTrapCard) Card.getCardByName("Trap Hole");
        trapHole.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = theOtherPlayer.getField().getSpellAndTrapCards();
        ArrayList<Card> handBackUp = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(handBackUp);
        thisPlayer.getField().getHand().add(trapHole);
        theOtherPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        theOtherPlayer.getField().getSpellAndTrapCards().add(trapHole);
        ArrayList<MonsterCard> monsterCards = thisPlayer.getField().getMonsterCards();
        ArrayList<Card> GY = thisPlayer.getField().getGraveyard();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(silverFag);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\nno\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().handleTrapHole(silverFag);
        Assertions.assertNull(DuelController.getInstance().getGame().getSelectedCard());
        System.setIn(backup);
        thisPlayer.getField().setGraveyard(GY);
        thisPlayer.getField().setMonsterCards(monsterCards);
        theOtherPlayer.getField().setSpellAndTrapCards(spellAndTrapCards);
        thisPlayer.getField().setHand(handBackUp);
    }

    @Test
    public void summonTest() {
        MonsterCard fireyarou = (MonsterCard) Card.getCardByName("Fireyarou");
        fireyarou.setOwner(thisPlayer);
        DuelController.getInstance().getGame().setSummonedInThisTurn(false);
        DuelController.getInstance().getGame().setSelectedCard(fireyarou);
        ArrayList<Card> handBackUp = thisPlayer.getField().getHand();
        ArrayList<MonsterCard> monsterCardsBackUp = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().getHand().add(fireyarou);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        DuelController.getInstance().summon();
        Assertions.assertTrue(DuelController.getInstance().getGame().isSummonedInThisTurn());
        thisPlayer.getField().setHand(handBackUp);
        thisPlayer.getField().setMonsterCards(monsterCardsBackUp);
        DuelController.getInstance().getGame().setSelectedCard(null);
        DuelController.getInstance().getGame().setSummonedInThisTurn(false);
    }

    @Test
    public void changePhaseDrawTest() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.DRAW_PHASE);
        DuelController.getInstance().nextPhase();
        Assertions.assertEquals(Phases.STANDBY_PHASE, DuelController.getInstance().getGame().getCurrentPhase());
    }

    @Test
    public void changePhaseStandByTest() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.STANDBY_PHASE);
        DuelController.getInstance().nextPhase();
        Assertions.assertEquals(Phases.FIRST_MAIN_PHASE, DuelController.getInstance().getGame().getCurrentPhase());
    }

    @Test
    public void changePhaseBattleTest() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        DuelController.getInstance().nextPhase();
        Assertions.assertEquals(Phases.SECOND_MAIN_PHASE, DuelController.getInstance().getGame().getCurrentPhase());
    }

    @Test
    public void changePhaseFirstTest() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        DuelController.getInstance().nextPhase();
        Assertions.assertEquals(Phases.BATTLE_PHASE, DuelController.getInstance().getGame().getCurrentPhase());
    }

    @Test
    public void changePhaseEndTest() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.END_PHASE);
        ArrayList<Card> myCards = thisPlayer.getField().getHand();
        thisPlayer.getField().setHand(new ArrayList<>());
        ArrayList<Card> opponentCards = theOtherPlayer.getField().getHand();
        theOtherPlayer.getField().setHand(new ArrayList<>());
        DuelController.getInstance().nextPhase();
        Assertions.assertEquals(Phases.DRAW_PHASE, DuelController.getInstance().getGame().getCurrentPhase());
        DuelController.getInstance().getGame().setCurrentPlayer(thisPlayer);
        DuelController.getInstance().getGame().setTheOtherPlayer(theOtherPlayer);
        thisPlayer.getField().setHand(myCards);
        theOtherPlayer.getField().setHand(opponentCards);
    }

    @Test
    public void messengerOfPeaceTest() {
        int LP = thisPlayer.getLP();
        thisPlayer.setLP(8000);
        MessengerOfPeace messengerOfPiss = new MessengerOfPeace();
        messengerOfPiss.setOwner(thisPlayer);
        messengerOfPiss.setActive(true);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = thisPlayer.getField().getSpellAndTrapCards();
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().getSpellAndTrapCards().add(messengerOfPiss);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("no\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().handleMessengerOfPeace();
        Assertions.assertEquals(7900, thisPlayer.getLP());
        System.setIn(backup);
        thisPlayer.getField().setSpellAndTrapCards(spellAndTrapCards);
        thisPlayer.setLP(LP);
    }

    @Test
    public void handleHeraldOfCreationTest() {
        MonsterCard heraldOfCreation = (MonsterCard) Card.getCardByName("Herald of Creation");
        heraldOfCreation.setOwner(thisPlayer);
        ArrayList<MonsterCard> monsterCards = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(heraldOfCreation);
        heraldOfCreation.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        ArrayList<Card> hand = thisPlayer.getField().getHand();
        MonsterCard slutMachine = (MonsterCard) Card.getCardByName("Slot Machine");
        thisPlayer.getField().setHand(new ArrayList<>());
        thisPlayer.getField().getHand().add(slutMachine);
        MonsterCard hornymp = (MonsterCard) Card.getCardByName("Horn Imp");
        ArrayList<Card> GY = thisPlayer.getField().getGraveyard();
        thisPlayer.getField().setGraveyard(new ArrayList<>());
        thisPlayer.getField().getGraveyard().add(hornymp);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().handleCommandKnightAndHeraldOfCreation(thisPlayer.getField(), theOtherPlayer.getField());
        Assertions.assertTrue(heraldOfCreation.isHasBeenUsedInThisTurn());
        System.setIn(backup);
        heraldOfCreation.setHasBeenUsedInThisTurn(false);
        thisPlayer.getField().setMonsterCards(monsterCards);
        thisPlayer.getField().setHand(hand);
        thisPlayer.getField().setGraveyard(GY);
    }

    @Test
    public void handleCommandKnightTest() {
        CommandKnight commandKnight = (CommandKnight) Card.getCardByName("Command Knight");
        commandKnight.setOwner(thisPlayer);
        commandKnight.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        ArrayList<MonsterCard> monsterCards = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        thisPlayer.getField().getMonsterCards().add(commandKnight);
        MonsterCard slutMachine = (MonsterCard) Card.getCardByName("Slot Machine");
        thisPlayer.getField().getMonsterCards().add(slutMachine);
        DuelController.getInstance().handleCommandKnightAndHeraldOfCreation(thisPlayer.getField(), theOtherPlayer.getField());
        Assertions.assertEquals(1400, commandKnight.getThisCardAttackPower());
        thisPlayer.getField().setMonsterCards(monsterCards);
    }

    @Test
    public void swordOfRevealingLightTest() {
        SwordsOfRevealingLight swordsOfRevealingLight = (SwordsOfRevealingLight) Card.getCardByName("Swords of Revealing Light");
        swordsOfRevealingLight.setOwner(theOtherPlayer);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = theOtherPlayer.getField().getSpellAndTrapCards();
        ArrayList<MonsterCard> monsterCards = thisPlayer.getField().getMonsterCards();
        thisPlayer.getField().setMonsterCards(new ArrayList<>());
        theOtherPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        theOtherPlayer.getField().getSpellAndTrapCards().add(swordsOfRevealingLight);
        swordsOfRevealingLight.setActive(true);
        MonsterCard heraldOfCreation = (MonsterCard) Card.getCardByName("Herald of Creation");
        thisPlayer.getField().getMonsterCards().add(heraldOfCreation);
        heraldOfCreation.setOwner(thisPlayer);
        heraldOfCreation.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_DOWN);
        DuelController.getInstance().handleSwordOfRevealingLight();
        Assertions.assertEquals(MonsterCardModeInField.DEFENSE_FACE_UP, heraldOfCreation.getMonsterCardModeInField());
        thisPlayer.getField().setMonsterCards(monsterCards);
        theOtherPlayer.getField().setSpellAndTrapCards(spellAndTrapCards);
    }

    @Test
    public void exchangeWithSideDeckTest() {
        ArrayList<Card> deckZone = thisPlayer.getField().getDeckZone();
        ArrayList<Card> sideDeck = thisPlayer.getField().getSideDeck();
        thisPlayer.getField().setDeckZone(new ArrayList<>());
        thisPlayer.getField().setSideDeck(new ArrayList<>());
        MonsterCard slutMachine = (MonsterCard) Card.getCardByName("Slot Machine");
        slutMachine.setOwner(thisPlayer);
        thisPlayer.getField().getDeckZone().add(slutMachine);
        SpellAndTrapCard inTheCloset = (SpellAndTrapCard) Card.getCardByName("Closed Forest");
        inTheCloset.setOwner(thisPlayer);
        thisPlayer.getField().getSideDeck().add(inTheCloset);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\nClosed Forest*Slot Machine\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().exchangeCardsWithSideDeck(thisPlayer);
        Assertions.assertTrue(thisPlayer.getField().getSideDeck().contains(slutMachine));
        System.setIn(backup);
        thisPlayer.getField().setDeckZone(deckZone);
        thisPlayer.getField().setSideDeck(sideDeck);
    }

    @Test
    public void canMakeChainTest() {
        SpellAndTrapCard adam = (SpellAndTrapCard) Card.getCardByName("Call of The Haunted");
        adam.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = thisPlayer.getField().getSpellAndTrapCards();
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().getSpellAndTrapCards().add(adam);
        Assertions.assertTrue(DuelController.getInstance().canMakeChain(thisPlayer));
        thisPlayer.getField().setSpellAndTrapCards(spellAndTrapCards);
    }

    @Test
    public void activateCards() {
        SpellAndTrapCard adam = (SpellAndTrapCard) Card.getCardByName("Call of The Haunted");
        adam.setOwner(thisPlayer);
        MonsterCard tadashi = (MonsterCard) Card.getCardByName("Flame manipulator");
        tadashi.setOwner(thisPlayer);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = thisPlayer.getField().getSpellAndTrapCards();
        thisPlayer.getField().setSpellAndTrapCards(new ArrayList<>());
        thisPlayer.getField().getSpellAndTrapCards().add(adam);
        ArrayList<MonsterCard> monsterCards = thisPlayer.getField().getMonsterCards();
        ArrayList<Card> GY = thisPlayer.getField().getGraveyard();
        thisPlayer.getField().setGraveyard(new ArrayList<>());
        thisPlayer.getField().getGraveyard().add(tadashi);
        DuelController.getInstance().getForChain().add(adam);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n0\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().activateCards();
        Assertions.assertTrue(thisPlayer.getField().getMonsterCards().contains(tadashi));
        thisPlayer.getField().setSpellAndTrapCards(spellAndTrapCards);
        System.setIn(backup);
        thisPlayer.getField().setMonsterCards(monsterCards);
        thisPlayer.getField().setGraveyard(GY);
    }

    @Test
    public void fromFieldZoneTest() {
        SpellAndTrapCard adam = (SpellAndTrapCard) Card.getCardByName("Call of The Haunted");
        adam.setOwner(thisPlayer);
        thisPlayer.getField().setFieldZone(adam);
        DuelController.getInstance().moveSpellOrTrapToGYFromFieldZone(adam);
        Assertions.assertNull(thisPlayer.getField().getFieldZone());
    }

    @Test
    public void cancelTest() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("cancel\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        Assertions.assertTrue(DuelController.getInstance().actionForChain(thisPlayer));
        System.setIn(backup);
    }
}