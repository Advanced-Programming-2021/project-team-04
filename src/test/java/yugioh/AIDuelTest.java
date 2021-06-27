package yugioh;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import yugioh.controller.DuelController;
import yugioh.controller.ImportAndExport;
import yugioh.controller.MainController;
import yugioh.controller.ShopController;
import yugioh.model.*;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;
import org.junit.jupiter.api.BeforeAll;

import java.util.ArrayList;
import java.util.List;

public class AIDuelTest {

    public static final Account ACCOUNT = new Account("Pvt. Witt", "I_ACTUALLY_SAW_ANOTHER_WORLD_WITCH", "Witt");

    @BeforeAll
    public static void setup() {
        ShopController.getInstance();
        MainController.getInstance().setLoggedIn(ACCOUNT);
        var cardName = "Silver Fang";
        PlayerDeck playerDeck = new PlayerDeck("Damaged");
        for (int i = 0; i < 40; i++)
            playerDeck.addCardToMainDeck(cardName);
        ACCOUNT.addDeck(playerDeck);
        ACCOUNT.setActivePlayerDeck("Damaged");
        MainController.getInstance().newAIDuel(1, AI.AIDifficulty.HARD);
        DuelController.getInstance().getGame().setCurrentPlayer(AI.getInstance());
        DuelController.getInstance().getGame().setTheOtherPlayer(ACCOUNT);
    }

    @Test
    public void summonStrongestMonsterInHandWithNoTributes() throws Exception {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        var initialHand = AI.getInstance().getField().getHand();
        var initialMonsterZone = AI.getInstance().getField().getMonsterCards();
        var newHand = new ArrayList<>(List.of(ImportAndExport.getInstance().readCard("Bitron"),
                ImportAndExport.getInstance().readCard("Bitron"), ImportAndExport.getInstance().readCard("Horn Imp"),
                ImportAndExport.getInstance().readCard("Axe Raider"), ImportAndExport.getInstance().readCard("Silver Fang")));
        newHand.forEach(c -> c.setOwnerUsername(AI.AI_USERNAME));
        AI.getInstance().getField().setHand(newHand);
        AI.getInstance().getField().setMonsterCards(new ArrayList<>());
        var initialHandSize = AI.getInstance().getField().getHand().size();
        AI.getInstance().summonMonster();
        Assertions.assertEquals(initialHandSize - 1, AI.getInstance().getField().getHand().size());
        Assertions.assertEquals("Axe Raider", AI.getInstance().getField().getMonsterCards().get(0).getName());
        AI.getInstance().getField().setHand(initialHand);
        AI.getInstance().getField().setMonsterCards(initialMonsterZone);
    }

    @Test
    public void summonStrongestMonsterInHandWithOneTribute() throws Exception {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        var initialHand = AI.getInstance().getField().getHand();
        var initialMonsterZone = AI.getInstance().getField().getMonsterCards();
        var newHand = new ArrayList<>(List.of(ImportAndExport.getInstance().readCard("Bitron"),
                ImportAndExport.getInstance().readCard("Crawling dragon"), ImportAndExport.getInstance().readCard("BlueEyes white dragon"),
                ImportAndExport.getInstance().readCard("Axe Raider"), ImportAndExport.getInstance().readCard("Wattaildragon")));
        newHand.forEach(c -> c.setOwnerUsername(AI.AI_USERNAME));
        var newMonsterZone = new ArrayList<>(List.of((MonsterCard) ImportAndExport.getInstance().readCard("Bitron")));
        newMonsterZone.forEach(m -> m.setOwnerUsername(AI.AI_USERNAME));
        AI.getInstance().getField().setHand(newHand);
        AI.getInstance().getField().setMonsterCards(newMonsterZone);
        var initialHandSize = AI.getInstance().getField().getHand().size();
        AI.getInstance().summonMonster();
        Assertions.assertEquals(initialHandSize - 1, AI.getInstance().getField().getHand().size());
        Assertions.assertEquals("Wattaildragon", AI.getInstance().getField().getMonsterCards().get(0).getName());
        AI.getInstance().getField().setHand(initialHand);
        AI.getInstance().getField().setMonsterCards(initialMonsterZone);
    }

    @Test
    public void summonStrongestMonsterInHandWithTwoTributes() throws Exception {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        var initialHand = AI.getInstance().getField().getHand();
        var initialMonsterZone = AI.getInstance().getField().getMonsterCards();
        var newHand = new ArrayList<>(List.of(ImportAndExport.getInstance().readCard("Bitron"),
                ImportAndExport.getInstance().readCard("Crawling dragon"), ImportAndExport.getInstance().readCard("BlueEyes white dragon"),
                ImportAndExport.getInstance().readCard("Axe Raider"), ImportAndExport.getInstance().readCard("Wattaildragon")));
        newHand.forEach(c -> c.setOwnerUsername(AI.AI_USERNAME));
        var newMonsterZone = new ArrayList<>(List.of((MonsterCard) ImportAndExport.getInstance().readCard("Bitron"),
                (MonsterCard) ImportAndExport.getInstance().readCard("Axe Raider")));
        newMonsterZone.forEach(m -> m.setOwnerUsername(AI.AI_USERNAME));
        AI.getInstance().getField().setHand(newHand);
        AI.getInstance().getField().setMonsterCards(newMonsterZone);
        var initialHandSize = AI.getInstance().getField().getHand().size();
        AI.getInstance().summonMonster();
        Assertions.assertEquals(initialHandSize - 1, AI.getInstance().getField().getHand().size());
        Assertions.assertEquals("BlueEyes white dragon", AI.getInstance().getField().getMonsterCards().get(0).getName());
        AI.getInstance().getField().setHand(initialHand);
        AI.getInstance().getField().setMonsterCards(initialMonsterZone);
    }

    @Test
    public void activateFieldZoneSpell() throws Exception {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        var initialHand = AI.getInstance().getField().getHand();
        var initialFieldZone = AI.getInstance().getField().getFieldZone();
        var newHand = new ArrayList<>(List.of(ImportAndExport.getInstance().readCard("Closed Forest"),
                ImportAndExport.getInstance().readCard("Crawling dragon"), ImportAndExport.getInstance().readCard("BlueEyes white dragon"),
                ImportAndExport.getInstance().readCard("Axe Raider"), ImportAndExport.getInstance().readCard("Wattaildragon")));
        newHand.forEach(c -> c.setOwnerUsername(AI.AI_USERNAME));
        AI.getInstance().getField().setHand(newHand);
        AI.getInstance().getField().setFieldZone(null);
        AI.getInstance().activateSpell();
        Assertions.assertEquals("Closed Forest", AI.getInstance().getField().getFieldZone().getName());
        AI.getInstance().getField().setHand(initialHand);
        AI.getInstance().getField().setFieldZone(initialFieldZone);
    }

    @Test
    public void activateRandomSpell() throws Exception {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.FIRST_MAIN_PHASE);
        var initialHand = AI.getInstance().getField().getHand();
        var initialSpellZone = AI.getInstance().getField().getSpellAndTrapCards();
        var newHand = new ArrayList<>(List.of(ImportAndExport.getInstance().readCard("Bitron"),
                ImportAndExport.getInstance().readCard("Spell Absorption"), ImportAndExport.getInstance().readCard("BlueEyes white dragon"),
                ImportAndExport.getInstance().readCard("Axe Raider"), ImportAndExport.getInstance().readCard("Wattaildragon")));
        newHand.forEach(c -> c.setOwnerUsername(AI.AI_USERNAME));
        var newMonsterZone = new ArrayList<SpellAndTrapCard>();
        AI.getInstance().getField().setHand(newHand);
        AI.getInstance().getField().setSpellAndTrapCards(newMonsterZone);
        var initialHandSize = AI.getInstance().getField().getHand().size();
        AI.getInstance().activateSpell();
        Assertions.assertEquals(initialHandSize - 1, AI.getInstance().getField().getHand().size());
        Assertions.assertNotNull(AI.getInstance().getField().getSpellAndTrapCards().get(0));
        AI.getInstance().getField().setHand(initialHand);
        AI.getInstance().getField().setSpellAndTrapCards(initialSpellZone);
    }

    @Test
    public void attack() throws Exception {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        var initialAIMonsterZone = AI.getInstance().getField().getMonsterCards();
        var initialPlayerMonsterZone = ACCOUNT.getField().getMonsterCards();
        var newAIMonsterZone = new ArrayList<>(List.of((MonsterCard) ImportAndExport.getInstance().readCard("Bitron"),
                (MonsterCard) ImportAndExport.getInstance().readCard("Axe Raider")));
        var newPlayerMonsterZone = new ArrayList<>(List.of((MonsterCard) ImportAndExport.getInstance().readCard("Feral Imp")));
        newAIMonsterZone.forEach(m -> {
            m.setOwnerUsername(AI.AI_USERNAME);
            m.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        });
        newPlayerMonsterZone.forEach(m -> {
            m.setOwner(ACCOUNT);
            m.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        });
        AI.getInstance().getField().setMonsterCards(newAIMonsterZone);
        ACCOUNT.getField().setMonsterCards(newPlayerMonsterZone);
        var initialPlayerMonsterZoneSize = ACCOUNT.getField().getMonsterCards().size();
        AI.getInstance().attack(ACCOUNT);
        Assertions.assertEquals(initialPlayerMonsterZoneSize - 1, ACCOUNT.getField().getMonsterCards().size());
        AI.getInstance().getField().setMonsterCards(initialAIMonsterZone);
        ACCOUNT.getField().setMonsterCards(initialPlayerMonsterZone);
    }

    @Test
    public void directAttack() throws Exception {
        DuelController.getInstance().getGame().setCurrentPhase(Phases.BATTLE_PHASE);
        var initialAIMonsterZone = AI.getInstance().getField().getMonsterCards();
        var initialPlayerMonsterZone = ACCOUNT.getField().getMonsterCards();
        var newAIMonsterZone = new ArrayList<>(List.of((MonsterCard) ImportAndExport.getInstance().readCard("Bitron"),
                (MonsterCard) ImportAndExport.getInstance().readCard("Axe Raider")));
        var newPlayerMonsterZone = new ArrayList<MonsterCard>();
        newAIMonsterZone.forEach(m -> {
            m.setOwnerUsername(AI.AI_USERNAME);
            m.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        });
        AI.getInstance().getField().setMonsterCards(newAIMonsterZone);
        ACCOUNT.getField().setMonsterCards(newPlayerMonsterZone);
        var initialPlayerLP = ACCOUNT.getLP();
        AI.getInstance().attack(ACCOUNT);
        Assertions.assertEquals(initialPlayerLP - 1900, ACCOUNT.getLP());
        AI.getInstance().getField().setMonsterCards(initialAIMonsterZone);
        ACCOUNT.getField().setMonsterCards(initialPlayerMonsterZone);
    }
}