package yugioh.controller;

import lombok.Getter;
import lombok.Setter;
import yugioh.model.*;
import yugioh.model.cards.Card;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;
import yugioh.model.cards.specialcards.*;
import yugioh.model.cards.specialcards.Scanner;
import yugioh.view.DuelView;
import yugioh.view.IO;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class DuelController {
    private static DuelController singleInstance = null;
    private Game game;
    private ArrayList<SpellAndTrapCard> forChain = new ArrayList<>();
    private boolean isChainActive = false;
    private SecureRandom random = new SecureRandom();
    private ArrayList<Scanner> toReset = new ArrayList<>();

    public static DuelController getInstance() {
        if (singleInstance == null)
            singleInstance = new DuelController();
        return singleInstance;
    }

    public void rockPaperScissor(String thisPlayer, String theOtherPlayer) {
        if (thisPlayer.equals(theOtherPlayer)) return;
        if (thisPlayer.matches("r(?:ock)?") && theOtherPlayer.matches("s(?:cissors)?"))
            game.getCurrentPlayer().increaseCountForRPS();
        else if (thisPlayer.matches("r(?:ock)?") && theOtherPlayer.matches("p(?:aper)?"))
            game.getTheOtherPlayer().increaseCountForRPS();
        else if (thisPlayer.matches("s(?:cissors)?") && theOtherPlayer.matches("p(?:aper)?"))
            game.getCurrentPlayer().increaseCountForRPS();
        else if (thisPlayer.matches("s(?:cissors)?") && theOtherPlayer.matches("r(?:ock)?"))
            game.getTheOtherPlayer().increaseCountForRPS();
        else if (thisPlayer.matches("p(?:aper)?") && theOtherPlayer.matches("r(?:ock)?"))
            game.getCurrentPlayer().increaseCountForRPS();
        else if (thisPlayer.matches("p(?:aper)?") && theOtherPlayer.matches("s(?:cissors)?"))
            game.getTheOtherPlayer().increaseCountForRPS();
        if ((game.getCurrentPlayer().getCountForRPS() == 2 && game.getTheOtherPlayer().getCountForRPS() == 0) ||
                game.getCurrentPlayer().getCountForRPS() == 3) {
            DuelView.getInstance().chooseStarter(game.getCurrentPlayer().getUsername());
            DuelView.getInstance().setRPSDone(true);
        } else if ((game.getCurrentPlayer().getCountForRPS() == 0 && game.getTheOtherPlayer().getCountForRPS() == 2) ||
                game.getTheOtherPlayer().getCountForRPS() == 3) {
            DuelView.getInstance().chooseStarter(game.getTheOtherPlayer().getUsername());
            DuelView.getInstance().setRPSDone(true);
        }
    }

    public void chooseStarter(String username) {
        if (!game.getCurrentPlayer().getUsername().equals(username)) game.changeTurn();
        game.getCurrentPlayer().setAbleToDraw(false);
        game.getCurrentPlayer().setAbleToAttack(false);
        drawPhase();
    }

    public void drawPhase() {
        IO.getInstance().printPhase("draw phase");
        if (game.getTheOtherPlayer().getField().getSetSpellAndTrapCard("Time Seal") != null)
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
        showGameBoard(game.getTheOtherPlayer(), game.getCurrentPlayer());
    }

    private void standbyPhase() {
        IO.getInstance().printPhase("standby phase");
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
            if (m.getName().equals("Herald of Creation") && !m.isHasBeenUsedInThisTurn() &&
                    !(m.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_DOWN))) {
                heraldOfCreation();
                m.setHasBeenUsedInThisTurn(true);
            }
            if (m.getName().equals("Command Knight")) {
                ((CommandKnight) m).specialMethod();
                m.setHasBeenUsedInThisTurn(true);
            }
        });
        opponentMonsters.stream().filter(m -> m.getName().equals("Command Knight")).forEach(m -> {
            ((CommandKnight) m).specialMethod();
            m.setHasBeenUsedInThisTurn(true);
        });
    }

    private void handleCallOfTheHaunted(Field field, Field opponentField) {
        SpellAndTrapCard callOfTheHauntedCard = field.getSpellAndTrapCard("Call of The Haunted");
        if (callOfTheHauntedCard != null) callOfTheHaunted(callOfTheHauntedCard, field, true);
        callOfTheHauntedCard = opponentField.getSpellAndTrapCard("Call of The Haunted");
        if (callOfTheHauntedCard != null) callOfTheHaunted(callOfTheHauntedCard, opponentField, false);
    }

    private void handleMindCrush(Field field, Field opponentField) {
        SpellAndTrapCard mindCrushCard = field.getSpellAndTrapCard("Mind Crush");
        if (mindCrushCard != null) mindCrush(mindCrushCard, game.getTheOtherPlayer());
        mindCrushCard = opponentField.getSpellAndTrapCard("Mind Crush");
        if (mindCrushCard != null) mindCrush(mindCrushCard, game.getCurrentPlayer());
    }

    private void handleTimeSeal(Field field, Field opponentField) {
        SpellAndTrapCard timeSealCard = field.getSpellAndTrapCard("Time Seal");
        if (timeSealCard != null) timeSeal(timeSealCard, game.getTheOtherPlayer());
        timeSealCard = opponentField.getSpellAndTrapCard("Time Seal");
        if (timeSealCard != null) timeSeal(timeSealCard, game.getCurrentPlayer());
    }

    private void handleEquip(Field field) {
        var swordOfDarkDestruction = (SwordOfDarkDestruction) field.getThisActivatedCard("Sword of dark destruction");
        if (swordOfDarkDestruction != null) swordOfDarkDestruction.equipMonster();
        var blackPendant = (BlackPendant) field.getThisActivatedCard("Black Pendant");
        if (blackPendant != null) blackPendant.equipMonster();
        var unitedWeStand = (UnitedWeStand) field.getThisActivatedCard("United We Stand");
        if (unitedWeStand != null) unitedWeStand.equipMonster();
        var magnumShield = (MagnumShield) field.getThisActivatedCard("Magnum Shield");
        if (magnumShield != null) magnumShield.equipMonster();
    }

    private void handleField(Field field, Field opponentField) {
        if ((field.getFieldZone() != null && field.getFieldZone().getName().equals("Umiiruka")) ||
                (opponentField.getFieldZone() != null && opponentField.getFieldZone().getName().equals("Umiiruka")))
            umiiruka();
        if ((field.getFieldZone() != null && field.getFieldZone().getName().equals("Forest")) ||
                (opponentField.getFieldZone() != null && opponentField.getFieldZone().getName().equals("Forest")))
            forest();
        if ((field.getFieldZone() != null && field.getFieldZone().getName().equals("Closed Forest")) ||
                (opponentField.getFieldZone() != null && opponentField.getFieldZone().getName().equals("Closed Forest")))
            closedForest();
        if ((field.getFieldZone() != null && field.getFieldZone().getName().equals("Yami")) ||
                (opponentField.getFieldZone() != null && opponentField.getFieldZone().getName().equals("Yami"))) yami();
    }

    public void handleMessengerOfPeace() {
        SpellAndTrapCard myMessengerOfPeace = game.getCurrentPlayer().getField().getThisActivatedCard("Messenger of peace");
        if (myMessengerOfPeace != null) {
            if (DuelView.getInstance().killMessengerOfPeace())
                moveSpellOrTrapToGYFromSpellZone(myMessengerOfPeace);
            else {
                ((MessengerOfPeace) myMessengerOfPeace).deactivateCards();
                game.getCurrentPlayer().changeLP(-100);
            }
        }
        SpellAndTrapCard opponentMessengerOfPeace = game.getTheOtherPlayer().getField().getThisActivatedCard("Messenger of peace");
        if (opponentMessengerOfPeace != null)
            ((MessengerOfPeace) opponentMessengerOfPeace).deactivateCards();
    }


    private String[] makeSpellZone(ArrayList<SpellAndTrapCard> spellZone) {
        //TODO needs to go to Output class
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
        //TODO needs to go to Output class
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

    private void showGameBoard(Duelist opponent, Duelist current) {
        //TODO needs to go to Output class
        String opponentNickname = opponent.getNickname() + ": " + opponent.getLP();
        int opponentHandCount = opponent.getField().getHand().size();
        int opponentDeckNumber = opponent.getField().getDeckZone().size();
        int opponentGraveYardCount = opponent.getField().getGraveyard().size();
        var opponentFieldZone = "O ";
        if (opponent.getField().getFieldZone() == null) opponentFieldZone = "E ";
        String[] opponentSpellZone = makeSpellZone(opponent.getField().getSpellAndTrapCards());
        String[] opponentMonsterZone = makeMonsterZone(opponent.getField().getMonsterCards());
        String currentNickname = current.getNickname() + ": " + current.getLP();
        int currentDeckNumber = current.getField().getDeckZone().size();
        int currentGraveYardCount = current.getField().getGraveyard().size();
        int currentHandCount = current.getField().getHand().size();
        var currentFieldZone = "O ";
        if (current.getField().getFieldZone() == null) currentFieldZone = "E ";
        String[] currentSpellZone = makeSpellZone(current.getField().getSpellAndTrapCards());
        String[] currentMonsterZone = makeMonsterZone(current.getField().getMonsterCards());
        StringBuilder board = new StringBuilder();
        board.append(opponentNickname).append("\n");
        board.append("\tc ".repeat(opponentHandCount));
        board.append("\n").append(opponentDeckNumber).append("\n\t");
        board.append(opponentSpellZone[3]).append("\t").append(opponentSpellZone[1]).append("\t").append(opponentSpellZone[0]).append("\t").append(opponentSpellZone[2]).append("\t").append(opponentSpellZone[4]).append("\n\t");
        board.append(opponentMonsterZone[3]).append("\t").append(opponentMonsterZone[1]).append("\t").append(opponentMonsterZone[0]).append("\t").append(opponentMonsterZone[2]).append("\t").append(opponentMonsterZone[4]).append("\n");
        board.append(opponentGraveYardCount).append("\t\t\t\t\t\t").append(opponentFieldZone).append("\n");
        board.append("\n--------------------------\n\n");
        board.append(currentFieldZone).append("\t\t\t\t\t\t").append(currentGraveYardCount).append("\n\t");
        board.append(currentMonsterZone[4]).append("\t").append(currentMonsterZone[2]).append("\t").append(currentMonsterZone[0]).append("\t").append(currentMonsterZone[1]).append("\t").append(currentMonsterZone[3]).append("\n\t");
        board.append(currentSpellZone[4]).append("\t").append(currentSpellZone[2]).append("\t").append(currentSpellZone[0]).append("\t").append(currentSpellZone[1]).append("\t").append(currentSpellZone[3]).append("\n");
        board.append("  \t\t\t\t\t\t").append(currentDeckNumber).append("\n");
        board.append("c \t".repeat(currentHandCount));
        board.append("\n").append(currentNickname);
        IO.getInstance().printString(board.toString());
    }

    private void firstMainPhase() {
        IO.getInstance().printPhase("main phase 1");
        showGameBoard(game.getTheOtherPlayer(), game.getCurrentPlayer());
    }

    public void battlePhase() {
        if (!game.getCurrentPlayer().isAbleToAttack()) {
            game.getCurrentPlayer().setAbleToAttack(true);
            nextPhase();
            return;
        }
        IO.getInstance().printPhase("battle phase");
    }

    private void secondMainPhase() {
        IO.getInstance().printPhase("main phase 2");
        showGameBoard(game.getTheOtherPlayer(), game.getCurrentPlayer());
    }

    public void endPhase() {
        IO.getInstance().printPhase("end phase");
        reset();
        handleSwordOfRevealingLight();
    }

    private void reset() {
        game.getCurrentPlayer().getField().resetAllCards();
        game.getTheOtherPlayer().getField().resetAllCards();
        toReset.forEach(Scanner::reset);
        toReset = new ArrayList<>();
    }

    public void handleSwordOfRevealingLight() {
        SwordsOfRevealingLight sword = (SwordsOfRevealingLight) game.getTheOtherPlayer().getField().getThisActivatedCard("Swords of Revealing Light");
        if (sword != null) {
            sword.counter++;
            if (sword.counter == 3) {
                moveSpellOrTrapToGYFromSpellZone(sword);
                sword.counter = 0;
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
        IO.getInstance().cardSelected();
        showGameBoard(game.getTheOtherPlayer(), game.getCurrentPlayer());
    }


    private Duelist getPlayerByCard(boolean myCard) {
        return myCard ? game.getCurrentPlayer() : game.getTheOtherPlayer();
    }

    private boolean errorForSelecting(Duelist thisPlayer, int number, CardStatusInField cardStatusInField) {
        if (cardStatusInField.equals(CardStatusInField.HAND) &&
                (number >= thisPlayer.getField().getHand().size() || number < 0)) {
            IO.getInstance().invalidSelection();
            return false;
        }
        if (cardStatusInField.equals(CardStatusInField.MONSTER_FIELD) &&
                (number >= thisPlayer.getField().getMonsterCards().size() || number < 0)) {
            IO.getInstance().invalidSelection();
            return false;
        }
        if (cardStatusInField.equals(CardStatusInField.SPELL_FIELD) &&
                (number >= thisPlayer.getField().getSpellAndTrapCards().size() || number < 0)) {
            IO.getInstance().invalidSelection();
            return false;
        }
        if (cardStatusInField.equals(CardStatusInField.FIELD_ZONE) &&
                thisPlayer.getField().getFieldZone() == null) {
            IO.getInstance().noCardInPosition();
            return false;
        }
        return true;
    }

    public void deselectCard() {
        if (game.getSelectedCard() == null) {
            IO.getInstance().noCardSelected();
            return;
        }
        game.setSelectedCard(null);
        IO.getInstance().cardDeselected();
        showGameBoard(game.getTheOtherPlayer(), game.getCurrentPlayer());
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
        if (game.getCurrentPhase() == Phases.FIRST_MAIN_PHASE || game.getCurrentPhase() == Phases.SECOND_MAIN_PHASE)
            AI.getInstance().summonMonster();
        else if (game.getCurrentPhase() == Phases.BATTLE_PHASE) AI.getInstance().attack(getGame().getTheOtherPlayer());
        nextPhase();
    }

    public void summon() { //TODO what the fuck? test shode vali ghermeze hanuz :|
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
        IO.getInstance().summoned();
        showGameBoard(game.getTheOtherPlayer(), game.getCurrentPlayer());
        torrentialTribute();
    }


    private boolean handleSpecialCasesBeforeSummon() {
        //TODO is it ok that this if and the next if have different return true; positions?
        if (getGame().getCurrentPlayer() instanceof Account && !isSummonValid()) return true;
        if (game.getSelectedCard().getName().equals("The Tricky") && DuelView.getInstance().ordinaryOrSpecial()) {
            theTricky();
            return true;
        }
        if (game.getSelectedCard().getName().equals("Gate Guardian")) {
            if (DuelView.getInstance().summonGateGuardian()) {
                gateGuardian();
            }
            return true;
        }
        if (game.getSelectedCard().getName().equals("Beast King Barbaros")) {
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
        ((MonsterCard) game.getSelectedCard()).changeAttackPower((int) (Stream.concat(game.getCurrentPlayer().getField().getMonsterCards().stream(), game.getTheOtherPlayer().getField().getMonsterCards().stream()).filter(m -> m instanceof CommandKnight).count() * 400));
        return false;
    }

    private void handleSpecialCasesAfterSummon() {
        if (game.getSelectedCard().getName().equals("Terratiger, the Empowered Warrior"))
            terraTigerMethod();
        else if (game.getSelectedCard() instanceof CommandKnight)
            ((CommandKnight) game.getSelectedCard()).specialMethod();
    }

    public boolean handleTrapHole(MonsterCard monsterCard) {
        SpellAndTrapCard trapHoleCard = game.getTheOtherPlayer().getField().getSpellAndTrapCard("Trap Hole");
        if (monsterCard.getClassAttackPower() >= 1000 && trapHoleCard != null &&
                DuelView.getInstance().wantsToActivateTrap("Trap Hole")) {
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
            int number = DuelView.getInstance().getTribute();
            if (isTributeValid(number))
                addMonsterToGYFromMonsterZone(game.getCurrentPlayer().getField().getMonsterCards().get(number));
        } else if (monsterCard.getLevel() == 7 || monsterCard.getLevel() == 8) {
            int firstNumber = DuelView.getInstance().getTribute();
            int secondNumber = DuelView.getInstance().getTribute();
            if (isTributeValid(firstNumber) && isTributeValid(secondNumber) && firstNumber != secondNumber) {
                addMonsterToGYFromMonsterZone(game.getCurrentPlayer().getField().getMonsterCards().get(firstNumber));
                addMonsterToGYFromMonsterZone(game.getCurrentPlayer().getField().getMonsterCards().get(secondNumber - 1));
            }
        }
    }

    public boolean isTributeValid(int number) {
        if (game.getCurrentPlayer().getField().getMonsterCards().size() <= number) {
            IO.getInstance().noMonster();
            return false;
        }
        return true;
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
        if (selectedCard.getName().equals("Gate Guardian")) {
            IO.getInstance().cantSet();
            return;
        }
        summonWithTribute();
        game.getCurrentPlayer().getField().getMonsterCards().add(selectedCard);
        game.getCurrentPlayer().getField().getHand().remove(selectedCard);
        selectedCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_DOWN);
        game.getSelectedCard().setHasBeenSetOrSummoned(true);
        game.setSelectedCard(null);
        game.setSummonedInThisTurn(true);
        IO.getInstance().setSuccessfully();
    }

    private boolean isSettingMonsterValid() {
        if (game.getSelectedCard() == null) {
            IO.getInstance().cardNotSelected();
            return false;
        }
        if (!game.getCurrentPlayer().getField().getHand().contains(game.getSelectedCard())) {
            IO.getInstance().cantSet();
            return false;
        }
        MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
        if (monsterCard.getCardType().equals("Ritual")) {
            IO.getInstance().cantSet();
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
        IO.getInstance().positionChanged();
        showGameBoard(game.getTheOtherPlayer(), game.getCurrentPlayer());
    }

    private boolean isSetPositionValid(boolean isAttack) {
        if (game.getSelectedCard() == null) {
            IO.getInstance().cardNotSelected();
            return false;
        }
        if (!(game.getSelectedCard() instanceof MonsterCard) ||
                !game.getCurrentPlayer().getField().getMonsterCards().contains(game.getSelectedCard())) {
            IO.getInstance().cannotChangePosition();
            return false;
        }
        if (!(game.getCurrentPhase() == Phases.FIRST_MAIN_PHASE || game.getCurrentPhase() == Phases.SECOND_MAIN_PHASE)) {
            IO.getInstance().wrongPhase();
            return false;
        }
        MonsterCard selectedCard = (MonsterCard) game.getSelectedCard();
        if ((isAttack && selectedCard.getMonsterCardModeInField() == MonsterCardModeInField.ATTACK_FACE_UP) ||
                (!isAttack && (selectedCard.getMonsterCardModeInField() == MonsterCardModeInField.DEFENSE_FACE_UP ||
                        selectedCard.getMonsterCardModeInField() == MonsterCardModeInField.DEFENSE_FACE_DOWN))) {
            IO.getInstance().alreadyInPosition();
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

    public void flipSummon() {
        if (!isFlipSummonValid()) return;
        MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
        if (handleTrapHole(monsterCard)) return;
        game.setSelectedCard(null);
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        IO.getInstance().flipSummoned();
        showGameBoard(game.getTheOtherPlayer(), game.getCurrentPlayer());
    }

    private boolean isFlipSummonValid() {
        if (game.getSelectedCard() == null) {
            IO.getInstance().cardNotSelected();
            return false;
        }
        if (!(game.getSelectedCard() instanceof MonsterCard) ||
                !game.getCurrentPlayer().getField().getMonsterCards().contains(game.getSelectedCard())) {
            IO.getInstance().cannotChangePosition();
            return false;
        }
        if (!(game.getCurrentPhase() == Phases.FIRST_MAIN_PHASE || game.getCurrentPhase() == Phases.SECOND_MAIN_PHASE)) {
            IO.getInstance().wrongPhase();
            return false;
        }
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
        opponentMonsterPositionNumber--; //TODO check these numbers and shits
        MonsterCard attacked = game.getTheOtherPlayer().getField().getMonsterCards().get(opponentMonsterPositionNumber);
        MonsterCard attacker = (MonsterCard) game.getSelectedCard();
        if (game.getTheOtherPlayer().getField().getThisActivatedCard("Swords of Revealing Light") != null) return;
        SpellAndTrapCard magicCylinderCard = game.getTheOtherPlayer().getField().getSpellAndTrapCard("Magic Cylinder");
        if (magicCylinderCard != null) {
            magicCylinder(attacker, magicCylinderCard);
            return;
        }
        SpellAndTrapCard mirrorForceCard = game.getTheOtherPlayer().getField().getSpellAndTrapCard("Mirror Force");
        if (mirrorForceCard != null) {
            mirrorForce(mirrorForceCard, attacker.getOwner());
            return;
        }
        if (attacked instanceof Suijin && !((Suijin) attacked).isHasBeenUsedInGeneral() &&
                DuelView.getInstance().wantsToActivate("Suijin")) {
            ((Suijin) attacked).specialMethod(attacker);
        }
        if (attacked.getName().equals("Texchanger") &&
                DuelView.getInstance().wantsToActivate("Texchanger")) {
            texChanger(attacked);
            return;
        }
        if (attacked.getMonsterCardModeInField() == MonsterCardModeInField.ATTACK_FACE_UP)
            attackInAttack(attacked, attacker);
        else attackInDefense(attacked, attacker);
        attacker.setAttacked(true);
        game.setSelectedCard(null);
        showGameBoard(game.getTheOtherPlayer(), game.getCurrentPlayer());
    }

    public boolean handleMirageDragon(String name) {
        Duelist opponent;
        opponent = game.getCurrentPlayer().getUsername().equals(name) ? game.getTheOtherPlayer() : game.getCurrentPlayer();
        MonsterCard mirageDragon = opponent.getMirageDragon();
        return mirageDragon != null && mirageDragon.getMonsterCardModeInField() != MonsterCardModeInField.DEFENSE_FACE_DOWN;
    }

    private boolean negateAttack() {
        SpellAndTrapCard negateAttackCard = game.getTheOtherPlayer().getField().getSpellAndTrapCard("Negate Attack");
        if (negateAttackCard != null && DuelView.getInstance().wantsToActivate("Negate Attack")) {
            makeChain(game.getCurrentPlayer(), game.getTheOtherPlayer());
            moveSpellOrTrapToGYFromSpellZone(negateAttackCard);
            return true;
        }
        return false;
    }

    private void attackInDefense(MonsterCard attacked, MonsterCard attacker) {
        if (attacked.getName().equals("Marshmallon")) game.getCurrentPlayer().changeLP(-1000);
        if (attacked.getMonsterCardModeInField() == MonsterCardModeInField.DEFENSE_FACE_DOWN)
            IO.getInstance().revealCard(attacked.getName());
        if (attacked.getThisCardDefensePower() < attacker.getThisCardAttackPower()) {
            moveToGraveyardAfterAttack(attacked, attacker);
            IO.getInstance().wonInDefense();
            return;
        }
        if (attacked.getThisCardDefensePower() == attacker.getThisCardAttackPower()) {
            attacked.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
            IO.getInstance().drawInDefense();
            return;
        }
        int damage = attacker.getThisCardAttackPower() - attacked.getThisCardDefensePower();
        game.getCurrentPlayer().changeLP(damage);
        attacked.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        IO.getInstance().lostInDefense(-damage);
    }

    private void attackInAttack(MonsterCard attacked, MonsterCard attacker) {
        if (attacked.getThisCardAttackPower() < attacker.getThisCardAttackPower()) {
            moveToGraveyardAfterAttack(attacked, attacker);
            if (!attacked.getName().equals("Exploder Dragon")) {
                int damage = attacked.getThisCardAttackPower() - attacker.getThisCardAttackPower();
                game.getTheOtherPlayer().changeLP(damage);
                IO.getInstance().wonAttackInAttack(-damage);
            } else IO.getInstance().lostInAttack(0);
            return;
        }
        if (attacked.getThisCardAttackPower() == attacker.getThisCardAttackPower()) {
            moveToGraveyardAfterAttack(attacker, attacked);
            moveToGraveyardAfterAttack(attacked, attacker);
            IO.getInstance().drawInAttack();
            return;
        }
        if (!attacker.getName().equals("Exploder Dragon")) {
            int damage = attacker.getThisCardAttackPower() - attacked.getThisCardAttackPower();
            game.getCurrentPlayer().changeLP(damage);
            IO.getInstance().lostInAttack(-damage);
        } else IO.getInstance().lostInAttack(0);
        moveToGraveyardAfterAttack(attacker, attacked);
    }

    private boolean isAttackValid(int opponentMonsterPositionNumber) {
        if (game.getSelectedCard() == null) {
            IO.getInstance().cardNotSelected();
            return false;
        }
        if (!(game.getSelectedCard() instanceof MonsterCard) ||
                !game.getCurrentPlayer().getField().getMonsterCards().contains(game.getSelectedCard())) {
            IO.getInstance().cannotAttack();
            return false;
        }
        if (game.getCurrentPhase() != Phases.BATTLE_PHASE) {
            IO.getInstance().wrongPhase();
            return false;
        }
        MonsterCard selectedCard = (MonsterCard) game.getSelectedCard();
        if (selectedCard.isAttacked()) {
            IO.getInstance().alreadyAttacked();
            return false;
        }
        if (!selectedCard.isAbleToAttack()) {
            IO.getInstance().cannotAttack();
            return false;
        }
        if (game.getTheOtherPlayer().getField().getMonsterCards().size() < opponentMonsterPositionNumber) {
            IO.getInstance().noCardHere();
            return false;
        }
        MonsterCard attacked = game.getTheOtherPlayer().getField().getMonsterCards().get(opponentMonsterPositionNumber - 1);
        if (!attacked.isAbleToBeRemoved()) { //TODO when is this set as false
            IO.getInstance().cannotAttackThisCard();
            return false;
        }
        if (selectedCard.isAttacked()) { //TODO this also
            IO.getInstance().cannotAttack();
            return false;
        }
        return true;
    }

    public void directAttack() {
        if (!isDirectAttackValid()) return;
        MonsterCard selectedCard = (MonsterCard) game.getSelectedCard();
        int damage = selectedCard.getThisCardAttackPower();
        game.getTheOtherPlayer().changeLP(-damage);
        selectedCard.setAttacked(true);
        game.setSelectedCard(null);
        IO.getInstance().receivedDamage(damage);
        showGameBoard(game.getTheOtherPlayer(), game.getCurrentPlayer());
    }

    public boolean isDirectAttackValid() {
        if (game.getSelectedCard() == null) {
            IO.getInstance().cardNotSelected();
            return false;
        }
        if (!(game.getSelectedCard() instanceof MonsterCard) ||
                !game.getCurrentPlayer().getField().getMonsterCards().contains(game.getSelectedCard())) {
            IO.getInstance().cannotAttack();
            return false;
        }
        if (game.getCurrentPhase() != Phases.BATTLE_PHASE) {
            IO.getInstance().wrongPhase();
            return false;
        }
        MonsterCard selectedCard = (MonsterCard) game.getSelectedCard();
        if (selectedCard.isAttacked()) {
            IO.getInstance().alreadyAttacked();
            return false;
        }
        if (!game.getTheOtherPlayer().getField().getMonsterCards().isEmpty()) {
            IO.getInstance().cannotDirectAttack();
            return false;
        }
        return true;
    }

    public void activateSpell() {
        if (!isActivatingSpellValid()) return;
        if (magicJamamer((SpellAndTrapCard) game.getSelectedCard())) return;
        SpellAndTrapCard selectedCard = (SpellAndTrapCard) game.getSelectedCard();
        selfAbsorption();
        if (selectedCard.getProperty().equals("Field")) {
            if (game.getCurrentPlayer().getField().getFieldZone() != null)
                game.getCurrentPlayer().getField().getGraveyard().add(game.getCurrentPlayer().getField().getFieldZone());
            game.getCurrentPlayer().getField().setFieldZone(selectedCard);
        } else if (!game.getCurrentPlayer().getField().getSpellAndTrapCards().contains(selectedCard))
            game.getCurrentPlayer().getField().getSpellAndTrapCards().add(selectedCard);
        selectedCard.setActive(true);
        callSpellAndTrapMethod(selectedCard);
        game.setSelectedCard(null);
        if (selectedCard.isActive()) IO.getInstance().spellActivated();
        showGameBoard(game.getTheOtherPlayer(), game.getCurrentPlayer());
    }

    public void selfAbsorption() {
        makeChain(game.getCurrentPlayer(), game.getTheOtherPlayer());
        if (game.getCurrentPlayer().getField().getThisActivatedCard("Spell Absorption") != null)
            game.getCurrentPlayer().changeLP(500);
        if (game.getTheOtherPlayer().getField().getThisActivatedCard("Spell Absorption") != null)
            game.getTheOtherPlayer().changeLP(500);
    }

    public boolean isActivatingSpellValid() {
        if (game.getSelectedCard() == null) {
            IO.getInstance().noCardSelected();
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
        switch (card.getName()) {
            case "Monster Reborn" -> monsterReborn(card);
            case "Terraforming" -> terraforming(card);
            case "Pot of Greed" -> potOfGreed(card);
            case "Raigeki" -> raigeki(card);
            case "Change of Heart" -> changeOfHeart((ChangeOfHeart) card);
            case "Harpie's Feather Duster" -> harpiesFeatherDuster(card);
            case "Dark Hole" -> darkHole(card);
            case "Messenger of peace" -> ((MessengerOfPeace) card).deactivateCards();
            case "Mystical space typhoon" -> mysticalSpaceTyphoon(card);
            case "Yami" -> yami();
            case "Forest" -> forest();
            case "Closed Forest" -> closedForest();
            case "Umiiruka" -> umiiruka();
            case "Sword of dark destruction", "Black Pendant", "United We Stand", "Magnum Shield" -> equipMonster(card);
            case "Advanced Ritual Art" -> advancedRitualArt(card);
            case "Twin Twisters" -> twinTwisters(card);
            case "Swords of Revealing Light" -> ((SwordsOfRevealingLight) card).specialMethod(game.getTheOtherPlayer());
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
        if (DuelView.getInstance().wantsToActivate("Call Of The Haunted")) {
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
        SpellAndTrapCard solemnWarning = game.getTheOtherPlayer().getField().getSpellAndTrapCard("Solemn Warning");
        if (solemnWarning != null && DuelView.getInstance().wantsToActivateTrap("Solemn Warning")) {
            makeChain(game.getCurrentPlayer(), game.getTheOtherPlayer());
            moveSpellOrTrapToGYFromSpellZone(solemnWarning);
            game.getTheOtherPlayer().changeLP(-2000);
            addMonsterToGYFromHand(monsterCard);
            return true;
        }
        return false;
    }

    public boolean magicJamamer(SpellAndTrapCard spellAndTrapCard) {
        SpellAndTrapCard magicJamamer = game.getTheOtherPlayer().getField().getSpellAndTrapCard("Magic Jamamer");
        if (magicJamamer != null && DuelView.getInstance().wantsToActivateTrap("Magic Jamamer")) {
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
        if (DuelView.getInstance().wantsToActivate("Time Seal")) {
            moveSpellOrTrapToGYFromSpellZone(timeSealCard);
            opponent.setAbleToDraw(false);
        }
    }

    public void torrentialTribute() {
        SpellAndTrapCard torrentialTributeCard = game.getTheOtherPlayer().getField().getSpellAndTrapCard("Torrential Tribute");
        if (torrentialTributeCard != null && DuelView.getInstance().wantsToActivateTrap("Torrential Tribute")) {
            makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
            moveSpellOrTrapToGYFromSpellZone(torrentialTributeCard);
            ArrayList<MonsterCard> allMonsters = getAllMonsterCards();
            int size = allMonsters.size();
            for (var i = 0; i < size; i++)
                addMonsterToGYFromMonsterZone(allMonsters.get(0));
        }
    }

    public void mindCrush(SpellAndTrapCard spellAndTrapCard, Duelist opponent) {
        if (DuelView.getInstance().wantsToActivateTrap("Mind Crush")) {
            String cardName = DuelView.getInstance().getCardName();
            if (!opponent.getCardInHand(cardName)) randomlyRemoveFromHand(spellAndTrapCard.getOwner());
            else opponent.getField().getHand().removeAll(opponent.getField().getHand().stream()
                    .filter(c -> c.getName().equals(cardName)).collect(Collectors.toList()));
            moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
        }
    }

    public void randomlyRemoveFromHand(Duelist duelist) {
        var card = duelist.getField().getHand().get(random.nextInt(duelist.getField().getHand().size()));
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
            case "Sword of dark destruction" -> ((SwordOfDarkDestruction) equipSpell).setEquippedMonster(monsterToEquip);
            case "Black Pendant" -> ((BlackPendant) equipSpell).setEquippedMonster(monsterToEquip);
            case "United We Stand" -> ((UnitedWeStand) equipSpell).setEquippedMonster(monsterToEquip);
            case "Magnum Shield" -> ((MagnumShield) equipSpell).setEquippedMonster(monsterToEquip);
        }
    }

    public void magicCylinder(MonsterCard attacker, SpellAndTrapCard magicCylinder) {
        if (!DuelView.getInstance().wantsToActivateTrap("Magic Cylinder")) return;
        makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        decreaseLPWithTrap(attacker.getOwner(), attacker.getClassAttackPower());
        addMonsterToGYFromMonsterZone(attacker);
        moveSpellOrTrapToGYFromSpellZone(magicCylinder);
    }

    public void mirrorForce(SpellAndTrapCard mirrorForce, Duelist opponent) {
        makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        ArrayList<MonsterCard> monsters = new ArrayList<>(opponent.getField().getMonsterCards());
        for (MonsterCard monster : monsters)
            if (monster.getMonsterCardModeInField() == MonsterCardModeInField.ATTACK_FACE_UP)
                addMonsterToGYFromMonsterZone(monster);
        moveSpellOrTrapToGYFromSpellZone(mirrorForce);
    }

    private void decreaseLPWithTrap(Duelist duelist, int amount) { //TODO
        if (duelist.getField().getThisActivatedCard("Ring of defense") == null)
            duelist.changeLP(-amount);
    }

    private void advancedRitualArt(SpellAndTrapCard spellAndTrapCard) {
        if (ritualSummon())
            moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    public void twinTwisters(SpellAndTrapCard spellAndTrapCard) {
        makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
        Card removeFromHand;
        if (spellAndTrapCard.getOwner().equals(game.getCurrentPlayer()))
            removeFromHand = DuelView.getInstance().getCardFromHand();
        else
            removeFromHand = DuelView.getInstance().getCardFromTheOtherPlayerHand();
        if (removeFromHand == null) return;
        spellAndTrapCard.getOwner().getField().getHand().remove(removeFromHand);
        spellAndTrapCard.getOwner().getField().getGraveyard().add(removeFromHand);
        int numOfSpellCardsToDestroy = DuelView.getInstance().numOfSpellCardsToDestroy();
        for (int i = 0; i < numOfSpellCardsToDestroy; i++) {
            boolean isMine = DuelView.getInstance().isMine();
            if (game.getTheOtherPlayer().equals(spellAndTrapCard.getOwner())) isMine = !isMine;
            SpellAndTrapCard toDestroy = isMine ? DuelView.getInstance().getFromMyField() : DuelView.getInstance().getFromOpponentField();
            if (toDestroy == null) continue;
            moveSpellOrTrapToGYFromSpellZone(toDestroy);
        }
    }

    public void umiiruka() {
        makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        getAllMonsterCards().stream().filter(m -> m.getMonsterType().equals("Aqua")).forEach(a -> {
            a.setThisCardAttackPower(a.getThisCardAttackPower() + 500);
            a.setThisCardDefensePower(a.getThisCardDefensePower() - 400);
        });
    }

    public void closedForest() {
        makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        int amount = game.getCurrentPlayer().getField().getGraveyard().size() * 100;
        game.getCurrentPlayer().getField().getMonsterCards().stream()
                .filter(m -> m.getMonsterType().equals("Beast") || m.getMonsterType().equals("Beast-Warrior"))
                .forEach(b -> b.setThisCardAttackPower(b.getThisCardAttackPower() + amount));
    }

    public void forest() {
        makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        getAllMonsterCards().stream().filter(m -> m.getMonsterType().equals("Insect") || m.getMonsterType().equals("Beast") ||
                m.getMonsterType().equals("Beast-Warrior")).forEach(m -> {
            m.setThisCardAttackPower(m.getThisCardAttackPower() + 200);
            m.setThisCardDefensePower(m.getThisCardDefensePower() + 200);
        });
    }

    public void yami() {
        makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        getAllMonsterCards().stream().filter(m -> m.getMonsterType().equals("Fiend") || m.getMonsterType().equals("Spellcaster")).forEach(m -> {
            m.setThisCardAttackPower(m.getThisCardAttackPower() + 200);
            m.setThisCardDefensePower(m.getThisCardDefensePower() + 200);
        });
        getAllMonsterCards().stream().filter(m -> m.getMonsterType().equals("Fairy")).forEach(m -> {
            m.setThisCardAttackPower(m.getThisCardAttackPower() - 200);
            m.setThisCardDefensePower(m.getThisCardDefensePower() - 200);
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
        makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        int myMonstersSize = game.getCurrentPlayer().getField().getMonsterCards().size();
        int opponentSize = game.getTheOtherPlayer().getField().getMonsterCards().size();
        //TODO could this be done by forEach()?
        for (var i = 0; i < myMonstersSize; i++)
            addMonsterToGYFromMonsterZone(game.getCurrentPlayer().getField().getMonsterCards().get(0));
        for (var i = 0; i < opponentSize; i++)
            addMonsterToGYFromMonsterZone(game.getTheOtherPlayer().getField().getMonsterCards().get(0));
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    public void harpiesFeatherDuster(SpellAndTrapCard spellAndTrapCard) {
        makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        int size = game.getTheOtherPlayer().getField().getSpellAndTrapCards().size();
        for (var i = 0; i < size; i++)
            moveSpellOrTrapToGYFromSpellZone(game.getTheOtherPlayer().getField().getSpellAndTrapCards().get(0));
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    public void changeOfHeart(ChangeOfHeart changeOfHeart) {
        MonsterCard toHijack = DuelView.getInstance().getHijackedCard();
        if (toHijack != null && changeOfHeart.getOwner().getField().getMonsterCards().size() != 5) {
            makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
            changeOfHeart.setHijackedCard(toHijack);
        }
    }

    public void raigeki(SpellAndTrapCard spellAndTrapCard) {
        makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
        int size = game.getTheOtherPlayer().getField().getMonsterCards().size();
        for (var i = 0; i < size; i++)
            addMonsterToGYFromMonsterZone(game.getTheOtherPlayer().getField().getMonsterCards().get(0));
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    public void potOfGreed(SpellAndTrapCard spellAndTrapCard) {
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
            makeChain(getGame().getCurrentPlayer(), game.getTheOtherPlayer());
            if (game.getCurrentPlayer().getField().getHand().size() != 6)
                game.getCurrentPlayer().getField().getHand().add(fieldSpell);
        }
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    public void monsterReborn(SpellAndTrapCard spellAndTrapCard) {
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
        IO.getInstance().setSuccessfully();
    }

    private boolean isSettingSpellAndTrapValid() {
        if (game.getSelectedCard() == null) {
            IO.getInstance().noCardSelected();
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
        showGameBoard(game.getTheOtherPlayer(), game.getCurrentPlayer());
    }

    public boolean ritualSummon() {
        if (!errorForRitualSummon()) return false;
        MonsterCard ritualMonster = DuelView.getInstance().getRitualCard();
        ArrayList<MonsterCard> toTribute = DuelView.getInstance().getTributes();
//        System.out.println(toTribute);
        if (Objects.isNull(toTribute)) return false;
        while (!isSumOfTributesValid(toTribute, ritualMonster)) {
            toTribute = DuelView.getInstance().getTributes();
            IO.getInstance().invalidTributeSum();
        }
        chooseMonsterMode(ritualMonster);
        game.getCurrentPlayer().getField().getMonsterCards().add(ritualMonster);
        for (MonsterCard monsterCard : toTribute) {
            addMonsterToGYFromMonsterZone(monsterCard);
        }
        moveSpellOrTrapToGYFromSpellZone((SpellAndTrapCard) game.getSelectedCard());
        game.setSelectedCard(null);
        IO.getInstance().summoned();
        return true;
    }

    private void chooseMonsterMode(MonsterCard monsterCard) {
        String mode = DuelView.getInstance().monsterMode().toLowerCase();
        if (mode.equals("attack"))
            monsterCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        else monsterCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
    }

    private boolean isSumOfTributesValid(ArrayList<MonsterCard> toTribute, MonsterCard ritualMonster) {
        return toTribute.stream().mapToInt(MonsterCard::getLevel).sum() == ritualMonster.getLevel();
    }

//    private boolean errorAfterChoosingRitualCard(MonsterCard ritualMonster) {
//        if (!ritualMonster.getCardType().equals("Ritual")) {
//            IO.getInstance().cannotRitualSummonThisCard();
//            return false;
//        }
//        if (!game.getCurrentPlayer().getField().isTributesLevelSumValid(ritualMonster.getLevel(),
//                game.getCurrentPlayer().getField().getMonsterCards().size())) {
//            IO.getInstance().cannotRitualSummonThisCard();
//            return false;
//        }
//        return true;
//    }

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

    public void showGraveyard() {
        String toPrint = game.getCurrentPlayer().getField().showGraveyard();
        IO.getInstance().printString(toPrint);
    }

    public void showSelectedCard() {
        if (!errorsForShowingSelectedCard()) return;
        IO.getInstance().printString(game.getSelectedCard().toString());
    }

    private boolean errorsForShowingSelectedCard() {
        if (game.getSelectedCard() == null) {
            IO.getInstance().noCardSelected();
            return false;
        }
        if (game.getSelectedCard().getOwner().equals(game.getTheOtherPlayer())) {
            if (game.getSelectedCard() instanceof MonsterCard) {
                if (((MonsterCard) game.getSelectedCard()).getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_DOWN)) {
                    IO.getInstance().cardNotVisible();
                    return false;
                }
            } else {
                if (!((SpellAndTrapCard) game.getSelectedCard()).isActive()) {
                    IO.getInstance().cardNotVisible();
                    return false;
                }
            }
        }
        return true;
    }

    public void cheatShowRivalHand() {
        var rivalHand = new StringBuilder();
        game.getTheOtherPlayer().getField().getHand().forEach(c -> rivalHand.append(c.getName()).append("\n"));
        if (!rivalHand.isEmpty()) rivalHand.setLength(rivalHand.length() - 1);
        IO.getInstance().printString(rivalHand.toString());
    }

    public void cheatIncreaseLP(int amount) {
        game.getCurrentPlayer().changeLP(amount);
    }

    public void cheatSetWinner() {
        game.finishGame(game.getTheOtherPlayer());
    }

    public void cheatSeeMyDeck() {
        var toPrint = new StringBuilder();
        game.getCurrentPlayer().getField().getDeckZone().stream().filter(Objects::nonNull).forEach(c -> toPrint.append(c.getName()).append("\n"));
        toPrint.setLength(toPrint.length() - 1);
        IO.getInstance().printString(toPrint.toString());
    }

    public void cheatDecreaseLP(int amount) {
        game.getTheOtherPlayer().changeLP(-amount);
    }

    public void surrender() {
        game.finishGame(game.getCurrentPlayer());
    }

    private void moveToGraveyardAfterAttack(MonsterCard toBeRemoved, MonsterCard remover) {
        if (toBeRemoved.getName().equals("Marshmallon")) return;
        addMonsterToGYFromMonsterZone(toBeRemoved);
        if (toBeRemoved.getName().equals("Exploder Dragon") ||
                toBeRemoved.getName().equals("Yomi Ship")) {
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
            SpellAndTrapCard supplySquad = field.getThisActivatedCard("Supply Squad");
            if (supplySquad != null && !supplySquad.isHasBeenUsedInThisTurn()) {
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
        if (DuelView.getInstance().wantsToActivate("Scanner")) {
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
        if (!DuelView.getInstance().wantsToActivate("Terratiger, the Empowered Warrior")) return;
        if (!errorForTerraTiger()) {
            IO.getInstance().invalidCard();
            return;
        }
        MonsterCard monsterCard = DuelView.getInstance().getMonsterCardFromHand();
        specialSummon(monsterCard, MonsterCardModeInField.DEFENSE_FACE_DOWN, game.getCurrentPlayer().getField().getHand());
        game.setSelectedCard(null);
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
            if (spellSize > 0)
                for (int i = 0; i < spellSize; i++)
                    moveSpellOrTrapToGYFromSpellZone(opponentSpellCards.get(0));
            if (monsterSize > 0)
                for (int i = 0; i < monsterSize; i++)
                    addMonsterToGYFromMonsterZone(opponentMonsterCards.get(0));
        }
        game.getCurrentPlayer().getField().getHand().remove(barbaros);
        game.getCurrentPlayer().getField().getMonsterCards().add(barbaros);
        barbaros.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        game.getSelectedCard().setHasBeenSetOrSummoned(true);
        game.setSelectedCard(null);
        game.setSummonedInThisTurn(true);
        IO.getInstance().summoned();
        showGameBoard(game.getTheOtherPlayer(), game.getCurrentPlayer());
    }

    public void texChanger(MonsterCard attacked) {
        if (!attacked.isHasBeenUsedInThisTurn()) {
            ArrayList<Card> toRemoveFrom = new ArrayList<>();
            int whereFrom = DuelView.getInstance().whereToSummonFrom();
            MonsterCard monsterCard = null;
            do {
                switch (whereFrom) {
                    case 1 -> {
                        monsterCard = DuelView.getInstance().getMonsterCardFromHand(true);
                        toRemoveFrom = game.getCurrentPlayer().getField().getHand();
                    }
                    case 2 -> {
                        monsterCard = DuelView.getInstance().getFromMyDeck(true);
                        toRemoveFrom = game.getCurrentPlayer().getField().getDeckZone();
                    }
                    case 3 -> {
                        monsterCard = DuelView.getInstance().getFromMyGY(true);
                        toRemoveFrom = game.getCurrentPlayer().getField().getGraveyard();
                    }
                }
                if (monsterCard == null) return;
            }
            while (!(monsterCard.getCardType().equals("Normal") &&
                    monsterCard.getMonsterType().equals("Cyberse")));

            specialSummon(monsterCard, MonsterCardModeInField.ATTACK_FACE_UP, toRemoveFrom);
            attacked.setHasBeenUsedInThisTurn(true);
        }
    }

    public void makeChain(Duelist currentPlayer, Duelist theOtherPlayer) {
        if (isChainActive) return;
        if (!canMakeChain(theOtherPlayer) && !canMakeChain(currentPlayer)) return;
        Duelist temp = theOtherPlayer;
        if (canMakeChain(theOtherPlayer) && canMakeChain(currentPlayer)) {
            IO.getInstance().makeChain();
            String command = IO.getInstance().getInputMessage();
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
        IO.getInstance().addToChain();
        String command = IO.getInstance().getInputMessage();
        if (command.toLowerCase().matches("n|no")) return;
        while (true) {
            if (actionForChain(currentPlayer)) return;
        }
    }

    public boolean actionForChain(Duelist temp) {
        IO.getInstance().selectCardToAdd();
        String inputMessage = IO.getInstance().getInputMessage();
        if (inputMessage.equalsIgnoreCase("cancel")) return true;
        SpellAndTrapCard card = temp.getField().getThisActivatedCard(inputMessage);
        forChain.add(card);
        return false;
    }

    public boolean canMakeChain(Duelist player) {
        ArrayList<String> cardNames = new ArrayList<>(
                List.of("Mind Crush", "Twin Twisters", "Mystical space typhoon", "Ring of defense", "Time Seal", "Call of The Haunted"));
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
            case "Mind Crush" -> mindCrush(card, opponent);
            case "Twin Twisters" -> twinTwisters(card);
            case "Mystical space typhoon" -> mysticalSpaceTyphoon(card);
            case "Time Seal" -> timeSeal(card, opponent);
            case "Call of The Haunted" -> callOfTheHaunted(card, card.getOwner().getField(), card.getOwner().equals(game.getCurrentPlayer()));
        }
    }

    public void exchangeCardsWithSideDeck(Account account) {
        if (DuelView.getInstance().wantsToExchange()) {
            String[] names = DuelView.getInstance().cardsToExchange();
            account.getField().exchangeCards(names[0], names[1]);
        }
    }

    public String sortFieldCards() {
        HashMap<Integer, SpellAndTrapCard> fields = new HashMap<>();
        ArrayList<Card> deck = game.getCurrentPlayer().getField().getDeckZone();
        int size = deck.size();
        for (int i = 0; i < size; i++)
            if (deck.get(i) instanceof SpellAndTrapCard) {
                SpellAndTrapCard spellAndTrapCard = (SpellAndTrapCard) deck.get(i);
                if (spellAndTrapCard.getProperty().equals("Field"))
                    fields.put(i + 1, spellAndTrapCard);
            }
        StringBuilder toPrint = new StringBuilder();
        for (Map.Entry mapElement : fields.entrySet()) {
            toPrint.append(mapElement.getKey()).append(": ").append(mapElement.getValue()).append("\n");
        }
            return toPrint.toString();
        }
}