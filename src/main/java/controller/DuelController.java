package controller;

import model.*;
import view.ChainView;
import view.DuelView;
import view.Output;

import java.util.ArrayList;

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
        //TODO کارتایی که اثرشون باید اینجا فعال شه مثل کامند نایت
        makeChain(field);
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
        Account thisPlayer = null;
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
        if (!isSummonValid()) return;
        summonWithTribute();
        MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
        game.getCurrentPlayer().getField().getMonsterCards().add(monsterCard);
        monsterCard.setMonsterCardModeInField(MonsterCardModeInField.ATTACK_FACE_UP);
        if (monsterCard.getName().equals("Terratiger, the Empowered Warrior"))
            terraTigerMethod();
        game.setSelectedCard(null);
        Output.getForNow();
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
        addToGY(cardToRemove);
    }

    private void moveToGraveYard(int firstConvertedNumber, int secondConvertedNumber) {
        MonsterCard firstCard = game.getCurrentPlayer().getField().getMonsterCards().get(firstConvertedNumber);
        MonsterCard secondCard = game.getCurrentPlayer().getField().getMonsterCards().get(secondConvertedNumber);
        addToGY(firstCard);
        addToGY(secondCard);
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
        game.setSelectedCard(null);
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
            }
            else if ((monsterCard.getLevel() == 5 || monsterCard.getLevel() == 6) &&
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
        return true;
    }

    private void flipSummon() {
        if (!isFlipSummonValid()) return;
        Output.getForNow();
        game.setSelectedCard(null);
        MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
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
        MonsterCard attacked = game.getTheOtherPlayer().getField().getMonsterCards().get(opponentMonsterPositionNumber);
        MonsterCard attacker = (MonsterCard) game.getSelectedCard();
        if (attacked.getName().equals("Suijin") &&
                DuelView.getInstance().wantsToActivate("Suijin")) {
            ((Suijin) attacked).specialMethod(attacker);
        }
        if (attacked.getMonsterCardModeInField() == MonsterCardModeInField.ATTACK_FACE_UP)
            attackInAttack(attacked, attacker);
        else attackInDefense(attacked, attacker);
        attacker.setHasAttacked(true);
        game.setSelectedCard(null);

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
        if (selectedCard.getProperty().equals("Field")) {
            if (game.getCurrentPlayer().getField().getFieldZone() != null) {
                game.getCurrentPlayer().getField().getGraveyard().add(game.getCurrentPlayer().getField().getFieldZone());
            }
            game.getCurrentPlayer().getField().setFieldZone(selectedCard);
            selectedCard.setActive(true);
        } else {
            game.getCurrentPlayer().getField().getTrapAndSpell().add(selectedCard);
            selectedCard.setActive(true);
        }
        //TODO MIRAGE DRAGON
        Output.getForNow();
        game.setSelectedCard(null);
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
        //TODO check specific card requirements
        return true;
    }

    public void setSpellAndTrap() {
        if (!isSettingSpellAndTrapValid()) return;
        game.getCurrentPlayer().getField().getHand().remove(game.getSelectedCard());
        game.getCurrentPlayer().getField().getTrapAndSpell().add((SpellAndTrapCard) game.getSelectedCard());
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
            addToGY(monsterCard);
        }
        game.getCurrentPlayer().getField().getGraveyard().add(game.getSelectedCard());
        SpellAndTrapCard thisSpell = (SpellAndTrapCard) game.getSelectedCard();
        game.getCurrentPlayer().getField().getTrapAndSpell().remove(thisSpell);
        Output.getForNow();
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

    public void specialSummon(MonsterCard monsterCard, MonsterCardModeInField monsterCardModeInField) {
        game.getCurrentPlayer().getField().getHand().remove(monsterCard);
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
        addToGY(toBeRemoved);
        if (toBeRemoved.getName().equals("Exploder Dragon") || toBeRemoved.getName().equals("Yomi Ship")) {
            addToGY(remover);
        }
    }

    private void addToGY(MonsterCard toBeRemoved) {
        toBeRemoved.getOwner().getField().getGraveyard().add(toBeRemoved);
        toBeRemoved.getOwner().getField().getMonsterCards().remove(toBeRemoved);
    }

    public MonsterCard getMonsterAttacking() {

    }

    public MonsterCard forManEaterBug() {
        // ask the client to choose card
    }

    public MonsterCard forScanner() {

    }

    public MonsterCard forTexChanger() {
        if (!DuelView.getInstance().wantsToActivate("Texchanger")) return null;
        //TODO
    }

    public void chooseMonsterMode(MonsterCard monsterCard) {

    }

    public SpellAndTrapCard chosenTrapCard() {

    }

    public MonsterCard selectFromGraveYard() {//for herald of creation
    }

    public MonsterCard selectToRemove() {
        //for herald of creation
    }

    private void terraTigerMethod() {
        if (!DuelView.getInstance().wantsToActivate("Terratiger, the Empowered Warrior")) return;
        if (!errorForTerraTiger()) return;
        MonsterCard monsterCard = DuelView.getInstance().getMonsterCardFromHand();
        while (!game.getCurrentPlayer().getField().ordinaryLowLevelCards().contains(monsterCard)) {
            monsterCard = DuelView.getInstance().getMonsterCardFromHand();
        }
        specialSummon(monsterCard, MonsterCardModeInField.DEFENSE_FACE_DOWN);
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
        specialSummon((MonsterCard) game.getSelectedCard(), MonsterCardModeInField.ATTACK_FACE_UP);
    }
}
