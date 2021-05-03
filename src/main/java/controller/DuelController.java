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

    public void run() {

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
        if (game.getCurrentPlayer().getUsername().equals(username)) return;
        game.changeTurn();
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    private void drawPhase() {
        if (!game.getCurrentPlayer().canDraw())
            return;
        Field field = game.getCurrentPlayer().getField();
        field.getHand().add(field.getDeckZone().get(0));
        makeChain(field);
    }

    private void standbyPhase() {
        Field field = game.getCurrentPlayer().getField();
        Field opponentField = game.getTheOtherPlayer().getField();
        handleField(field, opponentField);
        handleEquip(field);
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
        handleMessengerOfPeace();
        makeChain(field);
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
                moveSpellOrTrapToGY(myMessengerOfPeace);
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

    private void firstMainPhase() {

    }

    private void battlePhase() {

    }

    private void secondMainPhase() {

    }

    private void endPhase() {
        //TODO reset shit
        SwordsOfRevealingLight sword = (SwordsOfRevealingLight) game.getTheOtherPlayer().getField().hasThisCardActivated("Swords of Revealing Light");
        if (sword != null) {
            sword.counter++;
            if (sword.counter == 3) {
                moveSpellOrTrapToGY(sword);
                sword.counter = 0;
            }
        }
    }

    public void selectCard(boolean myCard, CardStatusInField cardStatusInField, int number) {
        int convertedNumber = getConvertedNumber(myCard, cardStatusInField, number);
        Account thisPlayer = createThisPlayer(myCard);
        if (!errorForSelecting(thisPlayer, number, convertedNumber, cardStatusInField)) return;
        switch (cardStatusInField) {
            case HAND:
                game.setSelectedCard(thisPlayer.getField().getHand().get(convertedNumber));
                break;
            case MONSTER_FIELD:
                game.setSelectedCard(thisPlayer.getField().getMonsterCards().get(convertedNumber));
                break;
            case SPELL_FIELD:
                game.setSelectedCard(thisPlayer.getField().getTrapAndSpell().get(convertedNumber));
                break;
            case FIELD_ZONE:
                game.setSelectedCard(thisPlayer.getField().getFieldZone());
                break;
        }
        Output.getForNow();
    }

    private int getConvertedNumber(boolean myCard, CardStatusInField cardStatusInField, int number) {
        int convertedNumber;
        if (cardStatusInField.equals(CardStatusInField.HAND)) convertedNumber = number;
        else {
            if (myCard) convertedNumber = handleMyCardNumber(number);
            else convertedNumber = handleOpponentsCardNumber(number);
        }
        return convertedNumber;
    }

    private Account createThisPlayer(boolean myCard) {
        Account thisPlayer;
        if (myCard) thisPlayer = game.getCurrentPlayer();
        else thisPlayer = game.getTheOtherPlayer();
        return thisPlayer;
    }

    private boolean errorForSelecting(Account thisPlayer, int number, int convertedNumber, CardStatusInField cardStatusInField) {

        if (cardStatusInField.equals(CardStatusInField.HAND) &&
                number > thisPlayer.getField().getHand().size()) {
            Output.getForNow();
            return false;
        } else if (cardStatusInField.equals(CardStatusInField.MONSTER_FIELD) &&
                convertedNumber + 1 > thisPlayer.getField().getMonsterCards().size()) {
            Output.getForNow();
            return false;
        } else if (cardStatusInField.equals(CardStatusInField.SPELL_FIELD) &&
                convertedNumber + 1 > thisPlayer.getField().getTrapAndSpell().size()) {
            Output.getForNow();
            return false;
        } else if (cardStatusInField.equals(CardStatusInField.FIELD_ZONE) &&
                thisPlayer.getField().getFieldZone() == null) {
            Output.getForNow();
            return false;
        }
        return true;
    }

    private int handleMyCardNumber(int number) {
        if (number == 1) return 2;
        if (number == 2) return 3;
        if (number == 3) return 1;
        if (number == 4) return 4;
        return 0;
    }

    private int handleOpponentsCardNumber(int number) {
        if (number == 1) return 2;
        if (number == 2) return 1;
        if (number == 3) return 3;
        if (number == 4) return 0;
        return 4;
    }


    private void deselectCard() {
        if (game.getSelectedCard() == null) {
            Output.getForNow();
            return;
        }
        game.setSelectedCard(null);
    }

    private void nextPhase() {

    }

    public void summon() {
        //TODO چک کن کارتایی که وضعشون خاصه ritual
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
        summonWithTribute();
        MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
        if (handleTrapHole(monsterCard)) return;
        game.getCurrentPlayer().getField().getMonsterCards().add(monsterCard);
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        if (monsterCard.getName().equals("Terratiger, the Empowered Warrior"))
            terraTigerMethod();
        game.getSelectedCard().setHasBeenSetOrSummoned(true);
        torrentialTribute();
        game.setSelectedCard(null);
        Output.getForNow();
        game.setHasSummonedInThisTurn(true);
    }

    private boolean handleTrapHole(MonsterCard monsterCard) {
        SpellAndTrapCard trapHoleCard = game.getTheOtherPlayer().getField().hasTrapCard("Trap Hole");
        if (monsterCard.getClassAttackPower() >= 1000 && trapHoleCard != null &&
                DuelView.getInstance().wantsToActivate("Trap Hole")) {
            trapHole(trapHoleCard, monsterCard);
            game.setSelectedCard(null);
            return true;
        }
        return false;
    }

    private void summonWithTribute() {
        MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
        if (monsterCard.getLevel() == 5 || monsterCard.getLevel() == 6) {
            int number = DuelView.getInstance().getTribute();
            int convertedNumber = handleMyCardNumber(number);
            if (isTributeValid(convertedNumber)) {
                moveOneCardToGY(convertedNumber);
            }
        } else if (monsterCard.getLevel() == 7 || monsterCard.getLevel() == 8) {
            int firstNumber = DuelView.getInstance().getTribute();
            int secondNumber = DuelView.getInstance().getTribute();
            int firstConvertedNumber = handleMyCardNumber(firstNumber);
            int secondConvertedNumber = handleMyCardNumber(secondNumber);
            if (isTributeValid(firstConvertedNumber) && isTributeValid(secondConvertedNumber)) {
                moveToGraveYard(firstConvertedNumber, secondConvertedNumber);
            }
        }
    }

    private void moveOneCardToGY(int convertedNumber) {
        MonsterCard cardToRemove = game.getCurrentPlayer().getField().getMonsterCards().get(convertedNumber);
        addMonsterToGY(cardToRemove);
    }

    private void moveToGraveYard(int firstConvertedNumber, int secondConvertedNumber) {
        MonsterCard firstCard = game.getCurrentPlayer().getField().getMonsterCards().get(firstConvertedNumber);
        MonsterCard secondCard = game.getCurrentPlayer().getField().getMonsterCards().get(secondConvertedNumber);
        addMonsterToGY(firstCard);
        addMonsterToGY(secondCard);
    }

    private boolean isTributeValid(int number) {
        if (game.getCurrentPlayer().getField().getMonsterCards().size() < number + 1) {
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

    private void setPosition(boolean isAttack) {
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

    private void flipSummon() {
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
        if (negateAttack()) return;
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

    private boolean negateAttack() {
        SpellAndTrapCard negateAttackCard = game.getTheOtherPlayer().getField().hasTrapCard("Negate Attack");
        if (negateAttackCard != null && DuelView.getInstance().wantsToActivate("Negate Attack")) {
            moveSpellOrTrapToGY(negateAttackCard);
            game.changeTurn(); //TODO مرحله نبرد رو خاتمه دادن چه سمیه؟
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

    private void directAttack() {
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
        } //TODO check for a spell that prevents this
        return true;
    }

    public void activateSpell() {
        if (!isActivatingSpellValid()) return;
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
        //TODO MIRAGE DRAGON
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
            case "Mind Crush":
                mindCrush(card);
                break;
            case "Call of The Haunted":
                callOfTheHaunted();
                break;

        }
    }

    private void moveSpellOrTrapToGY(SpellAndTrapCard spellAndTrapCard) {
        spellAndTrapCard.reset();
        game.getCurrentPlayer().getField().getTrapAndSpell().remove(spellAndTrapCard);
        game.getCurrentPlayer().getField().getGraveyard().add(spellAndTrapCard);
    }

    public void callOfTheHaunted() {
        Field field = game.getCurrentPlayer().getField();
        SpellAndTrapCard callOfTheHauntedCard = field.hasTrapCard("Call of The Haunted");
        if (callOfTheHauntedCard != null && DuelView.getInstance().wantsToActivate("Call Of The Haunted")) {
            Card card = DuelView.getInstance().getFromMyGY();
            field.getGraveyard().remove(card);
            if (card instanceof MonsterCard) field.getMonsterCards().add((MonsterCard) card);
            else if (((SpellAndTrapCard) card).getProperty().equals("Field")) {
                SpellAndTrapCard fieldCard = field.getFieldZone();
                if (fieldCard != null) moveSpellOrTrapToGY(fieldCard);
                field.setFieldZone((SpellAndTrapCard) card);
            }
            else field.getTrapAndSpell().add((SpellAndTrapCard) card);
        }
    }

    public void timeSeal() {
        SpellAndTrapCard timeSealCard = game.getTheOtherPlayer().getField().hasTrapCard("Time Seal");
        if (timeSealCard != null && DuelView.getInstance().wantsToActivate("Time Seal")) {
            moveSpellOrTrapToGY(timeSealCard);
            game.getCurrentPlayer().setCanDraw(false); //TODO ما الان باید کرنت رو فالس کنیم یا د ادر رو؟ چون ترن عوض میشه فکر کنم ولی نوبت نه
        }
    }

    public void torrentialTribute() {
        SpellAndTrapCard torrentialTributeCard = game.getTheOtherPlayer().getField().hasTrapCard("Torrential Tribute");
        if (torrentialTributeCard != null && DuelView.getInstance().wantsToActivate("Torrential Tribute")) {
            moveSpellOrTrapToGY(torrentialTributeCard);
            ArrayList<MonsterCard> myMonsterCards = game.getCurrentPlayer().getField().getMonsterCards();
            ArrayList<MonsterCard> opponentsMonsterCards = game.getTheOtherPlayer().getField().getMonsterCards();
            int mySize = myMonsterCards.size();
            int opponentSize = opponentsMonsterCards.size();
            int max = Math.max(mySize, opponentSize);
            for (int i = 0; i < max ; i++) {
                if (myMonsterCards.get(0) != null) {
                    myMonsterCards.remove(0);
                    addMonsterToGY(myMonsterCards.get(0));
                }
                if (opponentsMonsterCards.get(0) != null) {
                    opponentsMonsterCards.remove(0);
                    addMonsterToGY(opponentsMonsterCards.get(0));
                }
            }
        }
    }

    public void trapHole(SpellAndTrapCard spellAndTrapCard, MonsterCard monsterCard) {
        moveSpellOrTrapToGY(spellAndTrapCard);
        addMonsterToGY(monsterCard);
    }

    public void mindCrush(SpellAndTrapCard spellAndTrapCard) {
        String cardName = DuelView.getInstance().getCardName();
        if (!opponentHasCard(cardName)) randomlyRemoveFromHand();
        else {
            ArrayList<Card> hand = game.getTheOtherPlayer().getField().getHand();
            int size = hand.size();
            ArrayList<Card> toRemove = new ArrayList<>();
            for (Card card : hand) if (card.getName().equals(cardName)) toRemove.add(card);
            hand.removeAll(toRemove);
        }
        moveSpellOrTrapToGY(spellAndTrapCard);
    }
    private void randomlyRemoveFromHand() {
        Random rand = new Random();
        int randomNumber = rand.nextInt(6);
        while (game.getCurrentPlayer().getField().getHand().get(randomNumber) == null)
            randomNumber = rand.nextInt(6);
        game.getCurrentPlayer().getField().getHand().remove(randomNumber);
    }
    private boolean opponentHasCard(String cardName) {
        for (Card card : game.getTheOtherPlayer().getField().getHand())
            if (card.getName().equals(cardName)) return true;
        return false;
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
        moveSpellOrTrapToGY(magicCylinder);
    }

    public void mirrorForce(SpellAndTrapCard mirrorForce, Account opponent) {
        ArrayList<MonsterCard> monsters = new ArrayList<>(opponent.getField().getMonsterCards());
        for (MonsterCard monster : monsters)
            if (monster.getMonsterCardModeInField() == MonsterCardModeInField.ATTACK_FACE_UP)
                addMonsterToGY(monster);
        moveSpellOrTrapToGY(mirrorForce);
    }


    private void decreaseLPWithTrap(Account account, int amount) {
        if (account.getField().hasThisCardActivated("Ring of defense") == null)
            account.changeLP(-amount);
    }

    private void advancedRitualArt(SpellAndTrapCard spellAndTrapCard) {
        ritualSummon();
        moveSpellOrTrapToGY(spellAndTrapCard);
    }

    private void twinTwisters(SpellAndTrapCard spellAndTrapCard) {
        moveSpellOrTrapToGY(spellAndTrapCard);
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
            moveSpellOrTrapToGY(toDestroy);
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
            moveSpellOrTrapToGY(toDestroy);
        }
        moveSpellOrTrapToGY(spellAndTrapCard);
    }

    private void darkHole(SpellAndTrapCard spellAndTrapCard) {
        int myMonstersSize = game.getCurrentPlayer().getField().getMonsterCards().size();
        int opponentSize = game.getTheOtherPlayer().getField().getMonsterCards().size();
        for (int i = 0; i < myMonstersSize; i++)
            addMonsterToGY(game.getCurrentPlayer().getField().getMonsterCards().get(0));
        for (int i = 0; i < opponentSize; i++)
            addMonsterToGY(game.getTheOtherPlayer().getField().getMonsterCards().get(0));
        moveSpellOrTrapToGY(spellAndTrapCard);
    }

    private void harpiesFeatherDuster(SpellAndTrapCard spellAndTrapCard) {
        int size = game.getTheOtherPlayer().getField().getTrapAndSpell().size();
        for (int i = 0; i < size; i++)
            moveSpellOrTrapToGY(game.getTheOtherPlayer().getField().getTrapAndSpell().get(0));
        moveSpellOrTrapToGY(spellAndTrapCard);

    }

    private void changeOfHeart(ChangeOfHeart changeOfHeart) {
        MonsterCard toHijack = DuelView.getInstance().getHijackedCard();
        if (toHijack != null) changeOfHeart.setHijackedCard(toHijack);
    }

    private void raigeki(SpellAndTrapCard spellAndTrapCard) {
        int size = game.getTheOtherPlayer().getField().getMonsterCards().size();
        for (int i = 0; i < size; i++)
            addMonsterToGY(game.getTheOtherPlayer().getField().getMonsterCards().get(0));
        moveSpellOrTrapToGY(spellAndTrapCard);
    }

    private void potOfGreed(SpellAndTrapCard spellAndTrapCard) {
        if (game.getCurrentPlayer().getField().getHand().size() <= 4)
            if (game.getCurrentPlayer().getField().getDeckZone().size() >= 2) {
                game.getCurrentPlayer().getField().getHand().add(game.getCurrentPlayer().getField().getDeckZone().get(0));
                game.getCurrentPlayer().getField().getDeckZone().remove(0);
                game.getCurrentPlayer().getField().getHand().add(game.getCurrentPlayer().getField().getDeckZone().get(0));
                game.getCurrentPlayer().getField().getDeckZone().remove(0);
            }
        moveSpellOrTrapToGY(spellAndTrapCard);
    }

    private void terraforming(SpellAndTrapCard spellAndTrapCard) {
        SpellAndTrapCard fieldSpell = DuelView.getInstance().getFieldSpellFromDeck();
        if (fieldSpell != null)
            if (game.getCurrentPlayer().getField().getHand().size() != 6)
                game.getCurrentPlayer().getField().getHand().add(fieldSpell);
        moveSpellOrTrapToGY(spellAndTrapCard);
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
        moveSpellOrTrapToGY(spellAndTrapCard);
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
            addMonsterToGY(monsterCard);
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
        game.getCurrentPlayer().getField().getMonsterCards().add(monsterCard);
        monsterCard.setMonsterCardModeInField(monsterCardModeInField);
    }

    public void showGraveyard() {
        String toPrint = game.getCurrentPlayer().getField().showGraveyard();
        Output.getForNow();
    }

    public void showSelectedCard() {
        if (!errorsForShowingSelectedCard()) return;
        Output.getForNow(); //TODO toString
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
        game.finishGame();
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
        game.finishGame();
    }

    private void moveToGraveyardAfterAttack(MonsterCard toBeRemoved, MonsterCard remover) {
        addMonsterToGY(toBeRemoved);
        if (toBeRemoved.getName().equals("Exploder Dragon") || toBeRemoved.getName().equals("Yomi Ship")) {
            addMonsterToGY(remover);
        }
    }

    private void addMonsterToGY(MonsterCard toBeRemoved) {
        toBeRemoved.reset();
        Field field = toBeRemoved.getOwner().getField();
        field.getGraveyard().add(toBeRemoved);
        field.getMonsterCards().remove(toBeRemoved);
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
        if (monsterCard != null) addMonsterToGY(monsterCard);
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
        addMonsterToGY(monsterCardToRemove);
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
        addMonsterToGY(game.getCurrentPlayer().getField().getMonsterCards().get(firstTribute));
        addMonsterToGY(game.getCurrentPlayer().getField().getMonsterCards().get(secondTribute));
        addMonsterToGY(game.getCurrentPlayer().getField().getMonsterCards().get(thirdTribute));
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
            game.getTheOtherPlayer().getField().setFieldZone(null);
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
