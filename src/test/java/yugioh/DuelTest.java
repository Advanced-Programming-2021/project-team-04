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
    static final Account FIRST_ACCOUNT = new Account("Bad Decisions", "The Strokes", "Why Do I Exist");
    static final Account SECOND_ACCOUNT = new Account("Delilah", "Jessica", "Ricca");
    static SpellAndTrapCard card;

    static {
        try {
            card = (SpellAndTrapCard) ImportAndExport.getInstance().readCard("Dark Hole");
        } catch (Exception ignored) {
        }
    }

    @BeforeAll
    public static void setup() {
        ShopController.getInstance();
        MainController.getInstance().setLoggedIn(FIRST_ACCOUNT);
        var cardName = "Dark Hole";
        PlayerDeck playerDeck = new PlayerDeck("Damaged");
        for (int i = 0; i < 40; i++)
            playerDeck.addCardToMainDeck(cardName);
        FIRST_ACCOUNT.addDeck(playerDeck);
        SECOND_ACCOUNT.addDeck(playerDeck);
        FIRST_ACCOUNT.setActivePlayerDeck("Damaged");
        SECOND_ACCOUNT.setActivePlayerDeck("Damaged");
        DuelController.getInstance().setGame(new Game(FIRST_ACCOUNT, SECOND_ACCOUNT, 3, false));
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
        Assertions.assertEquals(FIRST_ACCOUNT, DuelController.getInstance().getGame().getCurrentPlayer());
    }

    @Test
    public void drawTest() {
        int hand = FIRST_ACCOUNT.getField().getHand().size();
        int deck = FIRST_ACCOUNT.getField().getDeckZone().size();
        FIRST_ACCOUNT.setAbleToDraw(true);
        DuelController.getInstance().drawPhase();
        Assertions.assertEquals(hand + 1, FIRST_ACCOUNT.getField().getHand().size());
        Assertions.assertEquals(deck - 1, FIRST_ACCOUNT.getField().getDeckZone().size());
    }

    @Test
    public void battlePhaseTest() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        FIRST_ACCOUNT.setAbleToAttack(false);
        DuelController.getInstance().battlePhase();
        Assertions.assertTrue(FIRST_ACCOUNT.isAbleToAttack());
        Assertions.assertEquals(Phases.SECOND_MAIN_PHASE, DuelController.getInstance().getGame().getCurrentPhase());
    }

    @Test
    public void resetTest() {
        card.setOwner(FIRST_ACCOUNT);
        card.setHasBeenUsedInThisTurn(true);
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(card);
        DuelController.getInstance().endPhase();
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().remove(card);
        card.setOwner(null);
        Assertions.assertFalse(card.isHasBeenUsedInThisTurn());
    }

    @Test
    public void selectErrorsHandTest() {
        int number = FIRST_ACCOUNT.getField().getHand().size() + 1;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().selectCard(true, CardStatusInField.HAND, number);
        Assertions.assertEquals("invalid selection\r\n", outputStream.toString());
    }

    @Test
    public void selectErrorsMonsterTest() {
        int number = FIRST_ACCOUNT.getField().getMonsterCards().size() + 1;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().selectCard(true, CardStatusInField.MONSTER_FIELD, number);
        Assertions.assertEquals("invalid selection\r\n", outputStream.toString());
    }

    @Test
    public void selectErrorsSpellTest() {
        int number = FIRST_ACCOUNT.getField().getSpellAndTrapCards().size() + 1;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().selectCard(true, CardStatusInField.SPELL_FIELD, number);
        Assertions.assertEquals("invalid selection\r\n", outputStream.toString());
    }

    @Test
    public void selectErrorsField() {
        FIRST_ACCOUNT.getField().setFieldZone(null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().selectCard(true, CardStatusInField.FIELD_ZONE, 0);
        Assertions.assertEquals("no card found in the given position\r\n", outputStream.toString());
    }

    @Test
    public void selectTest() {
        setup();
        DuelController.getInstance().selectCard(true, CardStatusInField.HAND, 0);
        Assertions.assertEquals(DuelController.getInstance().getGame().getSelectedCard().getName(), card.getName());
        DuelController.getInstance().deselectCard();
        Assertions.assertNull(DuelController.getInstance().getGame().getSelectedCard());
    }

    @Test
    public void nextPhaseTest() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.DRAW_PHASE);
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        DuelController.getInstance().nextPhase();
        Assertions.assertEquals(Phases.STANDBY_PHASE, DuelController.getInstance().getGame().getCurrentPhase());
    }

    @Test
    public void texChangerTest() {
        MonsterCard texChanger = (MonsterCard) Card.getCardByName("Texchanger");
        ArrayList<Card> backupHand = SECOND_ACCOUNT.getField().getHand();
        SECOND_ACCOUNT.getField().setHand(new ArrayList<>());
        MonsterCard leotron = (MonsterCard) Card.getCardByName("Leotron");
        leotron.setOwner(SECOND_ACCOUNT);
        SECOND_ACCOUNT.getField().getHand().add(leotron);
        SECOND_ACCOUNT.getField().getMonsterCards().add(texChanger);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("1\r\n1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().texChanger(texChanger);
        System.setIn(backup);
        SECOND_ACCOUNT.getField().setHand(backupHand);
        leotron.setOwner(null);
        Assertions.assertTrue(SECOND_ACCOUNT.getField().getMonsterCards().contains(leotron));
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
        MonsterCard card = (MonsterCard) Card.getCardByName("Baby dragon");
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().getMonsterCards().add(card);
        card.setOwner(SECOND_ACCOUNT);
        MonsterCard barbaros = (MonsterCard) Card.getCardByName("Beast King Barbaros");
        DuelController.getInstance().getGame().setSelectedCard(barbaros);
        MonsterCard battleOX = (MonsterCard) Card.getCardByName("Battle OX");
        MonsterCard crawlingDragon = (MonsterCard) Card.getCardByName("Crawling dragon");
        MonsterCard battleWarrior = (MonsterCard) Card.getCardByName("Battle warrior");
        battleOX.setOwner(FIRST_ACCOUNT);
        crawlingDragon.setOwner(FIRST_ACCOUNT);
        battleWarrior.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().getMonsterCards().add(battleOX);
        FIRST_ACCOUNT.getField().getMonsterCards().add(crawlingDragon);
        FIRST_ACCOUNT.getField().getMonsterCards().add(battleWarrior);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("1\r\n1\r\n1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().barbaros(3);
        System.setIn(backup);
        Assertions.assertEquals(0, SECOND_ACCOUNT.getField().getMonsterCards().size());
    }

    @Test
    public void gateGuardianTest() {
        ShopController.getInstance().buyCard("Gate Guardian");
        var gateGuardian = (MonsterCard) Card.getCardByName("Gate Guardian");
        gateGuardian.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSelectedCard(gateGuardian);
        var babyDragon = (MonsterCard) Card.getCardByName("Baby dragon");
        babyDragon.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().getMonsterCards().add(babyDragon);
        var crawlingDragon = (MonsterCard) Card.getCardByName("Crawling dragon");
        crawlingDragon.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().getMonsterCards().add(crawlingDragon);
        var battleWarrior = (MonsterCard) Card.getCardByName("Battle warrior");
        battleWarrior.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().getMonsterCards().add(battleWarrior);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("1\r\n1\r\n1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().gateGuardian();
        System.setIn(backup);
        Assertions.assertNull(DuelController.getInstance().getGame().getSelectedCard());
    }

    @Test
    public void theTrickyTest() {
        MonsterCard theTricky = (MonsterCard) Card.getCardByName("The Tricky");
        theTricky.setOwner(FIRST_ACCOUNT);
        ArrayList<Card> hand = FIRST_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        DuelController.getInstance().getGame().setSelectedCard(theTricky);
        Card babyDragon = Card.getCardByName("Baby dragon");
        babyDragon.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().getHand().add(babyDragon);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("1".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().theTricky();
        System.setIn(backup);
        FIRST_ACCOUNT.getField().setHand(hand);
        Assertions.assertNull(DuelController.getInstance().getGame().getSelectedCard());
    }

    @Test
    public void heraldOfCreationTest() {
        MonsterCard spiralSerpent = (MonsterCard) Card.getCardByName("Spiral Serpent");
        spiralSerpent.setOwner(FIRST_ACCOUNT);
        ArrayList<Card> graveyardBackUp = FIRST_ACCOUNT.getField().getGraveyard();
        ArrayList<Card> hand = FIRST_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        FIRST_ACCOUNT.getField().setGraveyard(new ArrayList<>());
        FIRST_ACCOUNT.getField().getGraveyard().add(spiralSerpent);
        Card babyDragon = Card.getCardByName("Baby dragon");
        babyDragon.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().getHand().add(babyDragon);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("1\r\n1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().heraldOfCreation();
        System.setIn(backup);
        boolean hasCard = FIRST_ACCOUNT.getField().getHand().contains(spiralSerpent);
        FIRST_ACCOUNT.getField().setHand(hand);
        FIRST_ACCOUNT.getField().setGraveyard(graveyardBackUp);
        Assertions.assertTrue(hasCard);
    }

    @Test
    public void forScannerTest() {
        ArrayList<Card> opponentGY = SECOND_ACCOUNT.getField().getGraveyard();
        ArrayList<MonsterCard> thisMonsterZone = FIRST_ACCOUNT.getField().getMonsterCards();
        SECOND_ACCOUNT.getField().setGraveyard(new ArrayList<>());
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Wattaildragon");
        monsterCard.setOwner(SECOND_ACCOUNT);
        SECOND_ACCOUNT.getField().getGraveyard().add(monsterCard);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n1".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        Scanner scanner = (Scanner) Card.getCardByName("Scanner");
        scanner.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().forScanner(scanner);
        Assertions.assertTrue(FIRST_ACCOUNT.getField().getMonsterCards().contains(monsterCard));
        scanner.reset();
        Assertions.assertTrue(SECOND_ACCOUNT.getField().getGraveyard().contains(monsterCard));
        SECOND_ACCOUNT.getField().setGraveyard(opponentGY);
        FIRST_ACCOUNT.getField().setMonsterCards(thisMonsterZone);
        System.setIn(backup);
    }

    @Test
    public void forManEaterTest() {
        ArrayList<Card> opponentGY = SECOND_ACCOUNT.getField().getGraveyard();
        ArrayList<MonsterCard> monsterCards = SECOND_ACCOUNT.getField().getMonsterCards();
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Wattaildragon");
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("1".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        monsterCard.setOwner(SECOND_ACCOUNT);
        SECOND_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().forManEaterBug();
        Assertions.assertTrue(SECOND_ACCOUNT.getField().getGraveyard().contains(monsterCard));
        SECOND_ACCOUNT.getField().setGraveyard(opponentGY);
        SECOND_ACCOUNT.getField().setMonsterCards(monsterCards);
        System.setIn(backup);
    }

    @Test
    public void supplySquadTest() {
        ArrayList<MonsterCard> monsterCards = FIRST_ACCOUNT.getField().getMonsterCards();
        ArrayList<Card> myGY = FIRST_ACCOUNT.getField().getGraveyard();
        ArrayList<Card> deck = FIRST_ACCOUNT.getField().getDeckZone();
        ArrayList<SpellAndTrapCard> spellAndTrapCards = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        ArrayList<Card> hand = FIRST_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().setDeckZone(new ArrayList<>());
        SpellAndTrapCard spellAndTrapCard = (SpellAndTrapCard) Card.getCardByName("Supply Squad");
        spellAndTrapCard.setOwner(FIRST_ACCOUNT);
        spellAndTrapCard.setActive(true);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Dark magician");
        monsterCard.setOwner(FIRST_ACCOUNT);
        MonsterCard toDie = (MonsterCard) Card.getCardByName("Dark magician");
        toDie.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().getMonsterCards().add(toDie);
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(spellAndTrapCard);
        FIRST_ACCOUNT.getField().getDeckZone().add(monsterCard);
        DuelController.getInstance().handleSupplySquad(toDie, FIRST_ACCOUNT.getField());
        Assertions.assertTrue(FIRST_ACCOUNT.getField().getHand().contains(monsterCard));
        FIRST_ACCOUNT.getField().setMonsterCards(monsterCards);
        FIRST_ACCOUNT.getField().setGraveyard(myGY);
        FIRST_ACCOUNT.getField().setDeckZone(deck);
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(spellAndTrapCards);
        FIRST_ACCOUNT.getField().setHand(hand);
    }

    @Test
    public void decreaseLPTest() {
        int LP = SECOND_ACCOUNT.getLP();
        SECOND_ACCOUNT.setLP(8000);
        DuelController.getInstance().cheatDecreaseLP(500);
        Assertions.assertEquals(7500, SECOND_ACCOUNT.getLP());
        SECOND_ACCOUNT.setLP(LP);
    }

    @Test
    public void seeMyDeckTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().cheatSeeMyDeck();
        Assertions.assertEquals(("Dark Hole\n").repeat(DuelController.getInstance().getGame().getCurrentPlayer().getField().getDeckZone().size() - 1) + "Dark Hole" + System.lineSeparator(), outputStream.toString());
    }

    @Test
    public void increaseLPTest() {
        int LP = FIRST_ACCOUNT.getLP();
        DuelController.getInstance().cheatIncreaseLP(500);
        Assertions.assertEquals(LP + 500, FIRST_ACCOUNT.getLP());
        FIRST_ACCOUNT.setLP(LP);
    }

    @Test
    public void showRivalHandTest() {
        ArrayList<Card> hand = SECOND_ACCOUNT.getField().getHand();
        SECOND_ACCOUNT.getField().setHand(new ArrayList<>());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().cheatShowRivalHand();
        Assertions.assertEquals("\r\n", outputStream.toString());
        SECOND_ACCOUNT.getField().setHand(hand);
    }

    @Test
    public void terraTigerTest() {
        Card selectedCard = DuelController.getInstance().getGame().getSelectedCard();
        ArrayList<MonsterCard> monsterCards = FIRST_ACCOUNT.getField().getMonsterCards();
        ArrayList<Card> hand = FIRST_ACCOUNT.getField().getHand();
        MonsterCard abbas = (MonsterCard) Card.getCardByName("Haniwa");
        MonsterCard selected = (MonsterCard) Card.getCardByName("Skull Guardian");
        DuelController.getInstance().getGame().setSelectedCard(selected);
        abbas.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        FIRST_ACCOUNT.getField().getHand().add(abbas);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().terraTigerMethod();
        Assertions.assertEquals(0, FIRST_ACCOUNT.getField().getHand().size());
        FIRST_ACCOUNT.getField().setMonsterCards(monsterCards);
        FIRST_ACCOUNT.getField().setHand(hand);
        System.setIn(backup);
        DuelController.getInstance().getGame().setSelectedCard(selectedCard);
    }

    @Test
    public void showSelectedCard() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Silver Fang");
        monsterCard.setOwner(FIRST_ACCOUNT);
        Card already = DuelController.getInstance().getGame().getSelectedCard();
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().showSelectedCard();
        Assertions.assertEquals("""
                Name: Silver Fang
                Level: 3
                Type: Beast
                ATK: 1200
                DEF: 800
                Description: A snow wolf that's beautiful to the eye, but absolutely vicious in battle.\r
                """, outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(already);
    }

    @Test
    public void showSelectedError1() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Silver Fang");
        monsterCard.setOwner(SECOND_ACCOUNT);
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
        spellAndTrapCard.setOwner(SECOND_ACCOUNT);
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
        ArrayList<Card> GY = FIRST_ACCOUNT.getField().getGraveyard();
        SpellAndTrapCard spellAndTrapCard = (SpellAndTrapCard) Card.getCardByName("Spell Absorption");
        spellAndTrapCard.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().setGraveyard(new ArrayList<>());
        FIRST_ACCOUNT.getField().getGraveyard().add(spellAndTrapCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().showGraveyard();
        Assertions.assertEquals("Spell Absorption: Each time a Spell Card is activated," +
                " gain 500 Life Points immediately after it resolves.\r\n", outputStream.toString());
        FIRST_ACCOUNT.getField().setGraveyard(GY);
    }

    @Test
    public void ritualTestErrors1() {
        ArrayList<MonsterCard> backUpCards = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().ritualSummon();
        Assertions.assertEquals("there is no way you could ritual summon a monster\r\n", outputStream.toString());
        FIRST_ACCOUNT.getField().setMonsterCards(backUpCards);
    }

    @Test
    public void ritualTestErrors2() {
        ArrayList<Card> handBackUp = FIRST_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        MonsterCard ritualCard = (MonsterCard) Card.getCardByName("Skull Guardian");
        ritualCard.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().getHand().add(ritualCard);
        MonsterCard firstSacrifice = (MonsterCard) Card.getCardByName("Wattkid");
        firstSacrifice.setOwner(FIRST_ACCOUNT);
        MonsterCard secondSacrifice = (MonsterCard) Card.getCardByName("Wattaildragon");
        secondSacrifice.setOwner(FIRST_ACCOUNT);
        ArrayList<MonsterCard> backUpCards = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(firstSacrifice);
        FIRST_ACCOUNT.getField().getMonsterCards().add(secondSacrifice);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().ritualSummon();
        FIRST_ACCOUNT.getField().setMonsterCards(backUpCards);
        FIRST_ACCOUNT.getField().setHand(handBackUp);
        Assertions.assertEquals("there is no way you could ritual summon a monster\r\n", outputStream.toString());
    }

    @Test
    public void ritualTest() {
        SpellAndTrapCard spell = (SpellAndTrapCard) Card.getCardByName("Advanced Ritual Art");
        spell.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSelectedCard(spell);
        ArrayList<Card> handBackUp = FIRST_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        MonsterCard ritualCard = (MonsterCard) Card.getCardByName("Skull Guardian"); //7
        ritualCard.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().getHand().add(ritualCard);
        MonsterCard firstSacrifice = (MonsterCard) Card.getCardByName("Wattkid");
        firstSacrifice.setOwner(FIRST_ACCOUNT);
        MonsterCard secondSacrifice = (MonsterCard) Card.getCardByName("Warrior Dai Grepher");
        secondSacrifice.setOwner(FIRST_ACCOUNT);
        ArrayList<MonsterCard> backUpCards = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(firstSacrifice);
        FIRST_ACCOUNT.getField().getMonsterCards().add(secondSacrifice);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("1\r\n1 2\r\nattack\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().ritualSummon();
        FIRST_ACCOUNT.getField().setHand(handBackUp);
        FIRST_ACCOUNT.getField().setMonsterCards(backUpCards);
        Assertions.assertEquals("""
                enter the number of the ritual card you choose:\r
                enter the numbers of the card you want to tribute divided by a space:\r
                choose the monster mode (Attack or Defense):\r
                summoned successfully\r
                """, outputStream.toString());
    }

    @Test
    public void setMonsterTest() {
        MonsterCard silverFag = (MonsterCard) Card.getCardByName("Silver Fang");
        silverFag.setOwner(FIRST_ACCOUNT);
        ArrayList<Card> hand = FIRST_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        FIRST_ACCOUNT.getField().getHand().add(silverFag);
        DuelController.getInstance().getGame().setSelectedCard(silverFag);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        ArrayList<MonsterCard> monsterBackUp = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        DuelController.getInstance().getGame().setSummonedInThisTurn(false);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().set();
        FIRST_ACCOUNT.getField().setHand(hand);
        FIRST_ACCOUNT.getField().setMonsterCards(monsterBackUp);
        Assertions.assertTrue(outputStream.toString().startsWith("set successfully"));
    }

    @Test
    public void setTrapSpellTest() {
        SpellAndTrapCard vanity = (SpellAndTrapCard) Card.getCardByName("Vanity's Emptiness");
        vanity.setOwner(FIRST_ACCOUNT);
        ArrayList<Card> hand = FIRST_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        FIRST_ACCOUNT.getField().getHand().add(vanity);
        DuelController.getInstance().getGame().setSelectedCard(vanity);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        DuelController.getInstance().getGame().setSummonedInThisTurn(false);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().set();
        FIRST_ACCOUNT.getField().setHand(hand);
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(spellAndTrapCards);
        Assertions.assertTrue(outputStream.toString().startsWith("set successfully"));
    }

    @Test
    public void monsterRebornTest() {
        SpellAndTrapCard monsterReborn = (SpellAndTrapCard) Card.getCardByName("Monster Reborn");
        monsterReborn.setOwner(FIRST_ACCOUNT);
        ArrayList<SpellAndTrapCard> spellZone = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        ArrayList<Card> GY = FIRST_ACCOUNT.getField().getGraveyard();
        MonsterCard marshmallon = (MonsterCard) Card.getCardByName("Marshmallon");
        marshmallon.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().setGraveyard(new ArrayList<>());
        FIRST_ACCOUNT.getField().getGraveyard().add(marshmallon);
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(monsterReborn);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().monsterReborn(monsterReborn);
        Assertions.assertTrue(FIRST_ACCOUNT.getField().getGraveyard().contains(monsterReborn));
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(spellZone);
        FIRST_ACCOUNT.getField().setGraveyard(GY);
    }

    @Test
    public void terraFormingTest() {
        SpellAndTrapCard terraForming = (SpellAndTrapCard) Card.getCardByName("Terraforming");
        terraForming.setOwner(FIRST_ACCOUNT);
        ArrayList<Card> handBackUp = FIRST_ACCOUNT.getField().getHand();
        ArrayList<Card> GY = FIRST_ACCOUNT.getField().getGraveyard();
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        ArrayList<Card> deckBackUp = FIRST_ACCOUNT.getField().getDeckZone();
        SpellAndTrapCard witch = (SpellAndTrapCard) Card.getCardByName("Forest");
        witch.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().setDeckZone(new ArrayList<>());
        FIRST_ACCOUNT.getField().getDeckZone().add(witch);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().terraforming(terraForming);
        Assertions.assertTrue(FIRST_ACCOUNT.getField().getGraveyard().contains(terraForming));
        FIRST_ACCOUNT.getField().setHand(handBackUp);
        FIRST_ACCOUNT.getField().setDeckZone(deckBackUp);
        FIRST_ACCOUNT.getField().setGraveyard(GY);
    }

    @Test
    public void potOfGreedTest() {
        SpellAndTrapCard potOfGreed = (SpellAndTrapCard) Card.getCardByName("Pot of Greed");
        potOfGreed.setOwner(FIRST_ACCOUNT);
        ArrayList<Card> handBackUp = FIRST_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        ArrayList<Card> deckBackUp = FIRST_ACCOUNT.getField().getDeckZone();
        FIRST_ACCOUNT.getField().setDeckZone(new ArrayList<>());
        SpellAndTrapCard joe = (SpellAndTrapCard) Card.getCardByName("Forest");
        joe.setOwner(FIRST_ACCOUNT);
        SpellAndTrapCard cherry = (SpellAndTrapCard) Card.getCardByName("Forest");
        cherry.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().setDeckZone(new ArrayList<>());
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        FIRST_ACCOUNT.getField().getDeckZone().add(joe);
        FIRST_ACCOUNT.getField().getDeckZone().add(cherry);
        DuelController.getInstance().potOfGreed(potOfGreed);
        Assertions.assertTrue(FIRST_ACCOUNT.getField().getHand().contains(joe) &&
                FIRST_ACCOUNT.getField().getHand().contains(cherry));
        FIRST_ACCOUNT.getField().setHand(handBackUp);
        FIRST_ACCOUNT.getField().setDeckZone(deckBackUp);
    }

    @Test
    public void raigekiTest() {
        SpellAndTrapCard reki = (SpellAndTrapCard) Card.getCardByName("Raigeki");
        reki.setOwner(FIRST_ACCOUNT);
        ArrayList<MonsterCard> opponentMonsterCards = SECOND_ACCOUNT.getField().getMonsterCards();
        ArrayList<Card> myGY = FIRST_ACCOUNT.getField().getGraveyard();
        ArrayList<SpellAndTrapCard> spellAndTrapCards = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        MonsterCard spiralSerpent = (MonsterCard) Card.getCardByName("Spiral Serpent");
        spiralSerpent.setOwner(SECOND_ACCOUNT);
        MonsterCard hornImp = (MonsterCard) Card.getCardByName("Horn Imp");
        hornImp.setOwner(SECOND_ACCOUNT);
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().getMonsterCards().add(hornImp);
        SECOND_ACCOUNT.getField().getMonsterCards().add(spiralSerpent);
        DuelController.getInstance().raigeki(reki);
        Assertions.assertTrue(SECOND_ACCOUNT.getField().getMonsterCards().isEmpty());
        SECOND_ACCOUNT.getField().setMonsterCards(opponentMonsterCards);
        FIRST_ACCOUNT.getField().setGraveyard(myGY);
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(spellAndTrapCards);
    }

    @Test
    public void harpiesFeatherDusterTest() {
        SpellAndTrapCard harpie = (SpellAndTrapCard) Card.getCardByName("Harpie's Feather Duster");
        harpie.setOwner(FIRST_ACCOUNT);
        ArrayList<SpellAndTrapCard> spellCards = SECOND_ACCOUNT.getField().getSpellAndTrapCards();
        ArrayList<Card> myGY = FIRST_ACCOUNT.getField().getGraveyard();
        ArrayList<SpellAndTrapCard> spellAndTrapCards = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        SpellAndTrapCard solemnWarning = (SpellAndTrapCard) Card.getCardByName("Solemn Warning");
        solemnWarning.setOwner(SECOND_ACCOUNT);
        SpellAndTrapCard timeSeal = (SpellAndTrapCard) Card.getCardByName("Time Seal");
        timeSeal.setOwner(SECOND_ACCOUNT);
        SECOND_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().getSpellAndTrapCards().add(solemnWarning);
        SECOND_ACCOUNT.getField().getSpellAndTrapCards().add(timeSeal);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("no\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().harpiesFeatherDuster(harpie);
        Assertions.assertTrue(SECOND_ACCOUNT.getField().getSpellAndTrapCards().isEmpty());
        SECOND_ACCOUNT.getField().setSpellAndTrapCards(spellCards);
        FIRST_ACCOUNT.getField().setGraveyard(myGY);
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(spellAndTrapCards);
    }

    @Test
    public void darkHoleTest() {
        SpellAndTrapCard darkHole = (SpellAndTrapCard) Card.getCardByName("Dark Hole");
        darkHole.setOwner(FIRST_ACCOUNT);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        ArrayList<MonsterCard> opponentsMonsters = SECOND_ACCOUNT.getField().getMonsterCards();
        ArrayList<Card> myHand = FIRST_ACCOUNT.getField().getHand();
        ArrayList<Card> myGY = FIRST_ACCOUNT.getField().getGraveyard();
        ArrayList<Card> opponentGY = SECOND_ACCOUNT.getField().getGraveyard();
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        MonsterCard bitron = (MonsterCard) Card.getCardByName("Bitron");
        bitron.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().getMonsterCards().add(bitron);
        MonsterCard battleOx = (MonsterCard) Card.getCardByName("Battle OX");
        battleOx.setOwner(SECOND_ACCOUNT);
        SECOND_ACCOUNT.getField().getMonsterCards().add(battleOx);
        DuelController.getInstance().darkHole(darkHole);
        Assertions.assertTrue(SECOND_ACCOUNT.getField().getMonsterCards().isEmpty() &&
                FIRST_ACCOUNT.getField().getMonsterCards().isEmpty());
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
        SECOND_ACCOUNT.getField().setMonsterCards(opponentsMonsters);
        SECOND_ACCOUNT.getField().setGraveyard(opponentGY);
        FIRST_ACCOUNT.getField().setGraveyard(myGY);
        FIRST_ACCOUNT.getField().setHand(myHand);
    }

    @Test
    public void mysticalSpaceTyphoonTest() {
        SpellAndTrapCard mystical = (SpellAndTrapCard) Card.getCardByName("Mystical space typhoon");
        mystical.setOwner(FIRST_ACCOUNT);
        SpellAndTrapCard umiiruka = (SpellAndTrapCard) Card.getCardByName("Umiiruka");
        umiiruka.setOwner(FIRST_ACCOUNT);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        ArrayList<Card> GY = FIRST_ACCOUNT.getField().getGraveyard();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(umiiruka);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().mysticalSpaceTyphoon(mystical);
        Assertions.assertTrue(FIRST_ACCOUNT.getField().getSpellAndTrapCards().isEmpty());
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(spellAndTrapCards);
        FIRST_ACCOUNT.getField().setGraveyard(GY);
    }

    @Test
    public void getAllMonstersTest() {
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        ArrayList<MonsterCard> opponentsMonsters = SECOND_ACCOUNT.getField().getMonsterCards();
        MonsterCard silverFag = (MonsterCard) Card.getCardByName("Silver Fang");
        silverFag.setOwner(FIRST_ACCOUNT);
        MonsterCard gateGuardian = (MonsterCard) Card.getCardByName("Gate Guardian");
        gateGuardian.setOwner(SECOND_ACCOUNT);
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().getMonsterCards().add(gateGuardian);
        FIRST_ACCOUNT.getField().getMonsterCards().add(silverFag);
        ArrayList<MonsterCard> toCompare = new ArrayList<>();
        toCompare.add(silverFag);
        toCompare.add(gateGuardian);
        ArrayList<MonsterCard> monsterCards = DuelController.getInstance().getAllMonsterCards();
        Assertions.assertEquals(monsterCards, toCompare);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
        SECOND_ACCOUNT.getField().setMonsterCards(opponentsMonsters);
    }

    @Test
    public void yamiTest() {
        MonsterCard marshmallon = (MonsterCard) Card.getCardByName("Marshmallon");
        marshmallon.setOwner(FIRST_ACCOUNT);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(marshmallon);
        DuelController.getInstance().yami();
        Assertions.assertEquals(marshmallon.getThisCardAttackPower(), 100);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void forestTest() {
        MonsterCard silverFag = (MonsterCard) Card.getCardByName("Silver Fang");
        silverFag.setOwner(FIRST_ACCOUNT);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(silverFag);
        DuelController.getInstance().forest();
        Assertions.assertEquals(1400, silverFag.getThisCardAttackPower());
        silverFag.reset();
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void closedForest() {
        MonsterCard silverFag = (MonsterCard) Card.getCardByName("Silver Fang");
        SpellAndTrapCard inTheCloset = (SpellAndTrapCard) Card.getCardByName("Closed Forest");
        inTheCloset.setOwner(FIRST_ACCOUNT);
        silverFag.setOwner(FIRST_ACCOUNT);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        ArrayList<Card> GY = FIRST_ACCOUNT.getField().getGraveyard();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().setGraveyard(new ArrayList<>());
        FIRST_ACCOUNT.getField().getGraveyard().add(inTheCloset);
        FIRST_ACCOUNT.getField().getMonsterCards().add(silverFag);
        DuelController.getInstance().closedForest();
        Assertions.assertEquals(1300, silverFag.getThisCardAttackPower());
        silverFag.reset();
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
        FIRST_ACCOUNT.getField().setGraveyard(GY);
    }

    @Test
    public void umiirukaTest() {
        MonsterCard yomi = (MonsterCard) Card.getCardByName("Yomi Ship");
        yomi.setOwner(FIRST_ACCOUNT);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(yomi);
        DuelController.getInstance().umiiruka();
        Assertions.assertEquals(1300, yomi.getThisCardAttackPower());
        Assertions.assertEquals(1000, yomi.getThisCardDefensePower());
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
        yomi.reset();
    }

    @Test
    public void changeOfHeartTest() {
        ChangeOfHeart changeOfHeart = (ChangeOfHeart) Card.getCardByName("Change of Heart");
        changeOfHeart.setOwner(FIRST_ACCOUNT);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Spiral Serpent");
        monsterCard.setOwner(SECOND_ACCOUNT);
        ArrayList<MonsterCard> opponentMonsters = SECOND_ACCOUNT.getField().getMonsterCards();
        ArrayList<MonsterCard> thisPlayerMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().changeOfHeart(changeOfHeart);
        Assertions.assertTrue(FIRST_ACCOUNT.getField().getMonsterCards().contains(monsterCard));
        SECOND_ACCOUNT.getField().setMonsterCards(opponentMonsters);
        FIRST_ACCOUNT.getField().setMonsterCards(thisPlayerMonsters);
    }

    @Test
    public void twinTwistersTest() {
        SpellAndTrapCard twinTwister = (SpellAndTrapCard) Card.getCardByName("Twin Twisters");
        SpellAndTrapCard cardFromHand = (SpellAndTrapCard) Card.getCardByName("Yami");
        SpellAndTrapCard toRemove = (SpellAndTrapCard) Card.getCardByName("Mind Crush");
        twinTwister.setOwner(FIRST_ACCOUNT);
        cardFromHand.setOwner(FIRST_ACCOUNT);
        toRemove.setOwner(SECOND_ACCOUNT);
        ArrayList<SpellAndTrapCard> thisPlayerSpells = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(twinTwister);
        ArrayList<Card> hand = FIRST_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        FIRST_ACCOUNT.getField().getHand().add(cardFromHand);
        ArrayList<SpellAndTrapCard> opponentSpells = SECOND_ACCOUNT.getField().getSpellAndTrapCards();
        SECOND_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().getSpellAndTrapCards().add(toRemove);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("no\r\nno\r\n1\r\n1\r\nno\r\n1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().twinTwisters(twinTwister);
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(thisPlayerSpells);
        FIRST_ACCOUNT.getField().setHand(hand);
        boolean assertBoolean = SECOND_ACCOUNT.getField().getSpellAndTrapCards().contains(toRemove);
        SECOND_ACCOUNT.getField().setSpellAndTrapCards(opponentSpells);
        Assertions.assertFalse(assertBoolean);
    }

    @Test
    public void mirrorForceTest() {
        SpellAndTrapCard mirrorForce = (SpellAndTrapCard) Card.getCardByName("Mirror Force");
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Curtain of the dark ones");
        mirrorForce.setOwner(FIRST_ACCOUNT);
        monsterCard.setOwner(SECOND_ACCOUNT);
        ArrayList<SpellAndTrapCard> mySpells = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        ArrayList<MonsterCard> opponentMonsters = SECOND_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(mirrorForce);
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        DuelController.getInstance().mirrorForce(mirrorForce, SECOND_ACCOUNT);
        Assertions.assertFalse(SECOND_ACCOUNT.getField().getMonsterCards().contains(monsterCard));
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(mySpells);
        SECOND_ACCOUNT.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void magicCylinderTest() {
        SpellAndTrapCard magicCylinder = (SpellAndTrapCard) Card.getCardByName("Magic Cylinder");
        MonsterCard attacker = (MonsterCard) Card.getCardByName("Feral Imp");
        magicCylinder.setOwner(FIRST_ACCOUNT);
        attacker.setOwner(SECOND_ACCOUNT);
        ArrayList<SpellAndTrapCard> mySpells = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        ArrayList<MonsterCard> opponentMonsters = SECOND_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(magicCylinder);
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().getMonsterCards().add(attacker);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        SECOND_ACCOUNT.setLP(8000);
        DuelController.getInstance().magicCylinder(attacker, magicCylinder);
        Assertions.assertEquals(8000 - attacker.getThisCardAttackPower(), SECOND_ACCOUNT.getLP());
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(mySpells);
        SECOND_ACCOUNT.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void swordOfDarkDestruction() {
        SpellAndTrapCard swordOfDarkDestruction = (SpellAndTrapCard) Card.getCardByName("Sword of dark destruction");
        MonsterCard hornImp = (MonsterCard) Card.getCardByName("Horn Imp");
        swordOfDarkDestruction.setOwner(FIRST_ACCOUNT);
        hornImp.setOwner(FIRST_ACCOUNT);
        ArrayList<SpellAndTrapCard> mySpells = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(swordOfDarkDestruction);
        FIRST_ACCOUNT.getField().getMonsterCards().add(hornImp);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().equipMonster(swordOfDarkDestruction);
        Assertions.assertEquals(400 + hornImp.getClassAttackPower(), hornImp.getThisCardAttackPower());
        hornImp.reset();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(mySpells);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void blackPendantTest() {
        SpellAndTrapCard blackPendant = (SpellAndTrapCard) Card.getCardByName("Black Pendant");
        MonsterCard hornImp = (MonsterCard) Card.getCardByName("Horn Imp");
        blackPendant.setOwner(FIRST_ACCOUNT);
        hornImp.setOwner(FIRST_ACCOUNT);
        ArrayList<SpellAndTrapCard> mySpells = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(blackPendant);
        FIRST_ACCOUNT.getField().getMonsterCards().add(hornImp);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().equipMonster(blackPendant);
        Assertions.assertEquals(500 + hornImp.getClassAttackPower(), hornImp.getThisCardAttackPower());
        hornImp.reset();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(mySpells);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void unitedWeStandTest() {
        SpellAndTrapCard unitedWeStand = (SpellAndTrapCard) Card.getCardByName("United We Stand");
        MonsterCard hornImp = (MonsterCard) Card.getCardByName("Horn Imp");
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Dark Blade");
        unitedWeStand.setOwner(FIRST_ACCOUNT);
        hornImp.setOwner(FIRST_ACCOUNT);
        monsterCard.setOwner(FIRST_ACCOUNT);
        ArrayList<SpellAndTrapCard> mySpells = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(unitedWeStand);
        FIRST_ACCOUNT.getField().getMonsterCards().add(hornImp);
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().equipMonster(unitedWeStand);
        Assertions.assertEquals(1600 + hornImp.getClassAttackPower(), hornImp.getThisCardAttackPower());
        monsterCard.reset();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(mySpells);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void magnumShield() {
        SpellAndTrapCard magnumShield = (SpellAndTrapCard) Card.getCardByName("Magnum Shield");
        MonsterCard darkBlade = (MonsterCard) Card.getCardByName("Dark Blade");
        magnumShield.setOwner(FIRST_ACCOUNT);
        darkBlade.setOwner(FIRST_ACCOUNT);
        ArrayList<SpellAndTrapCard> mySpells = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(magnumShield);
        FIRST_ACCOUNT.getField().getMonsterCards().add(darkBlade);
        darkBlade.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().equipMonster(magnumShield);
        System.setIn(backup);
        Assertions.assertEquals(darkBlade.getClassAttackPower() + darkBlade.getClassDefensePower(), darkBlade.getThisCardAttackPower());
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(mySpells);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void mindCrushTest() {
        SpellAndTrapCard mindCrush = (SpellAndTrapCard) Card.getCardByName("Mind Crush");
        Card opponentCard = Card.getCardByName("Closed Forest");
        mindCrush.setOwner(FIRST_ACCOUNT);
        opponentCard.setOwner(SECOND_ACCOUNT);
        ArrayList<Card> opponentHand = SECOND_ACCOUNT.getField().getHand();
        SECOND_ACCOUNT.getField().setHand(new ArrayList<>());
        SECOND_ACCOUNT.getField().getHand().add(opponentCard);
        ArrayList<SpellAndTrapCard> mySpells = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(mindCrush);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\nClosed Forest\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().mindCrush(mindCrush, SECOND_ACCOUNT);
        SECOND_ACCOUNT.getField().setHand(opponentHand);
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(mySpells);
    }

    @Test
    public void removeRandomTest() {
        FIRST_ACCOUNT.getField().getHand().add(Card.getCardByName("Closed Forest"));
        int size = FIRST_ACCOUNT.getField().getHand().size();
        DuelController.getInstance().randomlyRemoveFromHand(FIRST_ACCOUNT);
        Assertions.assertEquals(size - 1, FIRST_ACCOUNT.getField().getHand().size());
    }

    @Test
    public void torrentialTributeTest() {
        SpellAndTrapCard torrential = (SpellAndTrapCard) Card.getCardByName("Torrential Tribute");
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Dark magician");
        torrential.setOwner(SECOND_ACCOUNT);
        monsterCard.setOwner(FIRST_ACCOUNT);
        ArrayList<SpellAndTrapCard> opponentSpells = SECOND_ACCOUNT.getField().getSpellAndTrapCards();
        SECOND_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().getSpellAndTrapCards().add(torrential);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().torrentialTribute();
        Assertions.assertTrue(FIRST_ACCOUNT.getField().getMonsterCards().isEmpty());
        SECOND_ACCOUNT.getField().setSpellAndTrapCards(opponentSpells);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void timeSealTest() {
        SpellAndTrapCard timeSeal = (SpellAndTrapCard) Card.getCardByName("Time Seal");
        timeSeal.setOwner(FIRST_ACCOUNT);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().timeSeal(timeSeal, SECOND_ACCOUNT);
        Assertions.assertFalse(SECOND_ACCOUNT.isAbleToDraw());
        SECOND_ACCOUNT.setAbleToDraw(true);
    }

    @Test
    public void magicJamamerTest() {
        SpellAndTrapCard spellAndTrapCard = (SpellAndTrapCard) Card.getCardByName("Umiiruka");
        spellAndTrapCard.setOwner(FIRST_ACCOUNT);
        SpellAndTrapCard magicJamamer = (SpellAndTrapCard) Card.getCardByName("Magic Jamamer");
        magicJamamer.setOwner(SECOND_ACCOUNT);
        Card toRemove = Card.getCardByName("Fireyarou");
        toRemove.setOwner(SECOND_ACCOUNT);
        ArrayList<SpellAndTrapCard> mySpells = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        ArrayList<SpellAndTrapCard> opponentSpells = SECOND_ACCOUNT.getField().getSpellAndTrapCards();
        ArrayList<Card> opponentHand = SECOND_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(spellAndTrapCard);
        SECOND_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().getSpellAndTrapCards().add(magicJamamer);
        SECOND_ACCOUNT.getField().setHand(new ArrayList<>());
        SECOND_ACCOUNT.getField().getHand().add(toRemove);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().magicJamamer(spellAndTrapCard);
        boolean assertBoolean = FIRST_ACCOUNT.getField().getGraveyard().contains(spellAndTrapCard);
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(mySpells);
        SECOND_ACCOUNT.getField().setSpellAndTrapCards(opponentSpells);
        SECOND_ACCOUNT.getField().setHand(opponentHand);
        Assertions.assertTrue(assertBoolean);
    }

    @Test
    public void solemnWarningTest() {
        SECOND_ACCOUNT.setLP(8000);
        SpellAndTrapCard solemnWarning = (SpellAndTrapCard) Card.getCardByName("Solemn Warning");
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Exploder Dragon");
        solemnWarning.setOwner(SECOND_ACCOUNT);
        monsterCard.setOwner(FIRST_ACCOUNT);
        ArrayList<SpellAndTrapCard> opponentSpells = SECOND_ACCOUNT.getField().getSpellAndTrapCards();
        ArrayList<Card> myHand = FIRST_ACCOUNT.getField().getHand();
        SECOND_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        SECOND_ACCOUNT.getField().getSpellAndTrapCards().add(solemnWarning);
        FIRST_ACCOUNT.getField().getHand().add(monsterCard);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().solemnWarning(monsterCard);
        SECOND_ACCOUNT.getField().setSpellAndTrapCards(opponentSpells);
        FIRST_ACCOUNT.getField().setHand(myHand);
        Assertions.assertEquals(6000, SECOND_ACCOUNT.getLP());
    }

    @Test
    public void callOfTheHauntedTest() {
        SpellAndTrapCard callOfTheHaunted = (SpellAndTrapCard) Card.getCardByName("Call of The Haunted");
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Wattkid");
        callOfTheHaunted.setOwner(FIRST_ACCOUNT);
        monsterCard.setOwner(FIRST_ACCOUNT);
        ArrayList<SpellAndTrapCard> mySpells = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        ArrayList<Card> myGY = FIRST_ACCOUNT.getField().getGraveyard();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().setGraveyard(new ArrayList<>());
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(callOfTheHaunted);
        FIRST_ACCOUNT.getField().getGraveyard().add(monsterCard);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        DuelController.getInstance().callOfTheHaunted(callOfTheHaunted, FIRST_ACCOUNT.getField(), true);
        Assertions.assertTrue(FIRST_ACCOUNT.getField().getMonsterCards().contains(monsterCard));
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(mySpells);
        FIRST_ACCOUNT.getField().setGraveyard(myGY);
    }

    @Test
    public void spellAbsorptionTest() {
        SpellAndTrapCard callOfTheHaunted = (SpellAndTrapCard) Card.getCardByName("Spell Absorption");
        callOfTheHaunted.setOwner(FIRST_ACCOUNT);
        callOfTheHaunted.setActive(true);
        int LP = FIRST_ACCOUNT.getLP();
        FIRST_ACCOUNT.setLP(8000);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(callOfTheHaunted);
        DuelController.getInstance().selfAbsorption();
        Assertions.assertEquals(8500, FIRST_ACCOUNT.getLP());
        FIRST_ACCOUNT.setLP(LP);
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(spellAndTrapCards);
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
        monsterCard.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isActivatingSpellValid();
        Assertions.assertEquals("activate effect is only for spell cards.\r\n", outputStream.toString());
    }

    @Test
    public void errorForActivating3() {
        SpellAndTrapCard callOfTheHaunted = (SpellAndTrapCard) Card.getCardByName("Spell Absorption");
        callOfTheHaunted.setOwner(FIRST_ACCOUNT);
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
        callOfTheHaunted.setOwner(FIRST_ACCOUNT);
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
        callOfTheHaunted.setOwner(FIRST_ACCOUNT);
        callOfTheHaunted.setActive(false);
        Phases phase = DuelController.getInstance().getGame().getCurrentPhase();
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        ArrayList<Card> handBackUp = FIRST_ACCOUNT.getField().getHand();
        ArrayList<SpellAndTrapCard> spellAndTrapCards = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        for (int i = 0; i < 5; i++) {
            SpellAndTrapCard reki = (SpellAndTrapCard) Card.getCardByName("Raigeki");
            reki.setOwner(FIRST_ACCOUNT);
            FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(reki);
        }
        FIRST_ACCOUNT.getField().getHand().add(callOfTheHaunted);
        DuelController.getInstance().getGame().setSelectedCard(callOfTheHaunted);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isActivatingSpellValid();
        DuelController.getInstance().getGame().setCurrentPhase(phase);
        FIRST_ACCOUNT.getField().setHand(handBackUp);
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(spellAndTrapCards);
        DuelController.getInstance().getGame().setSelectedCard(null);
        Assertions.assertEquals("spell card zone is full\r\n", outputStream.toString());
    }

    @Test
    public void activateTest() {
        SpellAndTrapCard inTheCloset = (SpellAndTrapCard) Card.getCardByName("Closed Forest");
        inTheCloset.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSelectedCard(inTheCloset);
        ArrayList<Card> handBackUp = FIRST_ACCOUNT.getField().getHand();
        SpellAndTrapCard field = FIRST_ACCOUNT.getField().getFieldZone();
        Phases phase = DuelController.getInstance().getGame().getCurrentPhase();
        FIRST_ACCOUNT.getField().setFieldZone(null);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        FIRST_ACCOUNT.getField().getHand().add(inTheCloset);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().activateSpell();
        FIRST_ACCOUNT.getField().setHand(handBackUp);
        FIRST_ACCOUNT.getField().setFieldZone(field);
        DuelController.getInstance().getGame().setCurrentPhase(phase);
        Assertions.assertTrue(outputStream.toString().startsWith("spell activated"));
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
        inTheCloset.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSelectedCard(inTheCloset);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isDirectAttackValid();
        Assertions.assertEquals("you can’t attack with this card\r\n", outputStream.toString());
    }

    @Test
    public void directAttackError3() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Wattkid");
        monsterCard.setOwner(FIRST_ACCOUNT);
        Phases phase = DuelController.getInstance().getGame().getCurrentPhase();
        DuelController.getInstance().getGame().setCurrentPhase(Phases.DRAW_PHASE);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> monsterCards = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isDirectAttackValid();
        Assertions.assertEquals("action not allowed in this phase\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setCurrentPhase(phase);
        FIRST_ACCOUNT.getField().setMonsterCards(monsterCards);
    }

    @Test
    public void directAttackError4() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Wattkid");
        monsterCard.setOwner(FIRST_ACCOUNT);
        monsterCard.setAttacked(true);
        Phases phase = DuelController.getInstance().getGame().getCurrentPhase();
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> monsterCards = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isDirectAttackValid();
        Assertions.assertEquals("this card already attacked\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setCurrentPhase(phase);
        FIRST_ACCOUNT.getField().setMonsterCards(monsterCards);
        monsterCard.setAttacked(false);
    }

    @Test
    public void directAttackError5() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Wattkid");
        monsterCard.setOwner(FIRST_ACCOUNT);
        MonsterCard monsterCard1 = (MonsterCard) Card.getCardByName("Wattkid");
        monsterCard.setOwner(FIRST_ACCOUNT);
        Phases phase = DuelController.getInstance().getGame().getCurrentPhase();
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> monsterCards = FIRST_ACCOUNT.getField().getMonsterCards();
        ArrayList<MonsterCard> monsterCards1 = SECOND_ACCOUNT.getField().getMonsterCards();
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().getMonsterCards().add(monsterCard1);
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isDirectAttackValid();
        Assertions.assertEquals("you can’t attack the opponent directly\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setCurrentPhase(phase);
        FIRST_ACCOUNT.getField().setMonsterCards(monsterCards);
        FIRST_ACCOUNT.getField().setMonsterCards(monsterCards1);
    }

    @Test
    public void directAttack() {
        SECOND_ACCOUNT.setLP(8000);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Wattkid");
        monsterCard.setOwner(FIRST_ACCOUNT);
        monsterCard.setAttacked(false);
        Phases phase = DuelController.getInstance().getGame().getCurrentPhase();
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> monsterCards = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        ArrayList<MonsterCard> opponentMonsters = SECOND_ACCOUNT.getField().getMonsterCards();
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().directAttack();
        DuelController.getInstance().getGame().setCurrentPhase(phase);
        FIRST_ACCOUNT.getField().setMonsterCards(monsterCards);
        SECOND_ACCOUNT.getField().setMonsterCards(opponentMonsters);
        Assertions.assertTrue(outputStream.toString().startsWith("you opponent receives " + monsterCard.getThisCardAttackPower() + " battle damage"));
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
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("action not allowed in this phase\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void attackError4() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Slot Machine");
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        monsterCard.setAttacked(true);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("this card already attacked\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
        monsterCard.setAttacked(false);
    }

    @Test
    public void attackError5() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Slot Machine");
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        ArrayList<MonsterCard> opponentMonsters = SECOND_ACCOUNT.getField().getMonsterCards();
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("there is no card to attack here\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
        SECOND_ACCOUNT.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackError6() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Slot Machine");
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Spiral Serpent");
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        ArrayList<MonsterCard> opponentMonsters = SECOND_ACCOUNT.getField().getMonsterCards();
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().getMonsterCards().add(attacked);
        attacked.setAbleToBeRemoved(false);
        DuelController.getInstance().attack(0);
        Assertions.assertEquals("you can’t attack this card\r\n", outputStream.toString());
        attacked.setAbleToBeRemoved(true);
        DuelController.getInstance().getGame().setSelectedCard(null);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
        SECOND_ACCOUNT.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackError7() {
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Slot Machine");
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Spiral Serpent");
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        ArrayList<MonsterCard> opponentMonsters = SECOND_ACCOUNT.getField().getMonsterCards();
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().getMonsterCards().add(attacked);
        monsterCard.setAbleToAttack(false);
        DuelController.getInstance().attack(1);
        Assertions.assertEquals("you can’t attack with this card\r\n", outputStream.toString());
        monsterCard.setAbleToAttack(true);
        DuelController.getInstance().getGame().setSelectedCard(null);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
        SECOND_ACCOUNT.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackInAttackWinTest() {
        SECOND_ACCOUNT.setLP(8000);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Wattkid");
        MonsterCard attacker = (MonsterCard) Card.getCardByName("Baby dragon");
        attacked.reset();
        attacker.reset();
        attacked.setOwner(SECOND_ACCOUNT);
        attacker.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSelectedCard(attacker);
        attacked.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        ArrayList<MonsterCard> opponentMonsters = SECOND_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(attacker);
        SECOND_ACCOUNT.getField().getMonsterCards().add(attacked);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(0);
        Assertions.assertTrue(outputStream.toString().startsWith("your opponent’s monster is destroyed and your opponent receives 200 battle damage"));
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
        SECOND_ACCOUNT.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackInAttackDrawTest() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Silver Fang");
        MonsterCard attacker = (MonsterCard) Card.getCardByName("Baby dragon");
        attacked.reset();
        attacker.reset();
        attacked.setOwner(SECOND_ACCOUNT);
        attacker.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSelectedCard(attacker);
        attacked.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        ArrayList<MonsterCard> opponentMonsters = SECOND_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(attacker);
        SECOND_ACCOUNT.getField().getMonsterCards().add(attacked);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(0);
        Assertions.assertTrue(outputStream.toString().startsWith("both you and your opponent monster cards are destroyed and no one receives damage"));
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
        SECOND_ACCOUNT.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackInAttackLossTest() {
        FIRST_ACCOUNT.setLP(8000);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        MonsterCard attacker = (MonsterCard) Card.getCardByName("Wattkid");
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Baby dragon");
        attacked.reset();
        attacker.reset();
        attacked.setOwner(SECOND_ACCOUNT);
        attacker.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSelectedCard(attacker);
        attacked.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        ArrayList<MonsterCard> opponentMonsters = SECOND_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(attacker);
        SECOND_ACCOUNT.getField().getMonsterCards().add(attacked);
        DuelController.getInstance().attack(0);
        Assertions.assertEquals(7800, FIRST_ACCOUNT.getLP());
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
        SECOND_ACCOUNT.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackInDefenseWinTest() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Wattkid");
        MonsterCard attacker = (MonsterCard) Card.getCardByName("Baby dragon");
        attacked.reset();
        attacker.reset();
        attacked.setOwner(SECOND_ACCOUNT);
        attacker.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSelectedCard(attacker);
        attacked.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_DOWN);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        ArrayList<MonsterCard> opponentMonsters = SECOND_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(attacker);
        SECOND_ACCOUNT.getField().getMonsterCards().add(attacked);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(0);
        Assertions.assertTrue(outputStream.toString().startsWith("opponent’s monster card was Wattkid and the defense position monster is destroyed"));
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
        SECOND_ACCOUNT.getField().setMonsterCards(opponentMonsters);
    }

    @Test
    public void attackInDefenseDrawTest() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Fireyarou");
        MonsterCard attacker = (MonsterCard) Card.getCardByName("Wattkid");
        attacked.reset();
        attacker.reset();
        attacked.setOwner(SECOND_ACCOUNT);
        attacker.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSelectedCard(attacker);
        attacked.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        ArrayList<MonsterCard> opponentMonsters = SECOND_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(attacker);
        SECOND_ACCOUNT.getField().getMonsterCards().add(attacked);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(0);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
        SECOND_ACCOUNT.getField().setMonsterCards(opponentMonsters);
        Assertions.assertTrue(outputStream.toString().startsWith("no card is destroyed"));
    }

    @Test
    public void attackInDefenseLossTest() {
        FIRST_ACCOUNT.setLP(8000);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        MonsterCard attacker = (MonsterCard) Card.getCardByName("Wattkid");
        MonsterCard attacked = (MonsterCard) Card.getCardByName("Axe Raider");
        attacked.reset();
        attacker.reset();
        attacked.setOwner(SECOND_ACCOUNT);
        attacker.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSelectedCard(attacker);
        attacked.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        ArrayList<MonsterCard> opponentMonsters = SECOND_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(attacker);
        SECOND_ACCOUNT.getField().getMonsterCards().add(attacked);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().attack(0);
        Assertions.assertTrue(outputStream.toString().startsWith("no card is destroyed and you received 150 battle damage"));
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
        SECOND_ACCOUNT.getField().setMonsterCards(opponentMonsters);
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
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().flipSummon();
        Assertions.assertEquals("action not allowed in this phase\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void flipSummonError4() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.SECOND_MAIN_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().flipSummon();
        Assertions.assertEquals("you can’t flip summon this card\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void flipSummon() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.SECOND_MAIN_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_DOWN);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().flipSummon();
        Assertions.assertEquals(monsterCard.getMonsterCardModeInField(), MonsterCardModeInField.ATTACK_FACE_UP);
        DuelController.getInstance().getGame().setSelectedCard(null);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
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
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().setPosition(true);
        Assertions.assertEquals("action not allowed in this phase\r\n", outputStream.toString());
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
        DuelController.getInstance().getGame().setSelectedCard(null);
    }

    @Test
    public void setPositionErrorTest4() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.SECOND_MAIN_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().setPosition(true);
        Assertions.assertEquals("this card is already in the wanted position\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void setPositionErrorTest5() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.SECOND_MAIN_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        monsterCard.setChangedPosition(true);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().setPosition(true);
        Assertions.assertEquals("you already changed this card position in this turn\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void setPositionErrorTest6() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.SECOND_MAIN_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        monsterCard.setChangedPosition(false);
        monsterCard.setHasBeenSetOrSummoned(true);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().setPosition(true);
        Assertions.assertEquals("you already summoned/set on this turn\r\n", outputStream.toString());
        DuelController.getInstance().getGame().setSelectedCard(null);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
    }

    @Test
    public void setPosition() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.SECOND_MAIN_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        monsterCard.setChangedPosition(false);
        monsterCard.setHasBeenSetOrSummoned(false);
        ArrayList<MonsterCard> myMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(monsterCard);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        DuelController.getInstance().setPosition(true);
        Assertions.assertEquals(MonsterCardModeInField.ATTACK_FACE_UP, monsterCard.getMonsterCardModeInField());
        DuelController.getInstance().getGame().setSelectedCard(null);
        FIRST_ACCOUNT.getField().setMonsterCards(myMonsters);
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
        blackPendant.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSelectedCard(blackPendant);
        ArrayList<Card> backUpHand = FIRST_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isSummonValid();
        Assertions.assertEquals("you can’t summon this card\r\n", outputStream.toString());
        FIRST_ACCOUNT.getField().setHand(backUpHand);
        DuelController.getInstance().getGame().setSelectedCard(null);
    }

    @Test
    public void errorForSummon3() {
        SpellAndTrapCard blackPendant = (SpellAndTrapCard) Card.getCardByName("Black Pendant");
        blackPendant.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSelectedCard(blackPendant);
        ArrayList<Card> backUpHand = FIRST_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        FIRST_ACCOUNT.getField().getHand().add(blackPendant);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isSummonValid();
        FIRST_ACCOUNT.getField().setHand(backUpHand);
        DuelController.getInstance().getGame().setSelectedCard(null);
        Assertions.assertEquals("you can’t summon this card\r\n", outputStream.toString());
    }

    @Test
    public void errorForSummon4() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.END_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        monsterCard.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<Card> backUpHand = FIRST_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        FIRST_ACCOUNT.getField().getHand().add(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isSummonValid();
        FIRST_ACCOUNT.getField().setHand(backUpHand);
        DuelController.getInstance().getGame().setSelectedCard(null);
        Assertions.assertEquals("action not allowed in this phase\r\n", outputStream.toString());
    }

    @Test
    public void errorForSummon5() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Crab Turtle");
        monsterCard.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<Card> backUpHand = FIRST_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        FIRST_ACCOUNT.getField().getHand().add(monsterCard);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isSummonValid();
        FIRST_ACCOUNT.getField().setHand(backUpHand);
        DuelController.getInstance().getGame().setSelectedCard(null);
        Assertions.assertEquals("you can’t summon this card\r\n", outputStream.toString());
    }

    @Test
    public void errorForSummon6() {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        MonsterCard monsterCard = (MonsterCard) Card.getCardByName("Axe Raider");
        monsterCard.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSelectedCard(monsterCard);
        ArrayList<Card> backUpHand = FIRST_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        FIRST_ACCOUNT.getField().getHand().add(monsterCard);
        ArrayList<MonsterCard> backUpMonsters = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        for (int i = 0; i < 5; i++) {
            MonsterCard monster = (MonsterCard) Card.getCardByName("Axe Raider");
            monster.setOwner(FIRST_ACCOUNT);
            FIRST_ACCOUNT.getField().getMonsterCards().add(monster);

        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DuelController.getInstance().isSummonValid();
        FIRST_ACCOUNT.getField().setHand(backUpHand);
        DuelController.getInstance().getGame().setSelectedCard(null);
        FIRST_ACCOUNT.getField().setMonsterCards(backUpMonsters);
        Assertions.assertEquals("monster card zone is full\r\n", outputStream.toString());
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
        slotMachine.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSelectedCard(slotMachine);
        MonsterCard warriorDaiGrepher = (MonsterCard) Card.getCardByName("Warrior Dai Grepher");
        MonsterCard yomi = (MonsterCard) Card.getCardByName("Yomi Ship");
        warriorDaiGrepher.setOwner(FIRST_ACCOUNT);
        yomi.setOwner(FIRST_ACCOUNT);
        ArrayList<MonsterCard> monsterCards = FIRST_ACCOUNT.getField().getMonsterCards();
        ArrayList<Card> GY = FIRST_ACCOUNT.getField().getGraveyard();
        ArrayList<Card> handBackUp = FIRST_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        FIRST_ACCOUNT.getField().getHand().add(slotMachine);
        FIRST_ACCOUNT.getField().setGraveyard(new ArrayList<>());
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(yomi);
        FIRST_ACCOUNT.getField().getMonsterCards().add(warriorDaiGrepher);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("1\r\n2\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().summonWithTribute();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        boolean assertBoolean = FIRST_ACCOUNT.getField().getGraveyard().contains(yomi) &&
                FIRST_ACCOUNT.getField().getGraveyard().contains(warriorDaiGrepher);
        FIRST_ACCOUNT.getField().setMonsterCards(monsterCards);
        FIRST_ACCOUNT.getField().setGraveyard(GY);
        FIRST_ACCOUNT.getField().setHand(handBackUp);
        DuelController.getInstance().getGame().setSelectedCard(null);
        Assertions.assertTrue(assertBoolean);
    }

    @Test
    public void trapHoleTest() {
        MonsterCard silverFag = (MonsterCard) Card.getCardByName("Silver Fang");
        DuelController.getInstance().getGame().setSelectedCard(silverFag);
        silverFag.setOwner(FIRST_ACCOUNT);
        SpellAndTrapCard trapHole = (SpellAndTrapCard) Card.getCardByName("Trap Hole");
        trapHole.setOwner(FIRST_ACCOUNT);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = SECOND_ACCOUNT.getField().getSpellAndTrapCards();
        ArrayList<Card> handBackUp = FIRST_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setHand(handBackUp);
        FIRST_ACCOUNT.getField().getHand().add(trapHole);
        SECOND_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().getSpellAndTrapCards().add(trapHole);
        ArrayList<MonsterCard> monsterCards = FIRST_ACCOUNT.getField().getMonsterCards();
        ArrayList<Card> GY = FIRST_ACCOUNT.getField().getGraveyard();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(silverFag);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\nno\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().handleTrapHole(silverFag);
        System.setIn(backup);
        FIRST_ACCOUNT.getField().setGraveyard(GY);
        FIRST_ACCOUNT.getField().setMonsterCards(monsterCards);
        SECOND_ACCOUNT.getField().setSpellAndTrapCards(spellAndTrapCards);
        FIRST_ACCOUNT.getField().setHand(handBackUp);
        Assertions.assertNull(DuelController.getInstance().getGame().getSelectedCard());
    }

    @Test
    public void summonTest() {
        MonsterCard fireyarou = (MonsterCard) Card.getCardByName("Fireyarou");
        fireyarou.setOwner(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setSummonedInThisTurn(false);
        DuelController.getInstance().getGame().setSelectedCard(fireyarou);
        ArrayList<Card> handBackUp = FIRST_ACCOUNT.getField().getHand();
        ArrayList<MonsterCard> monsterCardsBackUp = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        FIRST_ACCOUNT.getField().getHand().add(fireyarou);
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        DuelController.getInstance().summon();
        FIRST_ACCOUNT.getField().setHand(handBackUp);
        FIRST_ACCOUNT.getField().setMonsterCards(monsterCardsBackUp);
        DuelController.getInstance().getGame().setSelectedCard(null);
        Assertions.assertTrue(DuelController.getInstance().getGame().isSummonedInThisTurn());
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
        ArrayList<Card> myCards = FIRST_ACCOUNT.getField().getHand();
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        ArrayList<Card> opponentCards = SECOND_ACCOUNT.getField().getHand();
        SECOND_ACCOUNT.getField().setHand(new ArrayList<>());
        DuelController.getInstance().nextPhase();
        Assertions.assertEquals(Phases.DRAW_PHASE, DuelController.getInstance().getGame().getCurrentPhase());
        DuelController.getInstance().getGame().setCurrentPlayer(FIRST_ACCOUNT);
        DuelController.getInstance().getGame().setTheOtherPlayer(SECOND_ACCOUNT);
        FIRST_ACCOUNT.getField().setHand(myCards);
        SECOND_ACCOUNT.getField().setHand(opponentCards);
    }

    @Test
    public void messengerOfPeaceTest() {
        int LP = FIRST_ACCOUNT.getLP();
        FIRST_ACCOUNT.setLP(8000);
        MessengerOfPeace messengerOfPiss = new MessengerOfPeace();
        messengerOfPiss.setOwner(FIRST_ACCOUNT);
        messengerOfPiss.setActive(true);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(messengerOfPiss);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("no\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().handleMessengerOfPeace();
        Assertions.assertEquals(7900, FIRST_ACCOUNT.getLP());
        System.setIn(backup);
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(spellAndTrapCards);
        FIRST_ACCOUNT.setLP(LP);
    }

    @Test
    public void handleHeraldOfCreationTest() {
        MonsterCard heraldOfCreation = (MonsterCard) Card.getCardByName("Herald of Creation");
        heraldOfCreation.setOwner(FIRST_ACCOUNT);
        ArrayList<MonsterCard> monsterCards = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(heraldOfCreation);
        heraldOfCreation.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        ArrayList<Card> hand = FIRST_ACCOUNT.getField().getHand();
        MonsterCard slutMachine = (MonsterCard) Card.getCardByName("Slot Machine");
        FIRST_ACCOUNT.getField().setHand(new ArrayList<>());
        FIRST_ACCOUNT.getField().getHand().add(slutMachine);
        MonsterCard hornymp = (MonsterCard) Card.getCardByName("Horn Imp");
        ArrayList<Card> GY = FIRST_ACCOUNT.getField().getGraveyard();
        FIRST_ACCOUNT.getField().setGraveyard(new ArrayList<>());
        FIRST_ACCOUNT.getField().getGraveyard().add(hornymp);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().handleCommandKnightAndHeraldOfCreation(FIRST_ACCOUNT.getField(), SECOND_ACCOUNT.getField());
        System.setIn(backup);
        FIRST_ACCOUNT.getField().setMonsterCards(monsterCards);
        FIRST_ACCOUNT.getField().setHand(hand);
        FIRST_ACCOUNT.getField().setGraveyard(GY);
        Assertions.assertTrue(heraldOfCreation.isHasBeenUsedInThisTurn());
        heraldOfCreation.setHasBeenUsedInThisTurn(false);
    }

    @Test
    public void handleCommandKnightTest() {
        CommandKnight commandKnight = (CommandKnight) Card.getCardByName("Command Knight");
        commandKnight.setOwner(FIRST_ACCOUNT);
        commandKnight.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        ArrayList<MonsterCard> monsterCards = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getMonsterCards().add(commandKnight);
        MonsterCard slutMachine = (MonsterCard) Card.getCardByName("Slot Machine");
        FIRST_ACCOUNT.getField().getMonsterCards().add(slutMachine);
        DuelController.getInstance().handleCommandKnightAndHeraldOfCreation(FIRST_ACCOUNT.getField(), SECOND_ACCOUNT.getField());
        Assertions.assertEquals(1400, commandKnight.getThisCardAttackPower());
        FIRST_ACCOUNT.getField().setMonsterCards(monsterCards);
    }

    @Test
    public void swordOfRevealingLightTest() {
        SwordsOfRevealingLight swordsOfRevealingLight = (SwordsOfRevealingLight) Card.getCardByName("Swords of Revealing Light");
        swordsOfRevealingLight.setOwner(SECOND_ACCOUNT);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = SECOND_ACCOUNT.getField().getSpellAndTrapCards();
        ArrayList<MonsterCard> monsterCards = FIRST_ACCOUNT.getField().getMonsterCards();
        FIRST_ACCOUNT.getField().setMonsterCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        SECOND_ACCOUNT.getField().getSpellAndTrapCards().add(swordsOfRevealingLight);
        swordsOfRevealingLight.setActive(true);
        MonsterCard heraldOfCreation = (MonsterCard) Card.getCardByName("Herald of Creation");
        FIRST_ACCOUNT.getField().getMonsterCards().add(heraldOfCreation);
        heraldOfCreation.setOwner(FIRST_ACCOUNT);
        heraldOfCreation.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_DOWN);
        DuelController.getInstance().handleSwordOfRevealingLight();
        Assertions.assertEquals(MonsterCardModeInField.DEFENSE_FACE_UP, heraldOfCreation.getMonsterCardModeInField());
        FIRST_ACCOUNT.getField().setMonsterCards(monsterCards);
        SECOND_ACCOUNT.getField().setSpellAndTrapCards(spellAndTrapCards);
    }

    @Test
    public void exchangeWithSideDeckTest() {
        ArrayList<Card> deckZone = FIRST_ACCOUNT.getField().getDeckZone();
        ArrayList<Card> sideDeck = FIRST_ACCOUNT.getField().getSideDeck();
        FIRST_ACCOUNT.getField().setDeckZone(new ArrayList<>());
        FIRST_ACCOUNT.getField().setSideDeck(new ArrayList<>());
        MonsterCard slutMachine = (MonsterCard) Card.getCardByName("Slot Machine");
        slutMachine.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().getDeckZone().add(slutMachine);
        SpellAndTrapCard inTheCloset = (SpellAndTrapCard) Card.getCardByName("Closed Forest");
        inTheCloset.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().getSideDeck().add(inTheCloset);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\nClosed Forest * Slot Machine\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().exchangeCardsWithSideDeck(FIRST_ACCOUNT);
        Assertions.assertTrue(FIRST_ACCOUNT.getField().getSideDeck().contains(slutMachine));
        System.setIn(backup);
        FIRST_ACCOUNT.getField().setDeckZone(deckZone);
        FIRST_ACCOUNT.getField().setSideDeck(sideDeck);
    }

    @Test
    public void canMakeChainTest() {
        SpellAndTrapCard adam = (SpellAndTrapCard) Card.getCardByName("Call of The Haunted");
        adam.setOwner(FIRST_ACCOUNT);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(adam);
        Assertions.assertTrue(DuelController.getInstance().canMakeChain(FIRST_ACCOUNT));
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(spellAndTrapCards);
    }

    @Test
    public void activateCards() {
        SpellAndTrapCard adam = (SpellAndTrapCard) Card.getCardByName("Call of The Haunted");
        adam.setOwner(FIRST_ACCOUNT);
        MonsterCard tadashi = (MonsterCard) Card.getCardByName("Flame manipulator");
        tadashi.setOwner(FIRST_ACCOUNT);
        ArrayList<SpellAndTrapCard> spellAndTrapCards = FIRST_ACCOUNT.getField().getSpellAndTrapCards();
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(new ArrayList<>());
        FIRST_ACCOUNT.getField().getSpellAndTrapCards().add(adam);
        ArrayList<MonsterCard> monsterCards = FIRST_ACCOUNT.getField().getMonsterCards();
        ArrayList<Card> GY = FIRST_ACCOUNT.getField().getGraveyard();
        FIRST_ACCOUNT.getField().setGraveyard(new ArrayList<>());
        FIRST_ACCOUNT.getField().getGraveyard().add(tadashi);
        DuelController.getInstance().getForChain().add(adam);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("yes\r\n1\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        DuelController.getInstance().activateCards();
        Assertions.assertTrue(FIRST_ACCOUNT.getField().getMonsterCards().contains(tadashi));
        FIRST_ACCOUNT.getField().setSpellAndTrapCards(spellAndTrapCards);
        System.setIn(backup);
        FIRST_ACCOUNT.getField().setMonsterCards(monsterCards);
        FIRST_ACCOUNT.getField().setGraveyard(GY);
    }

    @Test
    public void fromFieldZoneTest() {
        SpellAndTrapCard adam = (SpellAndTrapCard) Card.getCardByName("Call of The Haunted");
        adam.setOwner(FIRST_ACCOUNT);
        FIRST_ACCOUNT.getField().setFieldZone(adam);
        DuelController.getInstance().moveSpellOrTrapToGYFromFieldZone(adam);
        Assertions.assertNull(FIRST_ACCOUNT.getField().getFieldZone());
    }

    @Test
    public void cancelTest() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("cancel\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        Assertions.assertTrue(DuelController.getInstance().actionForChain(FIRST_ACCOUNT));
        System.setIn(backup);
    }
}
