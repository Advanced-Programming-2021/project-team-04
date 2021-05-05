package controller;

import model.*;
import view.ChainView;
import view.DuelView;
import view.Output;

import java.util.ArrayList;
import java.util.Random;

public class DuelController {
    private static DuelController singleInstance = null;
    private Game game;

    private DuelController() {

    }

    public static DuelController getInstance() {
        if (singleInstance == null)
            singleInstance = new DuelController();
        return singleInstance;
    }

    public void rockPaperScissor(String thisPlayer, String theOtherPlayer) {
        if (thisPlayer.equals(theOtherPlayer)) return;
        if (thisPlayer.matches("rock") && theOtherPlayer.matches("scissors"))
            game.getCurrentPlayer().increaseCountForRPS();
        else if (thisPlayer.matches("rock") && theOtherPlayer.matches("paper"))
            game.getTheOtherPlayer().increaseCountForRPS();
        else if (thisPlayer.matches("scissors") && theOtherPlayer.matches("paper"))
            game.getCurrentPlayer().increaseCountForRPS();
        else if (thisPlayer.matches("scissors") && theOtherPlayer.matches("rock"))
            game.getTheOtherPlayer().increaseCountForRPS();
        else if (thisPlayer.matches("paper") && theOtherPlayer.matches("rock"))
            game.getCurrentPlayer().increaseCountForRPS();
        else if (thisPlayer.matches("paper") && theOtherPlayer.matches("scissors"))
            game.getTheOtherPlayer().increaseCountForRPS();
        if ((game.getCurrentPlayer().getCountForRPS() == 2 && game.getTheOtherPlayer().getCountForRPS() == 0) ||
                game.getCurrentPlayer().getCountForRPS() == 3) {
            DuelView.getInstance().chooseStarter(game.getCurrentPlayer().getUsername());
            DuelView.getInstance().isRPSDone = true;
            return;
        }
        if ((game.getCurrentPlayer().getCountForRPS() == 0 && game.getTheOtherPlayer().getCountForRPS() == 2) ||
                game.getTheOtherPlayer().getCountForRPS() == 3) {
            DuelView.getInstance().chooseStarter(game.getTheOtherPlayer().getUsername());
            DuelView.getInstance().isRPSDone = true;
            return;
        }
    }

    public void chooseStarter(String username) {
        if (!game.getCurrentPlayer().getUsername().equals(username)) game.changeTurn();
        game.getCurrentPlayer().setCanDraw(false);
        game.getCurrentPlayer().setCanPlayerAttack(false);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    private void drawPhase() {
        Output.getInstance().printPhase("draw phase");
        if (!game.getCurrentPlayer().canDraw()) {
            game.getCurrentPlayer().setCanDraw(true);
            return;
        }
        Field field = game.getCurrentPlayer().getField();
        if (field.getDeckZone().isEmpty()) {
            game.finishGame(game.getCurrentPlayer());
            return;
        }
        field.getHand().add(field.getDeckZone().get(0));
        field.getDeckZone().remove(0);
    }

    private void standbyPhase() {
        Output.getInstance().printPhase("standby phase");
        Field field = game.getCurrentPlayer().getField();
        Field opponentField = game.getTheOtherPlayer().getField();
        handleSpecialCards(field, opponentField);
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

    private void handleCommandKnightAndHeraldOfCreation(Field field, Field opponentField) {
        for (MonsterCard monsterCard : field.getMonsterCards()) {
            if (monsterCard.getName().equals("Herald of Creation") &&
                    !monsterCard.isHasBeenUsedInThisTurn() &&
                    !(monsterCard.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_DOWN))) {
                heraldOfCreation();
                monsterCard.setHasBeenUsedInThisTurn(true);
            }
            if (monsterCard.getName().equals("Command Knight")) {
                CommandKnight commandKnight = (CommandKnight) monsterCard;
                commandKnight.specialMethod();
                commandKnight.setHasBeenUsedInThisTurn(true);
            }
        }
        for (MonsterCard monsterCard : opponentField.getMonsterCards()) {
            if (monsterCard.getName().equals("Command Knight")) {
                CommandKnight commandKnight = (CommandKnight) monsterCard;
                commandKnight.specialMethod();
                commandKnight.setHasBeenUsedInThisTurn(true);
            }
        }
    }

    private void handleCallOfTheHaunted(Field field, Field opponentField) {
        SpellAndTrapCard callOfTheHauntedCard = field.hasTrapCard("Call of The Haunted");
        if (callOfTheHauntedCard != null) callOfTheHaunted(callOfTheHauntedCard, field, true);
        callOfTheHauntedCard = opponentField.hasTrapCard("Call of The Haunted");
        if (callOfTheHauntedCard != null) callOfTheHaunted(callOfTheHauntedCard, opponentField, false);
    }

    private void handleMindCrush(Field field, Field opponentField) {
        SpellAndTrapCard mindCrushCard = field.hasTrapCard("Mind Crush");
        if (mindCrushCard != null) mindCrush(mindCrushCard, game.getTheOtherPlayer());
        mindCrushCard = opponentField.hasTrapCard("Mind Crush");
        if (mindCrushCard != null) mindCrush(mindCrushCard, game.getCurrentPlayer());
    }

    private void handleTimeSeal(Field field, Field opponentField) {
        SpellAndTrapCard timeSealCard = field.hasTrapCard("Time Seal");
        if (timeSealCard != null) timeSeal(timeSealCard, game.getTheOtherPlayer());
        timeSealCard = opponentField.hasTrapCard("Time Seal");
        if (timeSealCard != null) timeSeal(timeSealCard, game.getCurrentPlayer());
    }

    private void handleEquip(Field field) {
        SwordOfDarkDestruction swordOfDarkDestruction = (SwordOfDarkDestruction) field.hasThisCardActivated("Sword of dark destruction");
        if (swordOfDarkDestruction != null) swordOfDarkDestruction.equipMonster();
        BlackPendant blackPendant = (BlackPendant) field.hasThisCardActivated("Black Pendant");
        if (blackPendant != null) blackPendant.equipMonster();
        UnitedWeStand unitedWeStand = (UnitedWeStand) field.hasThisCardActivated("United We Stand");
        if (unitedWeStand != null) unitedWeStand.equipMonster();
        MagnumShield magnumShield = (MagnumShield) field.hasThisCardActivated("Magnum Shield");
        if (magnumShield != null) magnumShield.equipMonster();
    }

    private void handleField(Field field, Field opponentField) {
        if (field.getFieldZone().getName().equals("Umiiruka") ||
                opponentField.getFieldZone().getName().equals("Umiiruka")) umiiruka();
        if (field.getFieldZone().getName().equals("Forest") ||
                opponentField.getFieldZone().getName().equals("Forest")) forest();
        if (field.getFieldZone().getName().equals("Closed Forest") ||
                opponentField.getFieldZone().getName().equals("Closed Forest")) closedForest();
        if (field.getFieldZone().getName().equals("Yami") ||
                opponentField.getFieldZone().getName().equals("Yami")) yami();
    }

    private void handleMessengerOfPeace() {
        SpellAndTrapCard myMessengerOfPeace = game.getCurrentPlayer().getField().hasThisCardActivated("Messenger of peace");
        if (myMessengerOfPeace != null) {
            if (DuelView.getInstance().killMessengerOfPeace())
                moveSpellOrTrapToGYFromSpellZone(myMessengerOfPeace);
            else {
                MessengerOfPeace messengerOfPeace = (MessengerOfPeace) myMessengerOfPeace;
                messengerOfPeace.deactivateCards();
                game.getCurrentPlayer().changeLP(-100);
            }
        }
        SpellAndTrapCard opponentMessengerOfPeace = game.getTheOtherPlayer().getField().hasThisCardActivated("Messenger of peace");
        if (opponentMessengerOfPeace != null) {
            MessengerOfPeace messengerOfPeace = (MessengerOfPeace) opponentMessengerOfPeace;
            messengerOfPeace.deactivateCards();
        }
    }

    private void makeChain(Field field) {
        if (field.hasQuickSpellOrTrap())
            ChainView.getInstance();
    }

    private String[] makeSpellZone(ArrayList<SpellAndTrapCard> spellZone) {
        String[] spellZoneString = new String[5];
        for (int i = 0; i < 5; i++) {
            if (spellZone.size() - 1 < i) {
                spellZoneString[i] = "E ";
                continue;
            }
            SpellAndTrapCard spellAndTrapCard = spellZone.get(i);
            if (spellAndTrapCard.isActive()) spellZoneString[i] = "O ";
            else spellZoneString[i] = "H ";
        }
        return spellZoneString;
    }

    private String[] makeMonsterZone(ArrayList<MonsterCard> monsterZone) {
        String[] monsterZoneString = new String[5];
        for (int i = 0; i < 5; i++) {
            if (monsterZone.size() - 1 < i) {
                monsterZoneString[i] = "E ";
                continue;
            }
            MonsterCard monsterCard = monsterZone.get(i);
            if (monsterCard.getMonsterCardModeInField() == MonsterCardModeInField.ATTACK_FACE_UP)
                monsterZoneString[i] = "OO";
            else if (monsterCard.getMonsterCardModeInField() == MonsterCardModeInField.DEFENSE_FACE_UP)
                monsterZoneString[i] = "DO";
            else monsterZoneString[i] = "DH";
        }
        return monsterZoneString;
    }

    private void showGameBoard(Account opponent, Account current) {
        String opponentNickname = opponent.getNickname() + ": " + opponent.getLP();
        int opponentHandCount = opponent.getField().getHand().size();
        int opponentDeckNumber = opponent.getField().getDeckZone().size();
        int opponentGraveYardCount = opponent.getField().getGraveyard().size();
        String opponentFieldZone = "O ";
        if (opponent.getField().getFieldZone() == null) opponentFieldZone = "E ";
        String[] opponentSpellZone = makeSpellZone(opponent.getField().getTrapAndSpell());
        String[] opponentMonsterZone = makeMonsterZone(opponent.getField().getMonsterCards());
        String currentNickname = current.getNickname() + ": " + current.getLP();
        int currentDeckNumber = current.getField().getDeckZone().size();
        int currentGraveYardCount = current.getField().getGraveyard().size();
        int currentHandCount = current.getField().getHand().size();
        String currentFieldZone = "O ";
        if (current.getField().getFieldZone() == null) currentFieldZone = "E ";
        String[] currentSpellZone = makeSpellZone(current.getField().getTrapAndSpell());
        String[] currentMonsterZone = makeMonsterZone(current.getField().getMonsterCards());

        StringBuilder board = new StringBuilder();
        board.append(opponentNickname + "\n");
        for (int i = 0; i < opponentHandCount; i++) board.append("\tc ");
        board.append("\n" + opponentDeckNumber + "\n\t");
        board.append(opponentSpellZone[3] + "\t" + opponentSpellZone[1] + "\t" + opponentSpellZone[0]
                + "\t" + opponentSpellZone[2] + "\t" + opponentSpellZone[4] + "\n\t");
        board.append(opponentMonsterZone[3] + "\t" + opponentMonsterZone[1] + "\t" + opponentMonsterZone[0]
                + "\t" + opponentMonsterZone[2] + "\t" + opponentMonsterZone[4] + "\n");
        board.append(opponentGraveYardCount + "\t\t\t\t\t\t" + opponentFieldZone + "\n");
        board.append("\n----------------------------------------------------------\n\n");
        board.append(currentFieldZone + "\t\t\t\t\t\t" + currentGraveYardCount + "\n\t");
        board.append(currentMonsterZone[4] + "\t" + currentMonsterZone[2] + "\t" + currentMonsterZone[0]
                + "\t" + currentMonsterZone[1] + "\t" + currentMonsterZone[3] + "\n\t");
        board.append(currentSpellZone[4] + "\t" + currentSpellZone[2] + "\t" + currentSpellZone[0]
                + "\t" + currentSpellZone[1] + "\t" + currentSpellZone[3] + "\n");
        board.append("  \t\t\t\t\t\t" + currentDeckNumber + "\n");
        for (int i = 0; i < currentHandCount; i++) board.append("c \t");
        board.append("\n" + currentNickname);

        Output.getInstance().printString(board.toString());
    }


    private void firstMainPhase() {
        Output.getInstance().printPhase("main phase 1");
        showGameBoard(game.getTheOtherPlayer(), game.getCurrentPlayer());
    }

    private void battlePhase() {
        if (!game.getCurrentPlayer().canPlayerAttack()) {
            game.getCurrentPlayer().setCanPlayerAttack(true);
            nextPhase();
        }
        Output.getInstance().printPhase("battle phase");
    }

    private void secondMainPhase() {
        Output.getInstance().printPhase("main phase 2");
        showGameBoard(game.getTheOtherPlayer(), game.getCurrentPlayer());
    }

    private void endPhase() {
        Output.getInstance().printPhase("end phase");
        reset();
        handleSwordOfRevealingLight();
    }

    private void reset() {
        game.getCurrentPlayer().getField().resetAllCards();
        game.getTheOtherPlayer().getField().resetAllCards();
    }

    private void handleSwordOfRevealingLight() {
        SwordsOfRevealingLight sword = (SwordsOfRevealingLight) game.getTheOtherPlayer().getField().hasThisCardActivated("Swords of Revealing Light");
        if (sword != null) {
            sword.counter++;
            if (sword.counter == 3) {
                moveSpellOrTrapToGYFromSpellZone(sword);
                sword.counter = 0;
            }
        }
    }

    public void selectCard(boolean myCard, CardStatusInField cardStatusInField, int number) {
        Account thisPlayer = createThisPlayer(myCard);
        if (!errorForSelecting(thisPlayer, number, cardStatusInField)) return;
        switch (cardStatusInField) {
            case HAND:
                game.setSelectedCard(thisPlayer.getField().getHand().get(number));
                break;
            case MONSTER_FIELD:
                game.setSelectedCard(thisPlayer.getField().getMonsterCards().get(number));
                break;
            case SPELL_FIELD:
                game.setSelectedCard(thisPlayer.getField().getTrapAndSpell().get(number));
                break;
            case FIELD_ZONE:
                game.setSelectedCard(thisPlayer.getField().getFieldZone());
                break;
        }
        Output.getInstance().cardSelected();
    }


    private Account createThisPlayer(boolean myCard) {
        Account thisPlayer;
        if (myCard) thisPlayer = game.getCurrentPlayer();
        else thisPlayer = game.getTheOtherPlayer();
        return thisPlayer;
    }

    private boolean errorForSelecting(Account thisPlayer, int number, CardStatusInField cardStatusInField) {

        if (cardStatusInField.equals(CardStatusInField.HAND) &&
                number > thisPlayer.getField().getHand().size()) {
            Output.getInstance().invalidSelection();
            return false;
        }
        if (cardStatusInField.equals(CardStatusInField.MONSTER_FIELD) &&
                number > thisPlayer.getField().getMonsterCards().size()) {
            Output.getInstance().invalidSelection();
            return false;
        }
        if (cardStatusInField.equals(CardStatusInField.SPELL_FIELD) &&
                number > thisPlayer.getField().getTrapAndSpell().size()) {
            Output.getInstance().invalidSelection();
            return false;
        }
        if (cardStatusInField.equals(CardStatusInField.FIELD_ZONE) &&
                thisPlayer.getField().getFieldZone() == null) {
            Output.getInstance().noCardInPosition();
            return false;
        }
        return true;
    }

    public void deselectCard() {
        if (game.getSelectedCard() == null) {
            Output.getForNow();
            return;
        }
        game.setSelectedCard(null);
        Output.getInstance().cardDeselected();
    }

    public void nextPhase() {
        switch (game.getCurrentPhase()) {
            case DRAW_PHASE:
                game.setCurrentPhase(Phases.STANDBY_PHASE);
                standbyPhase();
                break;
            case STANDBY_PHASE:
                game.setCurrentPhase(Phases.FIRST_MAIN_PHASE);
                firstMainPhase();
                break;
            case FIRST_MAIN_PHASE:
                game.setCurrentPhase(Phases.BATTLE_PHASE);
                battlePhase();
                break;
            case BATTLE_PHASE:
                game.setCurrentPhase(Phases.SECOND_MAIN_PHASE);
                secondMainPhase();
                break;
            case SECOND_MAIN_PHASE:
                game.setCurrentPhase(Phases.END_PHASE);
                endPhase();
                break;
            case END_PHASE:
                game.setCurrentPhase(Phases.DRAW_PHASE);
                game.changeTurn();
                drawPhase();
        }
    }

    public void summon() {
        if (game.getSelectedCard().getName().equals("The Tricky"))
            if (DuelView.getInstance().ordinaryOrSpecial()) {
                theTricky();
                return;
            }
        if (game.getSelectedCard().getName().equals("Gate Guardian")) {
            if (DuelView.getInstance().summonGateGuardian()) {
                gateGuardian();
            }
            return;
        }
        if (game.getSelectedCard().getName().equals("Beast King Barbaros")) {
            int howToSummon = DuelView.getInstance().barbaros();
            if (howToSummon != 1) {
                barbaros(howToSummon);
                return;
            }
        }
        if (!isSummonValid()) return;
        if (solemnWarning((MonsterCard) game.getSelectedCard())) {
            game.setSelectedCard(null);
            return;
        }
        summonWithTribute();
        MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
        if (handleTrapHole(monsterCard)) return;
        game.getCurrentPlayer().getField().getMonsterCards().add(monsterCard);
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        if (monsterCard.getName().equals("Terratiger, the Empowered Warrior"))
            terraTigerMethod();
        game.getSelectedCard().setHasBeenSetOrSummoned(true);
        game.setSelectedCard(null);
        Output.getForNow();
        torrentialTribute();
        game.setHasSummonedInThisTurn(true);
    }


    private boolean handleTrapHole(MonsterCard monsterCard) {
        SpellAndTrapCard trapHoleCard = game.getTheOtherPlayer().getField().hasTrapCard("Trap Hole");
        if (monsterCard.getClassAttackPower() >= 1000 && trapHoleCard != null &&
                DuelView.getInstance().wantsToActivateTrap("Trap Hole")) {
            moveSpellOrTrapToGYFromSpellZone(trapHoleCard);
            addMonsterToGYFromHand(monsterCard);
            game.setSelectedCard(null);
            return true;
        }
        return false;
    }

    private void summonWithTribute() {
        MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
        if (monsterCard.getLevel() == 5 || monsterCard.getLevel() == 6) {
            int number = DuelView.getInstance().getTribute();
            if (isTributeValid(number))
                addMonsterToGYFromMonsterZone(game.getCurrentPlayer().getField().getMonsterCards().get(number));
        } else if (monsterCard.getLevel() == 7 || monsterCard.getLevel() == 8) {
            int firstNumber = DuelView.getInstance().getTribute();
            int secondNumber = DuelView.getInstance().getTribute();
            if (isTributeValid(firstNumber) && isTributeValid(secondNumber) && firstNumber != secondNumber) {
                addMonsterToGYFromMonsterZone(game.getCurrentPlayer().getField().getMonsterCards().get(firstNumber));
                addMonsterToGYFromMonsterZone(game.getCurrentPlayer().getField().getMonsterCards().get(secondNumber));
            }
        }
    }

    private boolean isTributeValid(int number) {
        if (game.getCurrentPlayer().getField().getMonsterCards().size() < number) {
            Output.getForNow();
            return false;
        }
        return true;
    }

    private boolean isSummonValid() {
        if (game.getSelectedCard() == null) {
            Output.getForNow();
            return false;
        } else if (!game.getCurrentPlayer().getField().getHand().contains(game.getSelectedCard())) {
            Output.getForNow();
            return false;
        } else if (!(game.getSelectedCard() instanceof MonsterCard)) {
            Output.getForNow();
            return false;
        }
        if (!(game.getCurrentPhase() == Phases.FIRST_MAIN_PHASE || game.getCurrentPhase() == Phases.SECOND_MAIN_PHASE)) {
            Output.getForNow();
            return false;
        }
        MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
        if (monsterCard.getCardType().equals("Ritual")) {
            Output.getForNow();
            return false;
        } else if (game.getCurrentPlayer().getField().getMonsterCards().size() == 5) {
            Output.getForNow();
            return false;
        } else if (game.isHasSummonedInThisTurn()) {
            Output.getForNow();
            return false;
        } else if ((monsterCard.getLevel() == 5 || monsterCard.getLevel() == 6) &&
                game.getCurrentPlayer().getField().getMonsterCards().isEmpty()) {
            Output.getForNow();
            return false;
        } else if ((monsterCard.getLevel() >= 7) &&
                game.getCurrentPlayer().getField().getMonsterCards().size() < 2) {
            Output.getForNow();
            return false;
        }
        return true;
    }

    public void setMonster() {
        if (!isSettingMonsterValid()) return;
        MonsterCard selectedCard = (MonsterCard) game.getSelectedCard();
        if (selectedCard.getName().equals("Gate Guardian")) {
            Output.getForNow();
            return;
        }
        summonWithTribute();
        game.getCurrentPlayer().getField().getMonsterCards().add(selectedCard);
        selectedCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_DOWN);
        game.getSelectedCard().setHasBeenSetOrSummoned(true);
        game.setSelectedCard(null);
        game.setHasSummonedInThisTurn(true);
        Output.getForNow();
    }

    private boolean isSettingMonsterValid() {
        if (game.getSelectedCard() == null) {
            Output.getForNow();
            return false;
        }
        if (!game.getCurrentPlayer().getField().getHand().contains(game.getSelectedCard())) {
            Output.getForNow();
            return false;
        }
        if (game.getSelectedCard() instanceof MonsterCard) {
            MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
            if (!(game.getCurrentPhase() == Phases.FIRST_MAIN_PHASE || game.getCurrentPhase() == Phases.SECOND_MAIN_PHASE)) {
                Output.getForNow();
                return false;
            }
            if (game.getCurrentPlayer().getField().getMonsterCards().size() == 5) {
                Output.getForNow();
                return false;
            }
            if (game.isHasSummonedInThisTurn()) {
                Output.getForNow();
                return false;
            } else if ((monsterCard.getLevel() == 5 || monsterCard.getLevel() == 6) &&
                    game.getCurrentPlayer().getField().getMonsterCards().isEmpty()) {
                Output.getForNow();
                return false;
            } else if ((monsterCard.getLevel() >= 7) &&
                    game.getCurrentPlayer().getField().getMonsterCards().size() < 2) {
                Output.getForNow();
                return false;
            }
        }
        return true;
    }

    public void setPosition(boolean isAttack) {
        if (!isSetPositionValid(isAttack)) return;
        MonsterCard selectedCard = (MonsterCard) game.getSelectedCard();
        if (isAttack) {
            selectedCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        } else {
            selectedCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        }
        game.setSelectedCard(null);
        Output.getForNow();
    }

    private boolean isSetPositionValid(boolean isAttack) {
        if (checkSelectedCard()) return false;
        if (!(game.getCurrentPhase() == Phases.FIRST_MAIN_PHASE || game.getCurrentPhase() == Phases.SECOND_MAIN_PHASE)) {
            Output.getForNow();
            return false;
        }
        MonsterCard selectedCard = (MonsterCard) game.getSelectedCard();
        if ((isAttack && selectedCard.getMonsterCardModeInField() == MonsterCardModeInField.ATTACK_FACE_UP) ||
                (!isAttack && (selectedCard.getMonsterCardModeInField() == MonsterCardModeInField.DEFENSE_FACE_UP ||
                        selectedCard.getMonsterCardModeInField() == MonsterCardModeInField.DEFENSE_FACE_DOWN))) {
            Output.getForNow();
            return false;
        }
        if (selectedCard.isHasChangedPosition()) {
            Output.getForNow();
            return false;
        }
        if (selectedCard.isHasBeenSetOrSummoned()) {
            Output.getForNow();
            return false;
        }
        return true;
    }

    public void flipSummon() {
        if (!isFlipSummonValid()) return;
        Output.getForNow();
        MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
        if (handleTrapHole(monsterCard)) return;
        game.setSelectedCard(null);
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
    }

    private boolean isFlipSummonValid() {
        if (checkSelectedCard()) return false;
        if (!(game.getCurrentPhase() == Phases.FIRST_MAIN_PHASE || game.getCurrentPhase() == Phases.SECOND_MAIN_PHASE)) {
            Output.getForNow();
            return false;
        }
        MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
        if (!monsterCard.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_DOWN)) {
            Output.getForNow();
            return false;
        }
        return true;
    }

    public void attack(int opponentMonsterPositionNumber) {
        if (!isAttackValid(opponentMonsterPositionNumber)) return;
        if (negateAttack()) {
            nextPhase();
            return;
        }
        MonsterCard attacked = game.getTheOtherPlayer().getField().getMonsterCards().get(opponentMonsterPositionNumber);
        MonsterCard attacker = (MonsterCard) game.getSelectedCard();
        SwordsOfRevealingLight swordsOfRevealingLight = (SwordsOfRevealingLight) game.getTheOtherPlayer().getField().hasThisCardActivated("Swords of Revealing Light");
        if (swordsOfRevealingLight != null) return;
        SpellAndTrapCard magicCylinderCard = game.getTheOtherPlayer().getField().hasTrapCard("Magic Cylinder");
        if (magicCylinderCard != null) {
            magicCylinder(attacker, magicCylinderCard);
            return;
        }
        SpellAndTrapCard mirrorForceCard = game.getTheOtherPlayer().getField().hasTrapCard("Mirror Force");
        if (mirrorForceCard != null) {
            mirrorForce(mirrorForceCard, attacker.getOwner());
            return;
        }
        if (attacked.getName().equals("Suijin") &&
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
        attacker.setHasAttacked(true);
        game.setSelectedCard(null);

    }

    public boolean handleMirageDragon(String name) {
        Account opponent = null;
        if (game.getCurrentPlayer().equals(name)) opponent = game.getTheOtherPlayer();
        else opponent = game.getCurrentPlayer();
        MonsterCard mirageDragon = opponent.hasMirageDragon();
        if (mirageDragon != null && mirageDragon.getMonsterCardModeInField() != MonsterCardModeInField.DEFENSE_FACE_DOWN)
            return true;
        return false;
    }

    private boolean negateAttack() {
        SpellAndTrapCard negateAttackCard = game.getTheOtherPlayer().getField().hasTrapCard("Negate Attack");
        if (negateAttackCard != null && DuelView.getInstance().wantsToActivate("Negate Attack")) {
            moveSpellOrTrapToGYFromSpellZone(negateAttackCard);
            return true;
        }
        return false;
    }

    private void attackInDefense(MonsterCard attacked, MonsterCard attacker) {
        if (attacked.getThisCardDefensePower() < attacker.getThisCardAttackPower()) {
            moveToGraveyardAfterAttack(attacked, attacker);
            Output.getForNow(); //TODO get the card and check the shit
            return;
        }
        if (attacked.getThisCardDefensePower() == attacker.getThisCardAttackPower()) {
            attacked.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
            Output.getForNow();
            return;
        }
        game.getCurrentPlayer().changeLP(attacker.getThisCardAttackPower() - attacked.getThisCardDefensePower());
        attacked.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
        Output.getForNow();
    }

    private void attackInAttack(MonsterCard attacked, MonsterCard attacker) {
        if (attacked.getThisCardAttackPower() < attacker.getThisCardAttackPower()) {
            moveToGraveyardAfterAttack(attacked, attacker);
            Output.getForNow();
            if (!attacked.getName().equals("Exploder Dragon"))
                game.getTheOtherPlayer().changeLP(attacked.getThisCardAttackPower() - attacker.getThisCardAttackPower());
            return;
        }
        if (attacked.getThisCardAttackPower() == attacker.getThisCardAttackPower()) {
            moveToGraveyardAfterAttack(attacker, attacked);
            moveToGraveyardAfterAttack(attacked, attacker);
            Output.getForNow();
            return;
        }
        if (!attacker.getName().equals("Exploder Dragon"))
            game.getCurrentPlayer().changeLP(attacker.getThisCardAttackPower() - attacked.getThisCardAttackPower());
        moveToGraveyardAfterAttack(attacker, attacked);
        Output.getForNow();
    }

    private boolean isAttackValid(int opponentMonsterPositionNumber) {
        if (checkSelectedCard()) return false;
        if (game.getCurrentPhase() != Phases.BATTLE_PHASE) {
            Output.getForNow();
            return false;
        }
        MonsterCard selectedCard = (MonsterCard) game.getSelectedCard();
        if (selectedCard.isHasAttacked()) {
            Output.getForNow();
            return false;
        }
        if (game.getTheOtherPlayer().getField().getMonsterCards().size() < opponentMonsterPositionNumber) {
            Output.getForNow();
            return false;
        }
        if (!selectedCard.canBeRemoved()) {
            Output.getForNow();
            return false;
        }
        if (!selectedCard.canAttack()) {
            Output.getForNow();
            return false;
        }
        if (selectedCard.isHasAttacked()) {
            Output.getForNow();
            return false;
        }
        return true;
    }

    private boolean checkSelectedCard() {
        if (game.getSelectedCard() == null) {
            Output.getForNow();
            return true;
        }
        if (!(game.getSelectedCard() instanceof MonsterCard) &&
                !game.getCurrentPlayer().getField().getMonsterCards().contains(game.getSelectedCard())) {
            Output.getForNow();
            return true;
        }
        return false;
    }

    public void directAttack() {
        if (!isDirectAttackValid()) return;
        MonsterCard selectedCard = (MonsterCard) game.getSelectedCard();
        game.getTheOtherPlayer().changeLP(-(selectedCard.getThisCardAttackPower()));
        selectedCard.setHasAttacked(true);
        Output.getForNow();
        game.setSelectedCard(null);
    }

    private boolean isDirectAttackValid() {
        if (checkSelectedCard()) return false;
        if (game.getCurrentPhase() != Phases.BATTLE_PHASE) {
            Output.getForNow();
            return false;
        }
        MonsterCard selectedCard = (MonsterCard) game.getSelectedCard();
        if (selectedCard.isHasAttacked()) {
            Output.getForNow();
            return false;
        }
        if (!game.getTheOtherPlayer().getField().getMonsterCards().isEmpty()) {
            Output.getForNow();
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
            if (game.getCurrentPlayer().getField().getFieldZone() != null) {
                game.getCurrentPlayer().getField().getGraveyard().add(game.getCurrentPlayer().getField().getFieldZone());
            }
            game.getCurrentPlayer().getField().setFieldZone(selectedCard);
            selectedCard.setActive(true);
        } else {
            if (!game.getCurrentPlayer().getField().getTrapAndSpell().contains(selectedCard))
                game.getCurrentPlayer().getField().getTrapAndSpell().add(selectedCard);
            selectedCard.setActive(true);
        }
        callSpellAndTrapMethod(selectedCard);
        if (selectedCard.isActive()) Output.getForNow();
        game.setSelectedCard(null);
    }

    private void selfAbsorption() {
        if (game.getCurrentPlayer().getField().hasThisCardActivated("Spell Absorption") != null)
            game.getCurrentPlayer().changeLP(500);
        if (game.getTheOtherPlayer().getField().hasThisCardActivated("Spell Absorption") != null)
            game.getTheOtherPlayer().changeLP(500);
    }

    private boolean isActivatingSpellValid() {
        if (game.getSelectedCard() == null) {
            Output.getForNow();
            return false;
        }
        if (!(game.getSelectedCard() instanceof SpellAndTrapCard)) {
            Output.getForNow();
            return false;
        }
        SpellAndTrapCard selectedCard = (SpellAndTrapCard) game.getSelectedCard();
        if (!selectedCard.isSpell()) {
            Output.getForNow();
            return false;
        }
        if (game.getCurrentPhase() != Phases.FIRST_MAIN_PHASE && game.getCurrentPhase() != Phases.SECOND_MAIN_PHASE) {
            Output.getForNow();
            return false;
        }
        if (selectedCard.isActive()) {
            Output.getForNow();
            return false;
        }
        if (game.getCurrentPlayer().getField().getHand().contains(selectedCard) && !selectedCard.getProperty().equals("Field") &&
                game.getCurrentPlayer().getField().getTrapAndSpell().size() == 5) {
            Output.getForNow();
            return false;
        }
        return true;
    }

    private void callSpellAndTrapMethod(SpellAndTrapCard card) {
        switch (card.getName()) {
            case "Monster Reborn":
                monsterReborn(card);
                break;
            case "Terraforming":
                terraforming(card);
                break;
            case "Pot of Greed":
                potOfGreed(card);
                break;
            case "Raigeki":
                raigeki(card);
                break;
            case "Change of Heart":
                changeOfHeart((ChangeOfHeart) card);
                break;
            case "Harpie's Feather Duster":
                harpiesFeatherDuster(card);
                break;
            case "Dark Hole":
                darkHole(card);
                break;
            case "Messenger of peace":
                ((MessengerOfPeace) card).deactivateCards();
                break;
            case "Mystical space typhoon":
                mysticalSpaceTyphoon(card);
                break;
            case "Yami":
                yami();
                break;
            case "Forest":
                forest();
                break;
            case "Closed Forest":
                closedForest();
                break;
            case "Umiiruka":
                umiiruka();
                break;
            case "Sword of dark destruction":
            case "Black Pendant":
            case "United We Stand":
            case "Magnum Shield":
                equipMonster(card);
                break;
            case "Advanced Ritual Art":
                advancedRitualArt(card);
                break;
            case "Twin Twisters":
                twinTwisters(card);
                break;
            case "Swords of Revealing Light":
                ((SwordsOfRevealingLight) card).specialMethod(game.getTheOtherPlayer());
                break;
        }
    }

    private void moveSpellOrTrapToGYFromSpellZone(SpellAndTrapCard spellAndTrapCard) {
        spellAndTrapCard.reset();
        game.getCurrentPlayer().getField().getTrapAndSpell().remove(spellAndTrapCard);
        game.getCurrentPlayer().getField().getGraveyard().add(spellAndTrapCard);
    }

    private void moveSpellOrTrapToGYFromFieldZone(SpellAndTrapCard spellAndTrapCard) {
        spellAndTrapCard.reset();
        game.getCurrentPlayer().getField().setFieldZone(null);
        game.getCurrentPlayer().getField().getGraveyard().add(spellAndTrapCard);
    }

    public void callOfTheHaunted(SpellAndTrapCard callOfTheHauntedCard, Field field, boolean isCurrentPlayer) {
        if (DuelView.getInstance().wantsToActivate("Call Of The Haunted")) {
            moveSpellOrTrapToGYFromSpellZone(callOfTheHauntedCard);
            Card card = null;
            if (isCurrentPlayer) card = DuelView.getInstance().getCardFromMyGY();
            else card = DuelView.getInstance().getCardFromOpponentGY();
            if (card == null) return;
            field.getGraveyard().remove(card);
            if (card instanceof MonsterCard) field.getMonsterCards().add((MonsterCard) card);
            else if (((SpellAndTrapCard) card).getProperty().equals("Field")) {
                SpellAndTrapCard fieldCard = field.getFieldZone();
                if (fieldCard != null) moveSpellOrTrapToGYFromFieldZone(fieldCard);
                field.setFieldZone((SpellAndTrapCard) card);
            } else field.getTrapAndSpell().add((SpellAndTrapCard) card);
        }
    }

    public boolean solemnWarning(MonsterCard monsterCard) {
        SpellAndTrapCard solemnWarning = game.getTheOtherPlayer().getField().hasTrapCard("Solemn Warning");
        if (solemnWarning != null && DuelView.getInstance().wantsToActivateTrap("Solemn Warning")) {
            moveSpellOrTrapToGYFromSpellZone(solemnWarning);
            game.getTheOtherPlayer().changeLP(-2000);
            addMonsterToGYFromHand(monsterCard);
            return true;
        }
        return false;
    }

    public boolean magicJamamer(SpellAndTrapCard spellAndTrapCard) {
        SpellAndTrapCard magicJamamer = game.getTheOtherPlayer().getField().hasTrapCard("Magic Jamamer");
        if (magicJamamer != null && DuelView.getInstance().wantsToActivateTrap("Magic Jamamer")) {
            moveSpellOrTrapToGYFromSpellZone(magicJamamer);
            Card toRemove = DuelView.getInstance().getCardFromHand();
            if (toRemove != null) return false;
            game.getTheOtherPlayer().getField().getHand().remove(toRemove);
            game.getTheOtherPlayer().getField().getGraveyard().add(toRemove);
            game.setSelectedCard(null);
            return true;
        }
        return false;
    }

    public void timeSeal(SpellAndTrapCard timeSealCard, Account opponent) {
        if (DuelView.getInstance().wantsToActivate("Time Seal")) {
            moveSpellOrTrapToGYFromSpellZone(timeSealCard);
            opponent.setCanDraw(false);
        }
    }

    public void torrentialTribute() {
        SpellAndTrapCard torrentialTributeCard = game.getTheOtherPlayer().getField().hasTrapCard("Torrential Tribute");
        if (torrentialTributeCard != null && DuelView.getInstance().wantsToActivateTrap("Torrential Tribute")) {
            moveSpellOrTrapToGYFromSpellZone(torrentialTributeCard);
            ArrayList<MonsterCard> myMonsterCards = game.getCurrentPlayer().getField().getMonsterCards();
            ArrayList<MonsterCard> opponentsMonsterCards = game.getTheOtherPlayer().getField().getMonsterCards();
            int mySize = myMonsterCards.size();
            int opponentSize = opponentsMonsterCards.size();
            int max = Math.max(mySize, opponentSize);
            for (int i = 0; i < max; i++) {
                if (myMonsterCards.get(i) != null) {
                    addMonsterToGYFromMonsterZone(myMonsterCards.get(i));
                }
                if (opponentsMonsterCards.get(i) != null) {
                    addMonsterToGYFromMonsterZone(opponentsMonsterCards.get(i));
                }
            }
        }
    }


    public void mindCrush(SpellAndTrapCard spellAndTrapCard, Account opponent) {
        if (DuelView.getInstance().wantsToActivateTrap("Mind Crush")) {
            String cardName = DuelView.getInstance().getCardName();
            if (!opponent.hasCardInHand(cardName)) randomlyRemoveFromHand(spellAndTrapCard.getOwner());
            else {
                ArrayList<Card> hand = opponent.getField().getHand();
                ArrayList<Card> toRemove = new ArrayList<>();
                for (Card card : hand) if (card.getName().equals(cardName)) toRemove.add(card);
                hand.removeAll(toRemove);
            }
            moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
        }
    }

    private void randomlyRemoveFromHand(Account account) {
        Random rand = new Random();
        int randomNumber = rand.nextInt(account.getField().getHand().size());
        account.getField().getHand().remove(randomNumber);
    }

    private void equipMonster(SpellAndTrapCard equipSpell) {
        MonsterCard monsterToEquip = DuelView.getInstance().getMonsterToEquip();
        if (monsterToEquip == null) {
            equipSpell.setActive(false);
            return;
        }
        switch (equipSpell.getName()) {
            case "Sword of dark destruction":
                ((SwordOfDarkDestruction) equipSpell).setEquippedMonster(monsterToEquip);
                break;
            case "Black Pendant":
                ((BlackPendant) equipSpell).setEquippedMonster(monsterToEquip);
                break;
            case "United We Stand":
                ((UnitedWeStand) equipSpell).setEquippedMonster(monsterToEquip);
                break;
            case "Magnum Shield":
                ((MagnumShield) equipSpell).setEquippedMonster(monsterToEquip);
        }
    }


    public void magicCylinder(MonsterCard attacker, SpellAndTrapCard magicCylinder) {
        if (!DuelView.getInstance().wantsToActivateTrap("Magic Cylinder")) return;
        decreaseLPWithTrap(attacker.getOwner(), attacker.getClassAttackPower());
        moveSpellOrTrapToGYFromSpellZone(magicCylinder);
    }

    public void mirrorForce(SpellAndTrapCard mirrorForce, Account opponent) {
        ArrayList<MonsterCard> monsters = new ArrayList<>(opponent.getField().getMonsterCards());
        for (MonsterCard monster : monsters)
            if (monster.getMonsterCardModeInField() == MonsterCardModeInField.ATTACK_FACE_UP)
                addMonsterToGYFromMonsterZone(monster);
        moveSpellOrTrapToGYFromSpellZone(mirrorForce);
    }


    private void decreaseLPWithTrap(Account account, int amount) {
        if (account.getField().hasThisCardActivated("Ring of defense") == null)
            account.changeLP(-amount);
    }

    private void advancedRitualArt(SpellAndTrapCard spellAndTrapCard) {
        ritualSummon();
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    private void twinTwisters(SpellAndTrapCard spellAndTrapCard) {
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
        Card removeFromHand = DuelView.getInstance().getCardFromHand();
        if (removeFromHand == null) return;
        game.getCurrentPlayer().getField().getHand().remove(removeFromHand);
        game.getCurrentPlayer().getField().getGraveyard().add(removeFromHand);

        int numOfSpellCardsToDestroy = DuelView.getInstance().numOfSpellCardsToDestroy();
        for (int i = 0; i < numOfSpellCardsToDestroy; i++) {
            SpellAndTrapCard toDestroy = null;
            if (DuelView.getInstance().isMine()) toDestroy = DuelView.getInstance().getFromMyField();
            else toDestroy = DuelView.getInstance().getFromOpponentField();
            if (toDestroy == null) continue;
            moveSpellOrTrapToGYFromSpellZone(toDestroy);
        }
    }

    private void umiiruka() {
        ArrayList<MonsterCard> cards = getAllMonsterCards();
        for (MonsterCard monsterCard : cards)
            if (monsterCard.getMonsterType().equals("Aqua")) {
                monsterCard.setThisCardAttackPower(monsterCard.getThisCardAttackPower() + 500);
                monsterCard.setThisCardDefensePower(monsterCard.getThisCardDefensePower() - 400);
            }
    }

    private void closedForest() {
        int amount = game.getCurrentPlayer().getField().getGraveyard().size() * 100;
        for (MonsterCard monsterCard : game.getCurrentPlayer().getField().getMonsterCards())
            if (monsterCard.getMonsterType().equals("Beast") ||
                    monsterCard.getMonsterType().equals("Beast-Warrior"))
                monsterCard.setThisCardAttackPower(monsterCard.getThisCardAttackPower() + amount);
    }

    private void forest() {
        ArrayList<MonsterCard> cards = getAllMonsterCards();
        for (MonsterCard monsterCard : cards)
            if (monsterCard.getMonsterType().equals("Insect") ||
                    monsterCard.getMonsterType().equals("Beast") || monsterCard.getMonsterType().equals("Beast-Warrior")) {
                monsterCard.setThisCardAttackPower(monsterCard.getThisCardAttackPower() + 200);
                monsterCard.setThisCardDefensePower(monsterCard.getThisCardDefensePower() + 200);
            }
    }

    private void yami() {
        ArrayList<MonsterCard> cards = getAllMonsterCards();
        for (MonsterCard monsterCard : cards) {
            if (monsterCard.getMonsterType().equals("Fiend") ||
                    monsterCard.getMonsterType().equals("Spellcaster")) {
                monsterCard.setThisCardAttackPower(monsterCard.getThisCardAttackPower() + 200);
                monsterCard.setThisCardDefensePower(monsterCard.getThisCardDefensePower() + 200);
            }
            if (monsterCard.getMonsterType().equals("Fairy")) {
                monsterCard.setThisCardAttackPower(monsterCard.getThisCardAttackPower() - 200);
                monsterCard.setThisCardDefensePower(monsterCard.getThisCardDefensePower() - 200);
            }
        }
    }

    private ArrayList<MonsterCard> getAllMonsterCards() {
        ArrayList<MonsterCard> cards = new ArrayList<>();
        cards.addAll(game.getCurrentPlayer().getField().getMonsterCards());
        cards.addAll(game.getTheOtherPlayer().getField().getMonsterCards());
        return cards;
    }

    public void mysticalSpaceTyphoon(SpellAndTrapCard spellAndTrapCard) {
        SpellAndTrapCard toDestroy = null;
        if (DuelView.getInstance().isMine())
            toDestroy = DuelView.getInstance().getFromMyField();
        else toDestroy = DuelView.getInstance().getFromOpponentField();
        if (toDestroy != null) {
            moveSpellOrTrapToGYFromSpellZone(toDestroy);
        }
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    private void darkHole(SpellAndTrapCard spellAndTrapCard) {
        int myMonstersSize = game.getCurrentPlayer().getField().getMonsterCards().size();
        int opponentSize = game.getTheOtherPlayer().getField().getMonsterCards().size();
        for (int i = 0; i < myMonstersSize; i++)
            addMonsterToGYFromMonsterZone(game.getCurrentPlayer().getField().getMonsterCards().get(0));
        for (int i = 0; i < opponentSize; i++)
            addMonsterToGYFromMonsterZone(game.getTheOtherPlayer().getField().getMonsterCards().get(0));
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    private void harpiesFeatherDuster(SpellAndTrapCard spellAndTrapCard) {
        int size = game.getTheOtherPlayer().getField().getTrapAndSpell().size();
        for (int i = 0; i < size; i++)
            moveSpellOrTrapToGYFromSpellZone(game.getTheOtherPlayer().getField().getTrapAndSpell().get(0));
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);

    }

    private void changeOfHeart(ChangeOfHeart changeOfHeart) {
        MonsterCard toHijack = DuelView.getInstance().getHijackedCard();
        if (toHijack != null) changeOfHeart.setHijackedCard(toHijack);
    }

    private void raigeki(SpellAndTrapCard spellAndTrapCard) {
        int size = game.getTheOtherPlayer().getField().getMonsterCards().size();
        for (int i = 0; i < size; i++)
            addMonsterToGYFromMonsterZone(game.getTheOtherPlayer().getField().getMonsterCards().get(0));
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    private void potOfGreed(SpellAndTrapCard spellAndTrapCard) {
        if (game.getCurrentPlayer().getField().getHand().size() <= 4)
            if (game.getCurrentPlayer().getField().getDeckZone().size() >= 2) {
                game.getCurrentPlayer().getField().getHand().add(game.getCurrentPlayer().getField().getDeckZone().get(0));
                game.getCurrentPlayer().getField().getDeckZone().remove(0);
                game.getCurrentPlayer().getField().getHand().add(game.getCurrentPlayer().getField().getDeckZone().get(0));
                game.getCurrentPlayer().getField().getDeckZone().remove(0);
            }
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    private void terraforming(SpellAndTrapCard spellAndTrapCard) {
        SpellAndTrapCard fieldSpell = DuelView.getInstance().getFieldSpellFromDeck();
        if (fieldSpell != null)
            if (game.getCurrentPlayer().getField().getHand().size() != 6)
                game.getCurrentPlayer().getField().getHand().add(fieldSpell);
        moveSpellOrTrapToGYFromSpellZone(spellAndTrapCard);
    }

    private void monsterReborn(SpellAndTrapCard spellAndTrapCard) {
        MonsterCard monsterCard = null;
        ArrayList<Card> removeFrom = null;
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
        game.getCurrentPlayer().getField().getTrapAndSpell().add((SpellAndTrapCard) game.getSelectedCard());
        game.getSelectedCard().setHasBeenSetOrSummoned(true);
        game.setSelectedCard(null);
        Output.getForNow();
    }

    private boolean isSettingSpellAndTrapValid() {
        if (game.getSelectedCard() == null) {
            Output.getForNow();
            return false;
        }
        if (!game.getCurrentPlayer().getField().getHand().contains(game.getSelectedCard())) {
            Output.getForNow();
            return false;
        }
        if (game.getCurrentPhase() != Phases.FIRST_MAIN_PHASE && game.getCurrentPhase() != Phases.SECOND_MAIN_PHASE) {
            Output.getForNow();
            return false;
        }
        if (game.getCurrentPlayer().getField().getTrapAndSpell().size() == 5) {
            Output.getForNow();
            return false;
        }
        return true;
    }

    public void set() {
        if (game.getSelectedCard() instanceof MonsterCard) setMonster();
        else setSpellAndTrap();
    }

    public void ritualSummon() {
        if (!errorForRitualSummon()) return;
        MonsterCard ritualMonster = DuelView.getInstance().getRitualCard();
        while (!errorAfterChoosingRitualCard(ritualMonster)) {
            ritualMonster = DuelView.getInstance().getRitualCard();
        }
        ArrayList<MonsterCard> toTribute = DuelView.getInstance().getTributes();
        while (!isSumOfTributesValid(toTribute, ritualMonster)) {
            toTribute = DuelView.getInstance().getTributes();
        }
        chooseMonsterMode(ritualMonster);
        game.getCurrentPlayer().getField().getMonsterCards().add(ritualMonster);
        for (MonsterCard monsterCard : toTribute) {
            addMonsterToGYFromMonsterZone(monsterCard);
        }
        game.getCurrentPlayer().getField().getGraveyard().add(game.getSelectedCard());
        SpellAndTrapCard thisSpell = (SpellAndTrapCard) game.getSelectedCard();
        game.getCurrentPlayer().getField().getTrapAndSpell().remove(thisSpell);
        Output.getForNow();
    }

    private void chooseMonsterMode(MonsterCard monsterCard) {
        String mode = DuelView.getInstance().monsterMode().toLowerCase();
        if (mode.equals("attack"))
            monsterCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        else monsterCard.setMonsterCardModeInField(MonsterCardModeInField.DEFENSE_FACE_UP);
    }

    private boolean isSumOfTributesValid(ArrayList<MonsterCard> toTribute, MonsterCard ritualMonster) {
        int sum = 0;
        for (MonsterCard monsterCard : toTribute)
            sum += monsterCard.getLevel();
        return sum == ritualMonster.getLevel();
    }

    private boolean errorAfterChoosingRitualCard(MonsterCard ritualMonster) {
        if (!ritualMonster.getCardType().equals("Ritual")) {
            Output.getForNow();
            return false;
        }
        if (!game.getCurrentPlayer().getField().isTributesLevelSumValid(ritualMonster.getLevel(),
                game.getCurrentPlayer().getField().getMonsterCards().size())) {
            Output.getForNow();
            return false;
        }
        return true;
    }

    private boolean errorForRitualSummon() {
        if (game.getCurrentPlayer().getField().ritualMonsterCards().isEmpty()) {
            Output.getForNow();
            return false;
        }
        if (!checkSum()) {
            Output.getForNow();
            return false;
        }
        return true;
    }

    private boolean checkSum() {
        for (MonsterCard monsterCard : game.getCurrentPlayer().getField().ritualMonsterCards()) {
            if (game.getCurrentPlayer().getField().isTributesLevelSumValid(monsterCard.getLevel(),
                    game.getCurrentPlayer().getField().getMonsterCards().size()))
                return true;
        }
        return false;
    }

    private void specialSummon(MonsterCard monsterCard, MonsterCardModeInField monsterCardModeInField,
                               ArrayList<Card> removeFrom) {
        removeFrom.remove(monsterCard);
        if (solemnWarning(monsterCard)) return;
        game.getCurrentPlayer().getField().getMonsterCards().add(monsterCard);
        monsterCard.setMonsterCardModeInField(monsterCardModeInField);
    }

    public void showGraveyard() {
        String toPrint = game.getCurrentPlayer().getField().showGraveyard();
        Output.getForNow();
    }

    public void showSelectedCard() {
        if (!errorsForShowingSelectedCard()) return;
        Output.getForNow(); //toString
    }

    private boolean errorsForShowingSelectedCard() {
        if (game.getSelectedCard() == null) {
            Output.getForNow();
            return false;
        }
        if (game.getSelectedCard().getOwner().equals(game.getTheOtherPlayer()))
            if (game.getSelectedCard() instanceof MonsterCard) {
                MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
                if (monsterCard.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_DOWN)) {
                    Output.getForNow();
                    return false;
                }
            } else {
                SpellAndTrapCard spellAndTrapCard = (SpellAndTrapCard) game.getSelectedCard();
                if (!spellAndTrapCard.isActive()) {
                    Output.getForNow();
                    return false;
                }
            }
        return true;
    }

    public void cheatShowRivalHand() {
        String rivalHand = "";
        for (Card card : game.getTheOtherPlayer().getField().getHand())
            rivalHand += card.getName() + "\n";
        rivalHand = rivalHand.substring(0, rivalHand.length() - 2);
        Output.getForNow();
    }

    public void cheatIncreaseLP(int amount) {
        game.getCurrentPlayer().changeLP(amount);
    }

    public void cheatSetWinner() {
        game.finishGame(game.getTheOtherPlayer());
    }

    public void cheatSeeMyDeck() {
        String toPrint = "";
        for (Card card : game.getCurrentPlayer().getField().getDeckZone())
            toPrint += card.getName() + "\n";
        toPrint = toPrint.substring(0, toPrint.length() - 2);
        Output.getForNow();
    }

    public void cheatDecreaseLP(int amount) {
        game.getTheOtherPlayer().changeLP(-amount);
    }

    public void surrender() {
        game.finishGame(game.getCurrentPlayer());
    }

    private void moveToGraveyardAfterAttack(MonsterCard toBeRemoved, MonsterCard remover) {
        addMonsterToGYFromMonsterZone(toBeRemoved);
        if (toBeRemoved.getName().equals("Exploder Dragon") || toBeRemoved.getName().equals("Yomi Ship")) {
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

    private void handleSupplySquad(MonsterCard toBeRemoved, Field field) {
        if (!field.getDeckZone().isEmpty() &&
                (field.getHand().size() != 6)) {
            SpellAndTrapCard supplySquad = field.hasThisCardActivated("Supply Squad");
            if (supplySquad != null && !supplySquad.isHasBeenUsedInThisTurn()) {
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

    public MonsterCard forScanner() {
        return null;
    }

    private void heraldOfCreation() {
        MonsterCard monsterCard = DuelView.getInstance().getFromMyGY();
        if (monsterCard.getLevel() < 7) {
            Output.getForNow();
            return;
        }
        MonsterCard monsterCardToRemove = DuelView.getInstance().getMonsterCardFromHand();
        addMonsterToGYFromHand(monsterCardToRemove);
        game.getCurrentPlayer().getField().getHand().add(monsterCard);
    }


    private void terraTigerMethod() {
        if (!DuelView.getInstance().wantsToActivate("Terratiger, the Empowered Warrior")) return;
        if (!errorForTerraTiger()) return;
        MonsterCard monsterCard = DuelView.getInstance().getMonsterCardFromHand();
        while (!game.getCurrentPlayer().getField().ordinaryLowLevelCards().contains(monsterCard)) {
            monsterCard = DuelView.getInstance().getMonsterCardFromHand();
        }
        specialSummon(monsterCard, MonsterCardModeInField.DEFENSE_FACE_DOWN, game.getCurrentPlayer().getField().getHand());
        game.setSelectedCard(null);
        game.setHasSummonedInThisTurn(true);
    }

    private boolean errorForTerraTiger() {
        if (game.getCurrentPlayer().getField().getMonsterCards().size() == 5) {
            Output.getForNow();
            return false;
        }
        if (game.getCurrentPlayer().getField().ordinaryLowLevelCards().isEmpty()) {
            Output.getForNow();
            return false;
        }
        return true;
    }

    private void theTricky() {
        MonsterCard monsterCard = DuelView.getInstance().getMonsterCardFromHand();
        game.getCurrentPlayer().getField().getHand().remove(monsterCard);
        game.getCurrentPlayer().getField().getGraveyard().add(monsterCard);
        specialSummon((MonsterCard) game.getSelectedCard(), MonsterCardModeInField.ATTACK_FACE_UP, game.getCurrentPlayer().getField().getHand());
        game.setSelectedCard(null);
    }

    private void gateGuardian() {
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

    private void barbaros(int howToSummon) {
        MonsterCard barbaros = (MonsterCard) game.getSelectedCard();
        if (howToSummon == 2) {
            barbaros.setThisCardAttackPower(1900);
            barbaros.setClassAttackPower(1900);
        } else {
            ArrayList<MonsterCard> opponentMonsterCards = game.getTheOtherPlayer().getField().getMonsterCards();
            ArrayList<SpellAndTrapCard> opponentSpellCards = game.getTheOtherPlayer().getField().getTrapAndSpell();
            int max = Math.max(opponentMonsterCards.size(), opponentSpellCards.size());
            tributeThreeCards();
            if (game.getTheOtherPlayer().getField().getFieldZone() != null)
                moveSpellOrTrapToGYFromFieldZone(game.getTheOtherPlayer().getField().getFieldZone());
            for (int i = 0; i < max; i++) {
                if (opponentMonsterCards.get(i) != null)
                    opponentMonsterCards.remove(i);
                if (opponentSpellCards.get(i) != null)
                    opponentSpellCards.remove(i);
            }
        }
        game.getCurrentPlayer().getField().getHand().remove(barbaros);
        game.getCurrentPlayer().getField().getMonsterCards().add(barbaros);
        barbaros.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        game.setSelectedCard(null);
        game.setHasSummonedInThisTurn(true);
    }

    public void texChanger(MonsterCard attacked) {
        if (!attacked.isHasBeenUsedInThisTurn()) {
            ArrayList<Card> toRemoveFrom = new ArrayList<>();
            int whereFrom = DuelView.getInstance().whereToSummonFrom();
            MonsterCard monsterCard = null;
            do {
                switch (whereFrom) {
                    case 1:
                        monsterCard = DuelView.getInstance().getMonsterCardFromHand();
                        toRemoveFrom = game.getCurrentPlayer().getField().getHand();
                        break;
                    case 2:
                        monsterCard = DuelView.getInstance().getFromMyDeck();
                        toRemoveFrom = game.getCurrentPlayer().getField().getDeckZone();
                        break;
                    case 3:
                        monsterCard = DuelView.getInstance().getFromMyGY();
                        toRemoveFrom = game.getCurrentPlayer().getField().getGraveyard();
                }
                if (monsterCard == null) return;
            }
            while (!(monsterCard.getCardType().equals("Normal") &&
                    monsterCard.getMonsterType().equals("Cyberse")));

            specialSummon(monsterCard, MonsterCardModeInField.ATTACK_FACE_UP, toRemoveFrom);
            attacked.setHasBeenUsedInThisTurn(true);
        }
    }
}
