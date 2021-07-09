package yugioh.controller;

import lombok.Getter;
import lombok.Setter;
import yugioh.model.*;
import yugioh.model.cards.*;
import yugioh.model.cards.specialcards.Scanner;
import yugioh.model.cards.specialcards.*;
import yugioh.view.DuelView;
import yugioh.view.IO;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class DuelController {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static DuelController singleInstance = null;

    private ArrayList<MonsterCard> deactivatedCards = new ArrayList<>();
    private Game game;
    private ArrayList<SpellAndTrapCard> forChain = new ArrayList<>();
    private boolean isChainActive = false;
    private ArrayList<Scanner> toReset = new ArrayList<>();

    public static DuelController getInstance() {
        if (singleInstance == null)
            singleInstance = new DuelController();
        return singleInstance;
    }

    public void coin() {
        if (RANDOM.nextInt(2) == 1) DuelView.getInstance().chooseStarter(game.getCurrentPlayerUsername());
        else DuelView.getInstance().chooseStarter(game.getTheOtherPlayerUsername());
    }

    public boolean chooseStarter(String username) {
        if (game.getTheOtherPlayer().getUsername().equals(username)) game.changeTurn();
        else if (!game.getCurrentPlayer().getUsername().equals(username)) {
            IO.getInstance().notAPlayer(username);
            return false;
        }
        game.getCurrentPlayer().setAbleToDraw(false);
        game.getCurrentPlayer().setAbleToAttack(false);
        drawPhase();
        return true;
    }

    public void drawPhase() {
        IO.getInstance().printPhase("draw\nphase");
        if (game.getTheOtherPlayer().getField().getSetSpellAndTrapCard(CardEffects.TIME_SEAL) != null)
            game.getCurrentPlayer().setAbleToDraw(false);
        if (!game.getCurrentPlayer().isAbleToDraw()) {
            game.getCurrentPlayer().setAbleToDraw(true);
            return;
        }
        var field = game.getCurrentPlayer().getField();
        if (field.getDeckZone().isEmpty()) {
            game.finishGame(game.getCurrentPlayer());
            return;
        }
        if (field.getHand().size() == 6) return;
        field.getHand().add(field.getDeckZone().get(0));
        field.getDeckZone().remove(0);
    }

    private void standbyPhase() {
        IO.getInstance().printPhase("standby\nphase");
        handleSpecialCards(game.getCurrentPlayer().getField(), game.getTheOtherPlayer().getField());
        ArrayList<Scanner> scanners = game.getCurrentPlayer().getField().getActiveScanners();
        scanners.forEach(singleInstance::forScanner);
    }

    private void handleSpecialCards(Field field, Field opponentField) {
        handleField(field, opponentField);
        handleEquip(field);
        handleTimeSeal(field, opponentField);
        handleMindCrush(field, opponentField);
        handleCallOfTheHaunted(field, opponentField);
        handleCommandKnightAndHeraldOfCreation(field, opponentField);
        handleMessengerOfPeace();
    }

    public void handleCommandKnightAndHeraldOfCreation(Field field, Field opponentField) {
        ArrayList<MonsterCard> myMonsters = new ArrayList<>(field.getMonsterCards());
        ArrayList<MonsterCard> opponentMonsters = new ArrayList<>(opponentField.getMonsterCards());
        myMonsters.forEach(m -> {
            if (m.getName().contains(CardEffects.HERALD_OF_CREATION) && !m.isHasBeenUsedInThisTurn() &&
                    !(m.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_DOWN))) {
                heraldOfCreation();
                m.setHasBeenUsedInThisTurn(true);
            }
            if (m.getName().equals(CardEffects.COMMAND_KNIGHT)) {
                ((CommandKnight) m).specialMethod();
                m.setHasBeenUsedInThisTurn(true);
            }
        });
        opponentMonsters.stream().filter(m -> m.getName().equals(CardEffects.COMMAND_KNIGHT)).forEach(m -> {
            ((CommandKnight) m).specialMethod();
            m.setHasBeenUsedInThisTurn(true);
        });
    }

    private void handleCallOfTheHaunted(Field field, Field opponentField) {
        SpellAndTrapCard callOfTheHauntedCard = field.getSpellAndTrapCard(CardEffects.CALL_OF_THE_HAUNTED);
        if (callOfTheHauntedCard != null) callOfTheHaunted(callOfTheHauntedCard, field, true);
        callOfTheHauntedCard = opponentField.getSpellAndTrapCard(CardEffects.CALL_OF_THE_HAUNTED);
        if (callOfTheHauntedCard != null) callOfTheHaunted(callOfTheHauntedCard, opponentField, false);
    }

    private void handleMindCrush(Field field, Field opponentField) {
        SpellAndTrapCard mindCrushCard = field.getSpellAndTrapCard(CardEffects.MIND_CRUSH);
        if (mindCrushCard != null) mindCrush(mindCrushCard, game.getTheOtherPlayer());
        mindCrushCard = opponentField.getSpellAndTrapCard(CardEffects.MIND_CRUSH);
        if (mindCrushCard != null) mindCrush(mindCrushCard, game.getCurrentPlayer());
    }

    private void handleTimeSeal(Field field, Field opponentField) {
        SpellAndTrapCard timeSealCard = field.getSpellAndTrapCard(CardEffects.TIME_SEAL);
        if (timeSealCard != null) timeSeal(timeSealCard, game.getTheOtherPlayer());
        timeSealCard = opponentField.getSpellAndTrapCard(CardEffects.TIME_SEAL);
        if (timeSealCard != null) timeSeal(timeSealCard, game.getCurrentPlayer());
    }

    private void handleEquip(Field field) { //TODO
        var swordOfDarkDestruction = (SwordOfDarkDestruction) field.getThisActivatedCard(CardEffects.SWORD_OF_DARK_DESTRUCTION);
        if (swordOfDarkDestruction != null) {
            swordOfDarkDestruction.equipMonster();
        }
        var blackPendant = (BlackPendant) field.getThisActivatedCard(CardEffects.BLACK_PENDANT);
        if (blackPendant != null) blackPendant.equipMonster();
        var unitedWeStand = (UnitedWeStand) field.getThisActivatedCard(CardEffects.UNITED_WE_STAND);
        if (unitedWeStand != null) unitedWeStand.equipMonster();
        var magnumShield = (MagnumShield) field.getThisActivatedCard(CardEffects.MAGNUM_SHIELD);
        if (magnumShield != null) magnumShield.equipMonster();
    }

    private void handleField(Field field, Field opponentField) {
        if ((field.getFieldZone() != null && field.getFieldZone().getName().contains(CardEffects.UMIIRUKA)) ||
                (opponentField.getFieldZone() != null && opponentField.getFieldZone().getName().contains(CardEffects.UMIIRUKA)))
            umiiruka();
        if ((field.getFieldZone() != null && field.getFieldZone().getName().contains(CardEffects.FOREST)) ||
                (opponentField.getFieldZone() != null && opponentField.getFieldZone().getName().contains(CardEffects.FOREST)))
            forest();
        if ((field.getFieldZone() != null && field.getFieldZone().getName().contains(CardEffects.CLOSED_FOREST)) ||
                (opponentField.getFieldZone() != null && opponentField.getFieldZone().getName().contains(CardEffects.CLOSED_FOREST)))
            closedForest();
        if ((field.getFieldZone() != null && field.getFieldZone().getName().contains(CardEffects.YAMI)) ||
                (opponentField.getFieldZone() != null && opponentField.getFieldZone().getName().contains(CardEffects.YAMI)))
            yami();
    }

    public void handleMessengerOfPeace() {
        SpellAndTrapCard myMessengerOfPeace = game.getCurrentPlayer().getField().getThisActivatedCard(CardEffects.MESSENGER_OF_PEACE);
        if (myMessengerOfPeace != null) {
            if (DuelView.getInstance().killMessengerOfPeace())
                moveSpellOrTrapToGYFromSpellZone(myMessengerOfPeace);
            else {
                messengerOfPeace();
                game.getCurrentPlayer().changeLP(-100);
            }
        }
        SpellAndTrapCard opponentMessengerOfPeace = game.getTheOtherPlayer().getField().getThisActivatedCard(CardEffects.MESSENGER_OF_PEACE);
        if (opponentMessengerOfPeace != null)
            messengerOfPeace();
    }


    private String[] makeSpellZone(ArrayList<SpellAndTrapCard> spellZone) {
        var spellZoneString = new String[5];
        for (var i = 0; i < 5; i++) {
            if (spellZone.size() - 1 < i) {
                spellZoneString[i] = "E ";
                continue;
            }
            var spellAndTrapCard = spellZone.get(i);
            if (spellAndTrapCard.isActive()) spellZoneString[i] = "O ";
            else spellZoneString[i] = "H ";
        }
        return spellZoneString;
    }

    private String[] makeMonsterZone(ArrayList<MonsterCard> monsterZone) {
        var monsterZoneString = new String[5];
        for (var i = 0; i < 5; i++) {
            if (monsterZone.size() - 1 < i) {
                monsterZoneString[i] = "E ";
                continue;
            }
            var monsterCard = monsterZone.get(i);
            if (monsterCard.getMonsterCardModeInField() == MonsterCardModeInField.ATTACK_FACE_UP)
                monsterZoneString[i] = "OO";
            else if (monsterCard.getMonsterCardModeInField() == MonsterCardModeInField.DEFENSE_FACE_UP)
                monsterZoneString[i] = "DO";
            else monsterZoneString[i] = "DH";
        }
        return monsterZoneString;
    }


    private void firstMainPhase() {
        IO.getInstance().printPhase("main\nphase 1");
    }

    public void battlePhase() {
        IO.getInstance().printPhase("battle\nphase");
        if (!game.getCurrentPlayer().isAbleToAttack()) {
            game.getCurrentPlayer().setAbleToAttack(true);
            nextPhase();
        }
    }

    private void secondMainPhase() {
        IO.getInstance().printPhase("main\nphase 2");
    }

    public void endPhase() {
        IO.getInstance().printPhase("end\nphase");
        reset();
        handleSwordOfRevealingLight();
    }

    private void reset() {
        game.getCurrentPlayer().getField().resetAllCards();
        game.getTheOtherPlayer().getField().resetAllCards();
        toReset.forEach(Scanner::reset);
        resetMessengerOfPeace();
        toReset = new ArrayList<>();
    }

    private void resetMessengerOfPeace() {
        deactivatedCards.forEach(m -> m.setAbleToAttack(true));
        deactivatedCards = new ArrayList<>();
    }

    public void handleSwordOfRevealingLight() {
        SwordsOfRevealingLight sword = (SwordsOfRevealingLight) game.getTheOtherPlayer().getField().getThisActivatedCard(CardEffects.SWORDS_OF_REVEALING_LIGHT);
        if (sword != null) {
            sword.setCounter(sword.getCounter() + 1);
            if (sword.getCounter() == 3) {
                moveSpellOrTrapToGYFromSpellZone(sword);
                sword.setCounter(0);
            } else {
                game.getCurrentPlayer().getField().getMonsterCards().stream()
                        .filter(m -> m.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_DOWN))
                        .forEach(m -> m.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP));
            }
        }
    }

    public void selectCard(boolean myCard, CardStatusInField cardStatusInField, int number) {
        Duelist thisPlayer = getPlayerByCard(myCard);
        if (!errorForSelecting(thisPlayer, number, cardStatusInField)) return;
        switch (cardStatusInField) {
            case HAND -> game.setSelectedCard(thisPlayer.getField().getHand().get(number));
            case MONSTER_FIELD -> game.setSelectedCard(thisPlayer.getField().getMonsterCards().get(number));
            case SPELL_FIELD -> game.setSelectedCard(thisPlayer.getField().getSpellAndTrapCards().get(number));
            case FIELD_ZONE -> game.setSelectedCard(thisPlayer.getField().getFieldZone());
        }
    }


    private Duelist getPlayerByCard(boolean myCard) {
        return myCard ? game.getCurrentPlayer() : game.getTheOtherPlayer();
    }

    private boolean errorForSelecting(Duelist thisPlayer, int number, CardStatusInField cardStatusInField) {
        if (cardStatusInField.equals(CardStatusInField.HAND) &&
                (number >= thisPlayer.getField().getHand().size() || number < 0)) {
            return false;
        }
        if (cardStatusInField.equals(CardStatusInField.MONSTER_FIELD) &&
                (number >= thisPlayer.getField().getMonsterCards().size() || number < 0)) {
            return false;
        }
        if (cardStatusInField.equals(CardStatusInField.SPELL_FIELD) &&
                (number >= thisPlayer.getField().getSpellAndTrapCards().size() || number < 0)) {
            return false;
        }
        return !cardStatusInField.equals(CardStatusInField.FIELD_ZONE) ||
                thisPlayer.getField().getFieldZone() != null;
    }

    public void deselectCard() {
        if (game.getSelectedCard() == null) {
            IO.getInstance().cardNotSelected();
            return;
        }
        game.setSelectedCard(null);
    }

    public void nextPhase() {
        switch (game.getCurrentPhase()) {
            case DRAW_PHASE -> {
                game.setCurrentPhase(Phases.STANDBY_PHASE);
                standbyPhase();
            }
            case STANDBY_PHASE -> {
                game.setCurrentPhase(Phases.FIRST_MAIN_PHASE);
                firstMainPhase();
            }
            case FIRST_MAIN_PHASE -> {
                game.setCurrentPhase(Phases.BATTLE_PHASE);
                battlePhase();
            }
            case BATTLE_PHASE -> {
                game.setCurrentPhase(Phases.SECOND_MAIN_PHASE);
                secondMainPhase();
            }
            case SECOND_MAIN_PHASE -> {
                game.setCurrentPhase(Phases.END_PHASE);
                endPhase();
            }
            case END_PHASE -> {
                game.setCurrentPhase(Phases.DRAW_PHASE);
                game.changeTurn();
                drawPhase();
            }
        }
        if (getGame().getCurrentPlayer() instanceof AI) handleAI();
    }

    private void handleAI() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException ignored) {
        }
        switch (getGame().getCurrentPhase()) {
            case FIRST_MAIN_PHASE -> {
                AI.getInstance().activateSpell();
                AI.getInstance().summonMonster();
            }
            case BATTLE_PHASE -> AI.getInstance().attack(getGame().getTheOtherPlayer());
            case SECOND_MAIN_PHASE -> AI.getInstance().activateSpell();
        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException ignored) {
        }
        nextPhase();
    }

    public void setOrSummon() {
        if (!(game.getSelectedCard() instanceof MonsterCard)) {
            IO.getInstance().cantSummon();
            return;
        }
        var selectedMonsterCard = (MonsterCard) game.getSelectedCard();
        if (selectedMonsterCard.getThisCardAttackPower() >= selectedMonsterCard.getThisCardDefensePower())
            DuelView.summon();
        else DuelView.set();
    }

    public void summon() {
        if (handleSpecialCasesBeforeSummon()) return;
        summonWithTribute();
        var monsterCard = (MonsterCard) game.getSelectedCard();
        monsterCard.getOwner().getField().getHand().remove(monsterCard);
        if (handleTrapHole(monsterCard)) return;
        game.getCurrentPlayer().getField().getMonsterCards().add(monsterCard);
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        handleSpecialCasesAfterSummon();
        game.getSelectedCard().setHasBeenSetOrSummoned(true);
        game.setSelectedCard(null);
        game.setSummonedInThisTurn(true);
        torrentialTribute();
    }

    private boolean handleSpecialCasesBeforeSummon() {
        if (getGame().getCurrentPlayer() instanceof Account && !isSummonValid()) return true;
        if (game.getSelectedCard().getName().contains(CardEffects.THE_TRICKY) && DuelView.getInstance().ordinaryOrSpecial()) {
            theTricky();
            return true;
        }
        if (game.getSelectedCard().getName().contains(CardEffects.GATE_GUARDIAN)) {
            if (DuelView.getInstance().summonGateGuardian()) {
                gateGuardian();
            }
            return true;
        }
        if (game.getSelectedCard().getName().contains(CardEffects.BEAST_KING_BARBAROS)) {
            int howToSummon = DuelView.getInstance().barbaros();
            if (howToSummon != 1) {
                barbaros(howToSummon);
                return true;
            }
        }
        if (solemnWarning((MonsterCard) game.getSelectedCard())) {
            game.setSelectedCard(null);
            return true;
        }
        ((MonsterCard) game.getSelectedCard()).changeAttackPower(
                (int) (Stream.concat(
                        game.getCurrentPlayer().getField().getMonsterCards().stream(),
                        game.getTheOtherPlayer().getField().getMonsterCards().stream())
                        .filter(CommandKnight.class::isInstance).count()
                        * 400));
        return false;
    }

    private void handleSpecialCasesAfterSummon() {
        if (game.getSelectedCard().getName().contains(CardEffects.TERRATIGER_THE_EMPOWERED_WARRIOR))
            terraTigerMethod();
        else if (game.getSelectedCard() instanceof CommandKnight)
            ((CommandKnight) game.getSelectedCard()).specialMethod();
    }

    public boolean handleTrapHole(MonsterCard monsterCard) {
        SpellAndTrapCard trapHoleCard = game.getTheOtherPlayer().getField().getSpellAndTrapCard(CardEffects.TRAP_HOLE);
        if (monsterCard.getClassAttackPower() >= 1000 && trapHoleCard != null &&
                DuelView.getInstance().wantsToActivateTrap(CardEffects.TRAP_HOLE, trapHoleCard.getOwner().getUsername())) {
            if (!getGame().isAI())
                makeChain(game.getCurrentPlayer(), game.getTheOtherPlayer());
            moveSpellOrTrapToGYFromSpellZone(trapHoleCard);
            addMonsterToGYFromMonsterZone(monsterCard);
            game.setSelectedCard(null);
            return true;
        }
        return false;
    }

    public void summonWithTribute() {
        var monsterCard = (MonsterCard) game.getSelectedCard();
        if (monsterCard.getLevel() == 5 || monsterCard.getLevel() == 6) {
            int number = getGame().getCurrentPlayer() instanceof AI ? AI.getInstance().getWeakestMonsterCardInZone() : DuelView.getInstance().getTribute();
            if (isTributeValid(number))
                addMonsterToGYFromMonsterZone(game.getCurrentPlayer().getField().getMonsterCards().get(number));
        } else if (monsterCard.getLevel() == 7 || monsterCard.getLevel() == 8) {
            int firstNumber = getGame().getCurrentPlayer() instanceof AI ? AI.getInstance().getWeakestMonsterCardInZone() : DuelView.getInstance().getTribute();
            int secondNumber = getGame().getCurrentPlayer() instanceof AI ? AI.getInstance().getSecondWeakestMonsterCardInZone() : DuelView.getInstance().getTribute();
            var firstTribute = game.getCurrentPlayer().getField().getMonsterCards().get(firstNumber);
            var secondTribute = game.getCurrentPlayer().getField().getMonsterCards().get(secondNumber);
            if (isTributeValid(firstNumber) && isTributeValid(secondNumber) && firstNumber != secondNumber) {
                addMonsterToGYFromMonsterZone(firstTribute);
                addMonsterToGYFromMonsterZone(secondTribute);
            }
        }
    }

    public boolean isTributeValid(int number) {
        return game.getCurrentPlayer().getField().getMonsterCards().size() > number;
    }

    public boolean isSummonValid() {
        if (game.getSelectedCard() == null) {
            IO.getInstance().cardNotSelected();
            return false;
        }
        if (!game.getCurrentPlayer().getField().getHand().contains(game.getSelectedCard()) ||
                !(game.getSelectedCard() instanceof MonsterCard)) {
            IO.getInstance().cantSummon();
            return false;
        }
        if (!(game.getCurrentPhase() == Phases.FIRST_MAIN_PHASE || game.getCurrentPhase() == Phases.SECOND_MAIN_PHASE)) {
            IO.getInstance().wrongPhase();
            return false;
        }
        var monsterCard = (MonsterCard) game.getSelectedCard();
        if (monsterCard.getCardType().equals("Ritual")) {
            IO.getInstance().cantSummon();
            return false;
        }
        if (game.getCurrentPlayer().getField().getMonsterCards().size() == 5) {
            IO.getInstance().monsterZoneFull();
            return false;
        }
        if (game.isSummonedInThisTurn()) {
            IO.getInstance().alreadySummonedOrSet();
            return false;
        }
        if (((monsterCard.getLevel() == 5 || monsterCard.getLevel() == 6) && game.getCurrentPlayer().getField().getMonsterCards().isEmpty()) ||
                ((monsterCard.getLevel() >= 7) && game.getCurrentPlayer().getField().getMonsterCards().size() < 2)) {
            IO.getInstance().notEnoughTribute();
            return false;
        }
        return true;
    }

    public void setMonster() {
        if (!isSettingMonsterValid()) return;
        MonsterCard selectedCard = (MonsterCard) game.getSelectedCard();
        if (selectedCard.getName().contains(CardEffects.GATE_GUARDIAN)) {
            IO.getInstance().cannotSet();
            return;
        }
        summonWithTribute();
        game.getCurrentPlayer().getField().getMonsterCards().add(selectedCard);
        game.getCurrentPlayer().getField().getHand().remove(selectedCard);
        selectedCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_DOWN);
        game.getSelectedCard().setHasBeenSetOrSummoned(true);
        game.setSelectedCard(null);
        game.setSummonedInThisTurn(true);
    }

    private boolean isSettingMonsterValid() {
        if (game.getSelectedCard() == null) {
            IO.getInstance().cardNotSelected();
            return false;
        }
        if (!game.getCurrentPlayer().getField().getHand().contains(game.getSelectedCard())) {
            IO.getInstance().cannotSet();
            return false;
        }
        MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
        if (monsterCard.getCardType().equals("Ritual")) {
            IO.getInstance().cannotSet();
            return false;
        }
        if (!(game.getCurrentPhase() == Phases.FIRST_MAIN_PHASE ||
                game.getCurrentPhase() == Phases.SECOND_MAIN_PHASE)) {
            IO.getInstance().wrongPhase();
            return false;
        }
        if (game.getCurrentPlayer().getField().getMonsterCards().size() == 5) {
            IO.getInstance().monsterZoneFull();
            return false;
        }
        if (game.isSummonedInThisTurn()) {
            IO.getInstance().alreadySummonedOrSet();
            return false;
        }
        if ((monsterCard.getLevel() == 5 || monsterCard.getLevel() == 6) &&
                game.getCurrentPlayer().getField().getMonsterCards().isEmpty()) {
            IO.getInstance().notEnoughTribute();
            return false;
        }
        if ((monsterCard.getLevel() >= 7) &&
                game.getCurrentPlayer().getField().getMonsterCards().size() < 2) {
            IO.getInstance().notEnoughTribute();
            return false;
        }
        return true;
    }

    public void setPosition(boolean isAttack) {
        if (!isSetPositionValid(isAttack)) return;
        MonsterCard selectedCard = (MonsterCard) game.getSelectedCard();
        if (isAttack) selectedCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        else selectedCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        if (selectedCard instanceof CommandKnight)
            ((CommandKnight) selectedCard).specialMethod();
        game.setSelectedCard(null);
    }

    private boolean isSetPositionValid(boolean isAttack) {
        if (checkChangingModeInFieldError()) return false;
        MonsterCard selectedCard = (MonsterCard) game.getSelectedCard();
        if ((isAttack && selectedCard.getMonsterCardModeInField() == MonsterCardModeInField.ATTACK_FACE_UP) ||
                (!isAttack && (selectedCard.getMonsterCardModeInField() == MonsterCardModeInField.DEFENSE_FACE_UP ||
                        selectedCard.getMonsterCardModeInField() == MonsterCardModeInField.DEFENSE_FACE_DOWN))) {
            return false;
        }
        if (selectedCard.isHasBeenSetOrSummoned()) {
            IO.getInstance().alreadySummonedOrSet();
            return false;
        }
        if (selectedCard.isChangedPosition()) {
            IO.getInstance().alreadyChangedPosition();
            return false;
        }
        return true;
    }

    private boolean checkChangingModeInFieldError() {
        if (game.getSelectedCard() == null) {
            IO.getInstance().cardNotSelected();
            return true;
        }
        if (!(game.getSelectedCard() instanceof MonsterCard) ||
                !game.getCurrentPlayer().getField().getMonsterCards().contains(game.getSelectedCard())) {
            IO.getInstance().cannotChangePosition();
            return true;
        }
        if (!(game.getCurrentPhase() == Phases.FIRST_MAIN_PHASE || game.getCurrentPhase() == Phases.SECOND_MAIN_PHASE)) {
            IO.getInstance().wrongPhase();
            return true;
        }
        return false;
    }

    public void flipSummon() {
        if (!isFlipSummonValid()) return;
        MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
        if (handleTrapHole(monsterCard)) return;
        if (monsterCard.getName().contains(CardEffects.MAN_EATER_BUG)) forManEaterBug();
        game.setSelectedCard(null);
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
    }

    private boolean isFlipSummonValid() {
        if (checkChangingModeInFieldError()) return false;
        if (!((MonsterCard) game.getSelectedCard()).getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_DOWN)) {
            IO.getInstance().cannotFlipSummon();
            return false;
        }
        return true;
    }

    public void attack(int opponentMonsterPositionNumber) {
        if (getGame().getCurrentPlayer() instanceof Account && !isAttackValid(opponentMonsterPositionNumber)) return;
        if (negateAttack()) {
            nextPhase();
            return;
        }
        MonsterCard attacked = game.getTheOtherPlayer().getField().getMonsterCards().get(opponentMonsterPositionNumber);
        MonsterCard attacker = (MonsterCard) game.getSelectedCard();
        if (game.getTheOtherPlayer().getField().getThisActivatedCard(CardEffects.SWORDS_OF_REVEALING_LIGHT) != null) return;
        SpellAndTrapCard magicCylinderCard = game.getTheOtherPlayer().getField().getSpellAndTrapCard(CardEffects.MAGIC_CYLINDER);
        if (magicCylinderCard != null) {
            magicCylinder(attacker, magicCylinderCard);
            return;
        }
        SpellAndTrapCard mirrorForceCard = game.getTheOtherPlayer().getField().getSpellAndTrapCard(CardEffects.MIRROR_FORCE);
        if (mirrorForceCard != null) {
            mirrorForce(mirrorForceCard, attacker.getOwner());
            return;
        }
        if (attacked.getName().contains(CardEffects.SUIJIN) &&
                DuelView.getInstance().wantsToActivate(attacked.getName())) {
            handleSuijin(attacked, attacker);
        }
        if (attacked.getName().contains(CardEffects.TEXCHANGER) &&
                DuelView.getInstance().wantsToActivate(attacked.getName())) {
            texChanger(attacked);
            return;
        }
        if (attacker.getName().contains(CardEffects.THE_CALCULATOR))
            handleCalculator(attacker);
        if (attacked.getMonsterCardModeInField().equals(MonsterCardModeInField.ATTACK_FACE_UP))
            handleCalculator(attacked);
        if (attacked.getMonsterCardModeInField() == MonsterCardModeInField.ATTACK_FACE_UP)
            attackInAttack(attacked, attacker);
        else attackInDefense(attacked, attacker);
        attacker.setAttacked(true);
        game.setSelectedCard(null);
    }

    private void handleCalculator(MonsterCard monsterCard) {
        var amount = 0;
        Duelist player = monsterCard.getOwner();
        for (MonsterCard m : player.getField().getMonsterCards())
            if (m.getMonsterCardModeInField().equals(MonsterCardModeInField.ATTACK_FACE_UP)
                    || m.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_UP))
                amount += m.getLevel();
        monsterCard.setThisCardAttackPower(amount * 300);
    }

    private void handleSuijin(MonsterCard attacked, MonsterCard attacker) {
        if (!attacked.isHasBeenUsedInGeneral()) {
            attacker.setThisCardAttackPower(0);
            attacked.setHasBeenUsedInGeneral(true);
        }
    }

    public boolean handleMirageDragon(String name) {
        Duelist opponent;
        opponent = game.getCurrentPlayer().getUsername().equals(name) ? game.getTheOtherPlayer() : game.getCurrentPlayer();
        MonsterCard mirageDragon = opponent.getMirageDragon();
        return mirageDragon != null && mirageDragon.getMonsterCardModeInField() != MonsterCardModeInField.DEFENSE_FACE_DOWN;
    }

    private boolean negateAttack() {
        SpellAndTrapCard negateAttackCard = game.getTheOtherPlayer().getField().getSpellAndTrapCard(CardEffects.NEGATE_ATTACK);
        if (negateAttackCard != null && DuelView.getInstance().wantsToActivate(CardEffects.NEGATE_ATTACK)) {
            if (!getGame().isAI())
                makeChain(game.getCurrentPlayer(), game.getTheOtherPlayer());
            moveSpellOrTrapToGYFromSpellZone(negateAttackCard);
            return true;
        }
        return false;
    }

    private void attackInDefense(MonsterCard attacked, MonsterCard attacker) {
        if (attacked.getName().contains(CardEffects.MARSHMALLON)) game.getCurrentPlayer().changeLP(-1000);
        if (attacked.getMonsterCardModeInField() == MonsterCardModeInField.DEFENSE_FACE_DOWN && attacked.getThisCardDefensePower() < attacker.getThisCardAttackPower()) {
            moveToGraveyardAfterAttack(attacked, attacker);
            return;
        }
        if (attacked.getThisCardDefensePower() == attacker.getThisCardAttackPower()) {
            attacked.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
            return;
        }
        int damage = attacker.getThisCardAttackPower() - attacked.getThisCardDefensePower();
        game.getCurrentPlayer().changeLP(damage);
        attacked.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
    }

    private void attackInAttack(MonsterCard attacked, MonsterCard attacker) {
        if (attacked.getThisCardAttackPower() < attacker.getThisCardAttackPower()) {
            moveToGraveyardAfterAttack(attacked, attacker);
            if (!attacked.getName().contains(CardEffects.EXPLODER_DRAGON)) {
                int damage = attacked.getThisCardAttackPower() - attacker.getThisCardAttackPower();
                game.getTheOtherPlayer().changeLP(damage);
            }
            return;
        }
        if (attacked.getThisCardAttackPower() == attacker.getThisCardAttackPower()) {
            moveToGraveyardAfterAttack(attacker, attacked);
            moveToGraveyardAfterAttack(attacked, attacker);
            return;
        }
        if (!attacker.getName().contains(CardEffects.EXPLODER_DRAGON)) {
            int damage = attacker.getThisCardAttackPower() - attacked.getThisCardAttackPower();
            game.getCurrentPlayer().changeLP(damage);
        }
        moveToGraveyardAfterAttack(attacker, attacked);
    }

    private boolean isAttackValid(int opponentMonsterPositionNumber) {
        if (checkAttackErrorsInCommon()) return false;
        var selectedMonster = (MonsterCard) getGame().getSelectedCard();
        if (!selectedMonster.isAbleToAttack()) {
            IO.getInstance().cannotAttack();
            return false;
        }
        if (game.getTheOtherPlayer().getField().getMonsterCards().size() <= opponentMonsterPositionNumber) {
            IO.getInstance().noCardHere();
            return false;
        }
        if (!game.getTheOtherPlayer().getField().getMonsterCards().get(opponentMonsterPositionNumber).isAbleToBeRemoved()) {
            IO.getInstance().cannotAttackThisCard();
            return false;
        }
        if (selectedMonster.isAttacked()) {
            IO.getInstance().cannotAttack();
            return false;
        }
        return true;
    }

    public void directAttack() {
        if (!isDirectAttackValid()) return;
        MonsterCard selectedCard = (MonsterCard) game.getSelectedCard();
        int damage = selectedCard.getThisCardAttackPower();
        selectedCard.setAttacked(true);
        game.setSelectedCard(null);
        game.getTheOtherPlayer().changeLP(-damage);
        if (game == null || game.getTheOtherPlayer().getLP() <= 0) return;
    }

    public boolean isDirectAttackValid() {
        if (checkAttackErrorsInCommon()) return false;
        if (!game.getTheOtherPlayer().getField().getMonsterCards().isEmpty()) {
            IO.getInstance().cannotDirectAttack();
            return false;
        }
        return true;
    }

    private boolean checkAttackErrorsInCommon() {
        if (game.getSelectedCard() == null) {
            IO.getInstance().cardNotSelected();
            return true;
        }
        if (!(game.getSelectedCard() instanceof MonsterCard) ||
                !game.getCurrentPlayer().getField().getMonsterCards().contains(game.getSelectedCard())) {
            IO.getInstance().cannotAttack();
            return true;
        }
        if (game.getCurrentPhase() != Phases.BATTLE_PHASE) {
            IO.getInstance().wrongPhase();
            return true;
        }
        MonsterCard selectedCard = (MonsterCard) game.getSelectedCard();
        if (selectedCard.isAttacked()) {
            IO.getInstance().alreadyAttacked();
            return true;
        }
        return false;
    }

    public void activateSpell() {
        if (!(getGame().getCurrentPlayer() instanceof AI) && !isActivatingSpellValid()) return;
        if (magicJamamer((SpellAndTrapCard) game.getSelectedCard())) return;
        SpellAndTrapCard selectedCard = (SpellAndTrapCard) game.getSelectedCard();
        selfAbsorption();
        if (selectedCard.getProperty().equals("Field")) {
            if (game.getCurrentPlayer().getField().getFieldZone() != null)
                game.getCurrentPlayer().getField().getGraveyard().add(game.getCurrentPlayer().getField().getFieldZone());
            game.getCurrentPlayer().getField().setFieldZone(selectedCard);
        } else if (!game.getCurrentPlayer().getField().getSpellAndTrapCards().contains(selectedCard))
            game.getCurrentPlayer().getField().getSpellAndTrapCards().add(selectedCard);
        game.getCurrentPlayer().getField().getHand().remove(selectedCard);
        selectedCard.setActive(true);
        callSpellAndTrapMethod(selectedCard);
        game.setSelectedCard(null);
    }

    public void selfAbsorption() {
        if (!getGame().isAI())
            makeChain(game.getCurrentPlayer(), game.getTheOtherPlayer());
        if (game.getCurrentPlayer().getField().getThisActivatedCard(CardEffects.SPELL_ABSORPTION) != null)
            game.getCurrentPlayer().changeLP(500);
        if (game.getTheOtherPlayer().getField().getThisActivatedCard(CardEffects.SPELL_ABSORPTION) != null)
            game.getTheOtherPlayer().changeLP(500);
    }

    public boolean isActivatingSpellValid() {
        if (game.getSelectedCard() == null) {
            IO.getInstance().cardNotSelected();
            return false;
        }
        if (!(game.getSelectedCard() instanceof SpellAndTrapCard)) {
            IO.getInstance().onlySpells();
            return false;
        }
        SpellAndTrapCard selectedCard = (SpellAndTrapCard) game.getSelectedCard();
        if (!selectedCard.isSpell()) {
            IO.getInstance().onlySpells();
            return false;
        }
        if (game.getCurrentPhase() != Phases.FIRST_MAIN_PHASE &&
                game.getCurrentPhase() != Phases.SECOND_MAIN_PHASE) {
            IO.getInstance().wrongPhase();
            return false;
        }
        if (selectedCard.isActive()) {
            IO.getInstance().alreadyActive();
            return false;
        }
        if (game.getCurrentPlayer().getField().getHand().contains(selectedCard) &&
                !selectedCard.getProperty().equals("Field") &&
                game.getCurrentPlayer().getField().getSpellAndTrapCards().size() == 5) {
            IO.getInstance().spellZoneFull();
            return false;
        }
        return true;
    }

    private void callSpellAndTrapMethod(SpellAndTrapCard card) {
        if (card.getName().contains(CardEffects.MONSTER_REBORN))
            monsterReborn(card);
        else if (card.getName().contains(CardEffects.TERRAFORMING))
            terraforming(card);
        else if (card.getName().contains(CardEffects.POT_OF_GREED))
            potOfGreed(card);
        else if (card.getName().contains(CardEffects.RAIGEKI))
            raigeki(card);
        else if (card.getName().contains(CardEffects.HARPIE_S_FEATHER_DUSTER))
            harpiesFeatherDuster(card);
        else if (card.getName().contains(CardEffects.DARK_HOLE))
            darkHole(card);
        else if (card.getName().contains(CardEffects.MYSTICAL_SPACE_TYPHOON))
            mysticalSpaceTyphoon(card);
        else if (card.getName().contains(CardEffects.YAMI))
            yami();
        else if (card.getName().contains(CardEffects.FOREST))
            forest();
        else if (card.getName().contains(CardEffects.CLOSED_FOREST))
            closedForest();
        else if (card.getName().contains(CardEffects.UMIIRUKA))
            umiiruka();
        else if (card.getName().contains(CardEffects.ADVANCED_RITUAL_ART))
            advancedRitualArt(card);
        else if (card.getName().contains(CardEffects.TWIN_TWISTERS))
            twinTwisters(card);
        else switch (card.getName()) {
                case CardEffects.CHANGE_OF_HEART -> changeOfHeart((ChangeOfHeart) card);
                case CardEffects.MESSENGER_OF_PEACE -> messengerOfPeace();
                case CardEffects.SWORD_OF_DARK_DESTRUCTION, CardEffects.BLACK_PENDANT, CardEffects.UNITED_WE_STAND, CardEffects.MAGNUM_SHIELD -> equipMonster(card); //TODO
                case CardEffects.SWORDS_OF_REVEALING_LIGHT -> ((SwordsOfRevealingLight) card).specialMethod(game.getTheOtherPlayer());
            }
    }

    private void moveSpellOrTrapToGYFromSpellZone(SpellAndTrapCard spellAndTrapCard) {
        spellAndTrapCard.getOwner().getField().getSpellAndTrapCards().remove(spellAndTrapCard);
        spellAndTrapCard.getOwner().getField().getGraveyard().add(spellAndTrapCard);
        spellAndTrapCard.reset();
    }

    public void moveSpellOrTrapToGYFromFieldZone(SpellAndTrapCard spellAndTrapCard) {
        spellAndTrapCard.reset();
        game.getCurrentPlayer().getField().setFieldZone(null);
        game.getCurrentPlayer().getField().getGraveyard().add(spellAndTrapCard);
    }

    public void callOfTheHaunted(SpellAndTrapCard callOfTheHauntedCard, Field field, boolean isCurrentPlayer) {
        if (DuelView.getInstance().wantsToActivate(CardEffects.CALL_OF_THE_HAUNTED)) {
            moveSpellOrTrapToGYFromSpellZone(callOfTheHauntedCard);
            Card card;
            if (isCurrentPlayer) card = DuelView.getInstance().getCardFromMyGY();
            else card = DuelView.getInstance().getCardFromOpponentGY();
            if (card == null) return;
            field.getGraveyard().remove(card);
            field.getMonsterCards().add((MonsterCard) card);
        }
    }

    public boolean solemnWarning(MonsterCard monsterCard) {
        SpellAndTrapCard solemnWarning = game.getTheOtherPlayer().getField().getSpellAndTrapCard(CardEffects.SOLEMN_WARNING);
        if (solemnWarning != null && DuelView.getInstance().wantsToActivateTrap(CardEffects.SOLEMN_WARNING, solemnWarning.getOwner().getUsername())) {
            if (!getGame().isAI())
                makeChain(game.getCurrentPlayer(), game.getTheOtherPlayer());
            moveSpellOrTrapToGYFromSpellZone(solemnWarning);
            game.getTheOtherPlayer().changeLP(-2000);
            addMonsterToGYFromHand(monsterCard);
            return true;
        }
        return false;
    }

    public boolean magicJamamer(SpellAndTrapCard spellAndTrapCard) {
        SpellAndTrapCard magicJamamer = game.getTheOtherPlayer().getField().getSpellAndTrapCard(CardEffects.MAGIC_JAMAMER);
        if (magicJamamer != null && DuelView.getInstance().wantsToActivateTrap(CardEffects.MAGIC_JAMAMER, magicJamamer.getOwner().getUsername())) {
            if (!getGame().isAI())
                makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
            var toRemove = DuelView.getInstance().getCardFromHand();
            if (toRemove == null) return false;
            moveSpellOrTrapToGYFromSpellZone(magicJamamer);
            game.getCurrentPlayer().getField().getGraveyard().add(spellAndTrapCard);
            game.getCurrentPlayer().getField().getSpellAndTrapCards().remove(spellAndTrapCard);
            game.getTheOtherPlayer().getField().getHand().remove(toRemove);
            game.getTheOtherPlayer().getField().getGraveyard().add(toRemove);
            game.setSelectedCard(null);
            return true;
        }
        return false;
    }

    public void timeSeal(SpellAndTrapCard timeSealCard, Duelist opponent) {
        if (DuelView.getInstance().wantsToActivate(CardEffects.TIME_SEAL)) {
            moveSpellOrTrapToGYFromSpellZone(timeSealCard);
            opponent.setAbleToDraw(false);
        }
    }

    public void torrentialTribute() {
        SpellAndTrapCard torrentialTributeCard = game.getTheOtherPlayer().getField().getSpellAndTrapCard(CardEffects.TORRENTIAL_TRIBUTE);
        if (torrentialTributeCard != null && DuelView.getInstance().wantsToActivateTrap(CardEffects.TORRENTIAL_TRIBUTE,
                torrentialTributeCard.getOwner().getUsername())) {
            if (!getGame().isAI())
                makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
            moveSpellOrTrapToGYFromSpellZone(torrentialTributeCard);
            ArrayList<MonsterCard> allMonsters = getAllMonsterCards();
            int size = allMonsters.size();
            for (var i = 0; i < size; i++)
                addMonsterToGYFromMonsterZone(allMonsters.get(0));
        }
    }

    public void mindCrush(SpellAndTrapCard spellAndTrapCard, Duelist opponent) {
        if (DuelView.getInstance().wantsToActivateTrap(CardEffects.MIND_CRUSH, spellAndTrapCard.getOwner().getUsername())) {
            String cardName = DuelView.getInstance().getCardName();
            if (!opponent.getCardInHand(cardName)) randomlyRemoveFromHand(spellAndTrapCard.getOwner());
            else opponent.getField().getHand().removeAll(opponent.getField().getHand().stream()
                    .filter(c -> c.getName().equals(cardName)).collect(Collectors.toList()));
            moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
        }
    }

    public void randomlyRemoveFromHand(Duelist duelist) {
        var card = duelist.getField().getHand().get(RANDOM.nextInt(duelist.getField().getHand().size()));
        duelist.getField().getHand().remove(card);
        duelist.getField().getGraveyard().add(card);
    }

    public void equipMonster(SpellAndTrapCard equipSpell) {
        MonsterCard monsterToEquip = DuelView.getInstance().getMonsterToEquip();
        if (monsterToEquip == null) {
            equipSpell.setActive(false);
            return;
        }
        switch (equipSpell.getName()) {
            case CardEffects.SWORD_OF_DARK_DESTRUCTION -> ((SwordOfDarkDestruction) equipSpell).setEquippedMonster(monsterToEquip);
            case CardEffects.BLACK_PENDANT -> ((BlackPendant) equipSpell).setEquippedMonster(monsterToEquip);
            case CardEffects.UNITED_WE_STAND -> ((UnitedWeStand) equipSpell).setEquippedMonster(monsterToEquip);
            case CardEffects.MAGNUM_SHIELD -> ((MagnumShield) equipSpell).setEquippedMonster(monsterToEquip);
        }
    }

    public void magicCylinder(MonsterCard attacker, SpellAndTrapCard magicCylinder) {
        if (!DuelView.getInstance().wantsToActivateTrap(CardEffects.MAGIC_CYLINDER, magicCylinder.getOwner().getUsername()))
            return;
        if (!getGame().isAI())
            makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        decreaseLPWithTrap(attacker.getOwner(), attacker.getClassAttackPower());
        addMonsterToGYFromMonsterZone(attacker);
        moveSpellOrTrapToGYFromSpellZone(magicCylinder);
    }

    public void mirrorForce(SpellAndTrapCard mirrorForce, Duelist opponent) {
        if (!getGame().isAI())
            makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        ArrayList<MonsterCard> monsters = new ArrayList<>(opponent.getField().getMonsterCards());
        for (MonsterCard monster : monsters)
            if (monster.getMonsterCardModeInField() == MonsterCardModeInField.ATTACK_FACE_UP)
                addMonsterToGYFromMonsterZone(monster);
        moveSpellOrTrapToGYFromSpellZone(mirrorForce);
    }

    private void decreaseLPWithTrap(Duelist duelist, int amount) {
        if (duelist.getField().getThisActivatedCard(CardEffects.RING_OF_DEFENSE) == null)
            duelist.changeLP(-amount);
    }

    private void advancedRitualArt(SpellAndTrapCard spellAndTrapCard) {
        if (ritualSummon())
            moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    public void twinTwisters(SpellAndTrapCard spellAndTrapCard) {
        if (!getGame().isAI())
            makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
        Card removeFromHand;
        if (spellAndTrapCard.getOwner().equals(game.getCurrentPlayer()))
            removeFromHand = DuelView.getInstance().getCardFromHand();
        else
            removeFromHand = DuelView.getInstance().getCardFromOpponentHand();
        if (removeFromHand == null) return;
        spellAndTrapCard.getOwner().getField().getHand().remove(removeFromHand);
        spellAndTrapCard.getOwner().getField().getGraveyard().add(removeFromHand);
        int numOfSpellCardsToDestroy = DuelView.getInstance().numOfSpellCardsToDestroy();
        for (var i = 0; i < numOfSpellCardsToDestroy; i++) {
            boolean isMine = DuelView.getInstance().isMine();
            if (game.getTheOtherPlayer().equals(spellAndTrapCard.getOwner())) isMine = !isMine;
            SpellAndTrapCard toDestroy = isMine ? DuelView.getInstance().getFromMyField() : DuelView.getInstance().getFromOpponentField();
            if (toDestroy == null) continue;
            moveSpellOrTrapToGYFromSpellZone(toDestroy);
        }
    }

    public void umiiruka() {
        SpellAndTrapCard umiiruka = game.getCurrentPlayer().getField().getFieldZone();
        if (umiiruka == null) umiiruka = game.getTheOtherPlayer().getField().getFieldZone();
        String[] toEffectPositively = umiiruka.getFieldPositiveEffects();
        if (umiiruka.isOriginal()) {
            toEffectPositively = new String[1];
            toEffectPositively[0] = "Aqua";
        }
        if (!getGame().isAI())
            makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        SpellAndTrapCard finalUmiiruka = umiiruka;
        getAllMonsterCards().stream().filter(m -> Arrays.stream(finalUmiiruka.getFieldPositiveEffects())
                .anyMatch(t -> m.getMonsterType().equals(t))).forEach(a -> {
            a.setThisCardAttackPower(a.getThisCardAttackPower() + 500);
            a.setThisCardDefensePower(a.getThisCardDefensePower() - 400);
        });
    }

    public void closedForest() {
        if (!getGame().isAI())
            makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        SpellAndTrapCard forest = game.getCurrentPlayer().getField().getFieldZone();
        if (forest == null) forest = game.getTheOtherPlayer().getField().getFieldZone();
        String[] toEffectPositively = forest.getFieldPositiveEffects();
        if (forest.isOriginal()) {
            toEffectPositively = new String[2];
            toEffectPositively[0] = "Beast";
            toEffectPositively[1] = "Beast-Warrior";
        }
        int amount = game.getCurrentPlayer().getField().getGraveyard().size() * 100;
        SpellAndTrapCard finalForest = forest;
        getAllMonsterCards().stream().filter(m -> Arrays.stream(finalForest.getFieldPositiveEffects())
                .anyMatch(t -> m.getMonsterType().equals(t))).forEach(m -> m.setThisCardAttackPower(m.getThisCardAttackPower() + amount));
    }

    public void forest() {
        SpellAndTrapCard forest = game.getCurrentPlayer().getField().getFieldZone();
        if (forest == null) forest = game.getTheOtherPlayer().getField().getFieldZone();
        String[] toEffectPositively = forest.getFieldPositiveEffects();
        if (forest.isOriginal()) {
            toEffectPositively = new String[3];
            toEffectPositively[0] = "Insect";
            toEffectPositively[1] = "Beast";
            toEffectPositively[2] = "Beast-Warrior";
        }
        if (!getGame().isAI())
            makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        SpellAndTrapCard finalForest = forest;
        getAllMonsterCards().stream().filter(m -> Arrays.stream(finalForest.getFieldPositiveEffects())
                .anyMatch(t -> m.getMonsterType().equals(t))).forEach(m -> {
            m.setThisCardDefensePower(m.getThisCardDefensePower() + 200);
            m.setThisCardAttackPower(m.getThisCardAttackPower() + 200);
        });
    }

    public void yami() {
        SpellAndTrapCard yami = game.getCurrentPlayer().getField().getFieldZone();
        if (yami == null) yami = game.getTheOtherPlayer().getField().getFieldZone();
        String[] toEffectPositively = yami.getFieldPositiveEffects();
        String[] toEffectNegatively = yami.getFieldNegativeEffects();
        if (yami.isOriginal()) {
            toEffectPositively = new String[2];
            toEffectNegatively = new String[1];
            toEffectPositively[0] = "Fiend";
            toEffectPositively[1] = "Spellcaster";
            toEffectNegatively[0] = "Fairy";
        }
        if (!getGame().isAI())
            makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        SpellAndTrapCard finalYami = yami;
        getAllMonsterCards().stream().filter(m -> Arrays.stream(finalYami.getFieldPositiveEffects())
                .anyMatch(t -> m.getMonsterType().equals(t))).forEach(m -> {
            m.setThisCardDefensePower(m.getThisCardDefensePower() + 200);
            m.setThisCardAttackPower(m.getThisCardAttackPower() + 200);
        });
        getAllMonsterCards().stream().filter(m -> Arrays.stream(finalYami.getFieldNegativeEffects())
                .anyMatch(t -> m.getMonsterType().equals(t))).forEach(m -> {
            m.setThisCardDefensePower(m.getThisCardDefensePower() - 200);
            m.setThisCardAttackPower(m.getThisCardAttackPower() - 200);
        });
    }

    public ArrayList<MonsterCard> getAllMonsterCards() {
        ArrayList<MonsterCard> cards = new ArrayList<>();
        cards.addAll(game.getCurrentPlayer().getField().getMonsterCards());
        cards.addAll(game.getTheOtherPlayer().getField().getMonsterCards());
        return cards;
    }

    public void mysticalSpaceTyphoon(SpellAndTrapCard spellAndTrapCard) {
        SpellAndTrapCard toDestroy;
        boolean isMine = DuelView.getInstance().isMine();
        if (!spellAndTrapCard.getOwner().equals(getGame().getCurrentPlayer())) isMine = !isMine;
        if (isMine)
            toDestroy = DuelView.getInstance().getFromMyField();
        else toDestroy = DuelView.getInstance().getFromOpponentField();
        if (toDestroy != null) {
            moveSpellOrTrapToGYFromSpellZone(toDestroy);
        }
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    public void darkHole(SpellAndTrapCard spellAndTrapCard) {
        if (!getGame().isAI())
            makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        int myMonstersSize = game.getCurrentPlayer().getField().getMonsterCards().size();
        int opponentSize = game.getTheOtherPlayer().getField().getMonsterCards().size();
        for (var i = 0; i < myMonstersSize; i++)
            addMonsterToGYFromMonsterZone(game.getCurrentPlayer().getField().getMonsterCards().get(0));
        for (var i = 0; i < opponentSize; i++)
            addMonsterToGYFromMonsterZone(game.getTheOtherPlayer().getField().getMonsterCards().get(0));
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    public void harpiesFeatherDuster(SpellAndTrapCard spellAndTrapCard) {
        if (!getGame().isAI())
            makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        int size = game.getTheOtherPlayer().getField().getSpellAndTrapCards().size();
        for (var i = 0; i < size; i++)
            moveSpellOrTrapToGYFromSpellZone(game.getTheOtherPlayer().getField().getSpellAndTrapCards().get(0));
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    public void changeOfHeart(ChangeOfHeart changeOfHeart) {
        MonsterCard toHijack = DuelView.getInstance().getHijackedCard();
        if (toHijack != null && changeOfHeart.getOwner().getField().getMonsterCards().size() != 5) {
            if (!getGame().isAI())
                makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
            changeOfHeart.setHijackedCard(toHijack);
        }
    }

    public void raigeki(SpellAndTrapCard spellAndTrapCard) {
        if (!getGame().isAI())
            makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        int size = game.getTheOtherPlayer().getField().getMonsterCards().size();
        for (var i = 0; i < size; i++)
            addMonsterToGYFromMonsterZone(game.getTheOtherPlayer().getField().getMonsterCards().get(0));
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    public void potOfGreed(SpellAndTrapCard spellAndTrapCard) {
        if (!getGame().isAI())
            makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        if (game.getCurrentPlayer().getField().getHand().size() <= 4 && game.getCurrentPlayer().getField().getDeckZone().size() >= 2) {
            game.getCurrentPlayer().getField().getHand().add(game.getCurrentPlayer().getField().getDeckZone().get(0));
            game.getCurrentPlayer().getField().getDeckZone().remove(0);
            game.getCurrentPlayer().getField().getHand().add(game.getCurrentPlayer().getField().getDeckZone().get(0));
            game.getCurrentPlayer().getField().getDeckZone().remove(0);
        }
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    public void terraforming(SpellAndTrapCard spellAndTrapCard) {
        SpellAndTrapCard fieldSpell = DuelView.getInstance().getFieldSpellFromDeck();
        if (fieldSpell != null) {
            if (!getGame().isAI())
                makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
            if (game.getCurrentPlayer().getField().getHand().size() != 6)
                game.getCurrentPlayer().getField().getHand().add(fieldSpell);
        }
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    public void monsterReborn(SpellAndTrapCard spellAndTrapCard) {
        if (!getGame().isAI())
            makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        MonsterCard monsterCard;
        ArrayList<Card> removeFrom;
        if (DuelView.getInstance().isMine()) {
            monsterCard = DuelView.getInstance().getFromMyGY();
            removeFrom = game.getCurrentPlayer().getField().getGraveyard();
        } else {
            monsterCard = DuelView.getInstance().getFromOpponentGY();
            removeFrom = game.getTheOtherPlayer().getField().getGraveyard();
        }
        if (monsterCard != null)
            specialSummon(monsterCard, MonsterCardModeInField.ATTACK_FACE_UP, removeFrom);
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    public void setSpellAndTrap() {
        if (!isSettingSpellAndTrapValid()) return;
        game.getCurrentPlayer().getField().getHand().remove(game.getSelectedCard());
        game.getCurrentPlayer().getField().getSpellAndTrapCards().add((SpellAndTrapCard) game.getSelectedCard());
        game.getSelectedCard().setHasBeenSetOrSummoned(true);
        game.setSelectedCard(null);
    }

    private boolean isSettingSpellAndTrapValid() {
        if (game.getSelectedCard() == null) {
            IO.getInstance().cardNotSelected();
            return false;
        }
        if (!game.getCurrentPlayer().getField().getHand().contains(game.getSelectedCard())) {
            IO.getInstance().cannotSet();
            return false;
        }
        if (game.getCurrentPhase() != Phases.FIRST_MAIN_PHASE && game.getCurrentPhase() != Phases.SECOND_MAIN_PHASE) {
            IO.getInstance().wrongPhase();
            return false;
        }
        if (game.getCurrentPlayer().getField().getSpellAndTrapCards().size() == 5) {
            IO.getInstance().spellZoneFull();
            return false;
        }
        return true;
    }

    public void set() {
        if (game.getSelectedCard() instanceof MonsterCard) setMonster();
        else setSpellAndTrap();
    }

    public boolean ritualSummon() {
        if (!errorForRitualSummon()) return false;
        MonsterCard ritualMonster = DuelView.getInstance().getRitualCard();
        ArrayList<MonsterCard> toTribute = DuelView.getInstance().getTributeMonsterCards();
        if (Objects.isNull(toTribute)) return false;
        while (!isSumOfTributesValid(toTribute, ritualMonster)) {
            IO.getInstance().invalidTributeSum();
            toTribute = DuelView.getInstance().getTributeMonsterCards();
        }
        chooseMonsterMode(ritualMonster);
        game.getCurrentPlayer().getField().getMonsterCards().add(ritualMonster);
        for (MonsterCard monsterCard : toTribute) {
            addMonsterToGYFromMonsterZone(monsterCard);
        }
        moveSpellOrTrapToGYFromSpellZone((SpellAndTrapCard) game.getSelectedCard());
        game.setSelectedCard(null);
        return true;
    }

    private void chooseMonsterMode(MonsterCard monsterCard) {
        String mode = DuelView.getInstance().monsterMode().toLowerCase();
        if (mode.startsWith("a"))
            monsterCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        else monsterCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
    }

    private boolean isSumOfTributesValid(ArrayList<MonsterCard> toTribute, MonsterCard ritualMonster) {
        return toTribute.stream().mapToInt(MonsterCard::getLevel).sum() == ritualMonster.getLevel();
    }

    private boolean errorForRitualSummon() {
        if (game.getCurrentPlayer().getField().ritualMonsterCards().isEmpty()) {
            IO.getInstance().cannotRitualSummon();
            return false;
        }
        if (!checkSum()) {
            IO.getInstance().cannotRitualSummon();
            return false;
        }
        return true;
    }

    private boolean checkSum() {
        return game.getCurrentPlayer().getField().ritualMonsterCards().stream().anyMatch(m -> game.getCurrentPlayer().getField()
                .isTributesLevelSumValid(m.getLevel(), game.getCurrentPlayer().getField().getMonsterCards().size()));
    }

    private void specialSummon(MonsterCard monsterCard, MonsterCardModeInField monsterCardModeInField, ArrayList<Card> removeFrom) {
        removeFrom.remove(monsterCard);
        if (solemnWarning(monsterCard)) return;
        monsterCard.getOwner().getField().getMonsterCards().add(monsterCard);
        monsterCard.setMonsterCardModeInField(monsterCardModeInField);
    }

    public String cheatShowRivalHand() {
        var rivalHand = new StringBuilder();
        game.getTheOtherPlayer().getField().getHand().forEach(c -> rivalHand.append(c.getName()).append("\n"));
        if (!rivalHand.isEmpty()) rivalHand.setLength(rivalHand.length() - 1);
        return rivalHand.toString();
    }

    public void cheatIncreaseLP(int amount) {
        game.getCurrentPlayer().changeLP(amount);
        IO.getInstance().cheatIncreaseScore();
    }

    public void cheatSetWinner() {
        game.finishGame(game.getTheOtherPlayer());
    }

    public String cheatSeeMyDeck() {
        var sixCards = new StringBuilder();
        for (var i = 0; i < 5; i++)
            sixCards.append(i).append(": ").append(game.getCurrentPlayer().getField().getDeckZone().get(i).getName()).append("\n");
        return sixCards.substring(0, sixCards.length() - 1);
    }

    public void cheatDecreaseLP(int amount) {
        game.getTheOtherPlayer().changeLP(-amount);
        IO.getInstance().cheatIncreaseScore();
    }

    public void surrender() {
        game.finishGame(game.getCurrentPlayer());
    }

    private void moveToGraveyardAfterAttack(MonsterCard toBeRemoved, MonsterCard remover) {
        if (toBeRemoved.getName().contains(CardEffects.MARSHMALLON)) return;
        addMonsterToGYFromMonsterZone(toBeRemoved);
        if (toBeRemoved.getName().contains(CardEffects.EXPLODER_DRAGON) || toBeRemoved.getName().startsWith(CardEffects.YOMI_SHIP)) {
            addMonsterToGYFromMonsterZone(remover);
        }
    }

    private void addMonsterToGYFromMonsterZone(MonsterCard toBeRemoved) {
        toBeRemoved.reset();
        Field field = toBeRemoved.getOwner().getField();
        field.getGraveyard().add(toBeRemoved);
        field.getMonsterCards().remove(toBeRemoved);
        handleSupplySquad(toBeRemoved, field);
    }

    private void addMonsterToGYFromHand(MonsterCard toBeRemoved) {
        toBeRemoved.reset();
        Field field = toBeRemoved.getOwner().getField();
        field.getGraveyard().add(toBeRemoved);
        field.getHand().remove(toBeRemoved);
    }

    public void handleSupplySquad(MonsterCard toBeRemoved, Field field) {
        if (!field.getDeckZone().isEmpty() &&
                (field.getHand().size() != 6)) {
            SpellAndTrapCard supplySquad = field.getThisActivatedCard(CardEffects.SUPPLY_SQUAD);
            if (supplySquad != null && !supplySquad.isHasBeenUsedInThisTurn()) {
                if (!getGame().isAI())
                    makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
                supplySquad.setHasBeenUsedInThisTurn(true);
                field.getHand().add(toBeRemoved.getOwner().getField().getDeckZone().get(0));
                field.getDeckZone().remove(0);
            }
        }
    }

    public void forManEaterBug() {
        MonsterCard monsterCard = DuelView.getInstance().getOpponentMonster();
        if (monsterCard != null) addMonsterToGYFromMonsterZone(monsterCard);
    }

    public void forScanner(Scanner scanner) {
        if (DuelView.getInstance().wantsToActivate(CardEffects.SCANNER)) {
            MonsterCard toReplace = DuelView.getInstance().getFromOpponentGY();
            if (toReplace == null) return;
            scanner.setCardReplaced(toReplace);
            ArrayList<MonsterCard> monsterCards = game.getCurrentPlayer().getField().getMonsterCards();
            monsterCards.remove(scanner);
            monsterCards.add(toReplace);
            toReset.add(scanner);
        }
    }

    public void heraldOfCreation() {
        MonsterCard monsterCard = DuelView.getInstance().getFromMyGY();
        if (monsterCard.getLevel() < 7) return;
        MonsterCard monsterCardToRemove = DuelView.getInstance().getMonsterCardFromHand();
        addMonsterToGYFromHand(monsterCardToRemove);
        game.getCurrentPlayer().getField().getHand().add(monsterCard);
    }


    public void terraTigerMethod() {
        if (!DuelView.getInstance().wantsToActivate(CardEffects.TERRATIGER_THE_EMPOWERED_WARRIOR)) return;
        if (!errorForTerraTiger()) {
            IO.getInstance().invalidCard();
            return;
        }
        MonsterCard monsterCard = DuelView.getInstance().getMonsterCardFromHand();
        specialSummon(monsterCard, MonsterCardModeInField.DEFENSE_FACE_DOWN, game.getCurrentPlayer().getField().getHand());
        game.setSummonedInThisTurn(true);
    }

    private boolean errorForTerraTiger() {
        if (game.getCurrentPlayer().getField().getMonsterCards().size() == 5) {
            IO.getInstance().monsterZoneFull();
            return false;
        }
        if (game.getCurrentPlayer().getField().ordinaryLowLevelCards().isEmpty()) {
            IO.getInstance().invalidCard();
            return false;
        }
        return true;
    }

    public void theTricky() {
        MonsterCard monsterCard = DuelView.getInstance().getMonsterCardFromHand();
        game.getCurrentPlayer().getField().getHand().remove(monsterCard);
        game.getCurrentPlayer().getField().getGraveyard().add(monsterCard);
        specialSummon((MonsterCard) game.getSelectedCard(), MonsterCardModeInField.ATTACK_FACE_UP, game.getCurrentPlayer().getField().getHand());
        game.setSelectedCard(null);
    }

    public void gateGuardian() {
        tributeThreeCards();
        MonsterCard gateGuardian = (MonsterCard) game.getSelectedCard();
        specialSummon(gateGuardian, MonsterCardModeInField.ATTACK_FACE_UP, game.getCurrentPlayer().getField().getHand());
        game.setSelectedCard(null);
    }

    private void tributeThreeCards() {
        int firstTribute = DuelView.getInstance().getTribute();
        int secondTribute = DuelView.getInstance().getTribute();
        int thirdTribute = DuelView.getInstance().getTribute();
        addMonsterToGYFromMonsterZone(game.getCurrentPlayer().getField().getMonsterCards().get(firstTribute));
        addMonsterToGYFromMonsterZone(game.getCurrentPlayer().getField().getMonsterCards().get(secondTribute));
        addMonsterToGYFromMonsterZone(game.getCurrentPlayer().getField().getMonsterCards().get(thirdTribute));
    }

    public void barbaros(int howToSummon) {
        MonsterCard barbaros = (MonsterCard) game.getSelectedCard();
        if (howToSummon == 2) {
            barbaros.setThisCardAttackPower(1900);
            barbaros.setClassAttackPower(1900);
        } else {
            ArrayList<MonsterCard> opponentMonsterCards = game.getTheOtherPlayer().getField().getMonsterCards();
            ArrayList<SpellAndTrapCard> opponentSpellCards = game.getTheOtherPlayer().getField().getSpellAndTrapCards();
            tributeThreeCards();
            if (game.getTheOtherPlayer().getField().getFieldZone() != null)
                moveSpellOrTrapToGYFromFieldZone(game.getTheOtherPlayer().getField().getFieldZone());
            int spellSize = opponentSpellCards.size();
            int monsterSize = opponentMonsterCards.size();
            for (var i = 0; i < spellSize; i++)
                moveSpellOrTrapToGYFromSpellZone(opponentSpellCards.get(0));
            for (var i = 0; i < monsterSize; i++)
                addMonsterToGYFromMonsterZone(opponentMonsterCards.get(0));
        }
        game.getCurrentPlayer().getField().getHand().remove(barbaros);
        game.getCurrentPlayer().getField().getMonsterCards().add(barbaros);
        barbaros.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        game.getSelectedCard().setHasBeenSetOrSummoned(true);
        game.setSelectedCard(null);
        game.setSummonedInThisTurn(true);
    }

    public void texChanger(MonsterCard attacked) {
        if (!attacked.isHasBeenUsedInThisTurn()) {
            ArrayList<Card> toRemoveFrom;
            MonsterCard monsterCard;
            do {
                toRemoveFrom = new ArrayList<>();
                int whereFrom = DuelView.getInstance().whereToSummonFrom();
                monsterCard = null;
                switch (whereFrom) {
                    case 1 -> {
                        monsterCard = DuelView.getInstance().getMonsterCardFromHand(true);
                        toRemoveFrom = game.getCurrentPlayer().getField().getHand();
                    }
                    case 2 -> {
                        monsterCard = game.getCurrentPlayer().getField().getDeckZone().stream().filter(MonsterCard.class::isInstance)
                                .map(MonsterCard.class::cast).filter(m -> m.getCardType().equals("Normal") && m.getMonsterType().equals("Cyberse"))
                                .findFirst().orElse(null);
                        toRemoveFrom = game.getCurrentPlayer().getField().getDeckZone();
                    }
                    case 3 -> {
                        monsterCard = DuelView.getInstance().getFromMyGY(true);
                        toRemoveFrom = game.getCurrentPlayer().getField().getGraveyard();
                    }
                }
                if (monsterCard == null) return;
            } while (!(monsterCard.getCardType().equals("Normal") && monsterCard.getMonsterType().equals("Cyberse")));
            specialSummon(monsterCard, MonsterCardModeInField.ATTACK_FACE_UP, toRemoveFrom);
            attacked.setHasBeenUsedInThisTurn(true);
        }
    }

    public void makeChain(Duelist currentPlayer, Duelist theOtherPlayer) {
        if (isChainActive) return;
        if (!canMakeChain(theOtherPlayer) && !canMakeChain(currentPlayer)) return;
        Duelist temp = theOtherPlayer;
        if (canMakeChain(theOtherPlayer) && canMakeChain(currentPlayer)) {
            String command = DuelView.getInstance().makeChain();
            if (command.toLowerCase().matches("n|no")) {
                chainForOnePersonWithoutCondition(currentPlayer);
                activateCards();
                return;
            }
            while (!actionForChain(temp)) {
                if (temp.equals(theOtherPlayer)) temp = currentPlayer;
                else temp = theOtherPlayer;
            }
        }
        chainForOnePerson(currentPlayer, theOtherPlayer);
        chainForOnePerson(theOtherPlayer, currentPlayer);
        activateCards();
    }

    private void chainForOnePerson(Duelist currentPlayer, Duelist theOtherPlayer) {
        if (!canMakeChain(theOtherPlayer) && canMakeChain(currentPlayer)) {
            chainForOnePersonWithoutCondition(currentPlayer);
        }
    }

    private void chainForOnePersonWithoutCondition(Duelist currentPlayer) {
        String command = DuelView.getInstance().makeChain();
        if (command.toLowerCase().matches("no")) return;
        while (true) {
            if (actionForChain(currentPlayer)) return;
        }
    }

    public boolean actionForChain(Duelist temp) {
        String inputMessage = DuelView.getInstance().addForChain();
        if (inputMessage.equalsIgnoreCase("cancel")) return true;
        SpellAndTrapCard card = temp.getField().getThisActivatedCard(inputMessage);
        forChain.add(card);
        return false;
    }

    public boolean canMakeChain(Duelist player) {
        ArrayList<String> cardNames = new ArrayList<>(
                List.of(CardEffects.MIND_CRUSH, CardEffects.TWIN_TWISTERS, CardEffects.MYSTICAL_SPACE_TYPHOON, CardEffects.RING_OF_DEFENSE, CardEffects.TIME_SEAL, CardEffects.CALL_OF_THE_HAUNTED));
        return cardNames.stream().anyMatch(c -> player.getField().getSetSpellAndTrapCard(c) != null);
    }

    public void activateCards() {
        isChainActive = true;
        forChain.forEach(c -> c.setActive(true));
        Collections.reverse(forChain);
        forChain.forEach(this::chainMethods);
        forChain = new ArrayList<>();
        isChainActive = false;
    }

    private void chainMethods(SpellAndTrapCard card) {
        if (card == null) return;
        Account opponent = (Account) game.getCurrentPlayer();
        if (card.getOwner().equals(game.getCurrentPlayer()))
            opponent = (Account) game.getTheOtherPlayer();
        switch (card.getName()) {
            case CardEffects.MIND_CRUSH -> mindCrush(card, opponent);
            case CardEffects.TWIN_TWISTERS -> twinTwisters(card);
            case CardEffects.MYSTICAL_SPACE_TYPHOON -> mysticalSpaceTyphoon(card);
            case CardEffects.TIME_SEAL -> timeSeal(card, opponent);
            case CardEffects.CALL_OF_THE_HAUNTED -> callOfTheHaunted(card, card.getOwner().getField(), card.getOwner().equals(game.getCurrentPlayer()));
        }
    }

    public void messengerOfPeace() {
        ArrayList<MonsterCard> cards = new ArrayList<>();
        cards.addAll(game.getCurrentPlayer().getField().getMonsterCards());
        cards.addAll(game.getTheOtherPlayer().getField().getMonsterCards());
        for (MonsterCard monsterCard : cards)
            if (monsterCard.getClassAttackPower() >= 1500 && monsterCard.isAbleToAttack()) {
                monsterCard.setAbleToAttack(false);
                deactivatedCards.add(monsterCard);
            }
    }


    public void wonGame(boolean allMatches, boolean isAI, Account winner, boolean isFinished) {
        if (isAI)
            IO.getInstance().setText("a spacecraft’s structure is its underlying body and it's won once again");
        else {
            if (allMatches)
                IO.getInstance().setText(winner.getUsername() + " won the whole match with score: " + winner.getScore());
            else
                IO.getInstance().setText(winner.getUsername() + " won the game and the score is: " + winner.getScore());
        }
        if (isFinished) {
            DuelView.finishGame(winner.getUsername());
            game = null;
            DuelView.secondStage.close();
        }
    }
}