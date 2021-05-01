package controller;

import model.*;
import view.ChainView;
import view.DuelView;
import view.Output;

import javax.swing.text.View;
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

    public void rockPaperScissor(String thisPlayerRPS, String theOtherPlayerRPS) {

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
        }
        else if (cardStatusInField.equals(CardStatusInField.MONSTER_FIELD) &&
                convertedNumber + 1 > thisPlayer.getField().getMonsterCards().size())  {
            Output.getForNow();
            return false;
        }
        else if (cardStatusInField.equals(CardStatusInField.SPELL_FIELD) &&
        convertedNumber + 1 > thisPlayer.getField().getTrapAndSpell().size()) {
            Output.getForNow();
            return false;
        }
        else if (cardStatusInField.equals(CardStatusInField.FIELD_ZONE) &&
               thisPlayer.getField().getFieldZone() == null) {
            Output.getForNow();
            return false;
        }
        return true;
    }
    private int handleMyCardNumber (int number) {
        if (number == 1) return 2;
        if (number == 2) return 3;
        if (number == 3) return 1;
        if (number == 4) return 4;
        return 0;
    }
    private int handleOpponentsCardNumber (int number) {
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

    private void summon() {
        //TODO چک کن کارتایی که وضعشون خاصه ritual
        if (!isSummonValid()) return;
        summonWithTribute();
        MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
        game.getCurrentPlayer().getField().getMonsterCards().add(monsterCard);
    }
    private void summonWithTribute() {
        MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
        if (monsterCard.getLevel() == 5 || monsterCard.getLevel() == 6) {
            int number = DuelView.getInstance().getTribute();
            int convertedNumber = handleMyCardNumber(number);
            if (isTributeValid(convertedNumber)) {
                moveOneCardToGY(convertedNumber);
            }
        }
        else if (monsterCard.getLevel() == 7 || monsterCard.getLevel() == 8) {
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
        game.getCurrentPlayer().getField().getMonsterCards().remove(cardToRemove);
        game.getCurrentPlayer().getField().getGraveyard().add(cardToRemove);
    }

    private void moveToGraveYard(int firstConvertedNumber, int secondConvertedNumber) {
        MonsterCard firstCard = game.getCurrentPlayer().getField().getMonsterCards().get(firstConvertedNumber);
        MonsterCard secondCard = game.getCurrentPlayer().getField().getMonsterCards().get(secondConvertedNumber);
        game.getCurrentPlayer().getField().getMonsterCards().remove(firstCard);
        game.getCurrentPlayer().getField().getGraveyard().add(firstCard);
        game.getCurrentPlayer().getField().getMonsterCards().remove(secondCard);
        game.getCurrentPlayer().getField().getGraveyard().add(secondCard);
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
        }
        else if (!game.getCurrentPlayer().getField().getHand().contains(game.getSelectedCard())) {
            Output.getForNow();
            return false;
        }
        else if (!(game.getSelectedCard() instanceof MonsterCard)) {
            Output.getForNow();
            return false;
        }
        MonsterCard monsterCard = (MonsterCard) game.getSelectedCard();
        if (monsterCard.getCardType().equals("Ritual")) {
            Output.getForNow();
            return false;
        }
        else if (game.getCurrentPlayer().getField().getMonsterCards().size() == 5) {
            Output.getForNow();
            return false;
        }
        else if (game.isHasSummonedInThisTurn()) {
            Output.getForNow();
            return false;
        }
        else if ((monsterCard.getLevel() == 5 || monsterCard.getLevel() == 6) &&
        game.getCurrentPlayer().getField().getMonsterCards().isEmpty()) {
            Output.getForNow();
            return false;
        }
        else if ((monsterCard.getLevel() >=7 ) &&
                game.getCurrentPlayer().getField().getMonsterCards().size() < 2) {
            Output.getForNow();
            return false;
        }
        return true;
    }

    private void set() {

    }

    private boolean isSetValid() {
        return true;
    }

    private void setPosition(boolean isAttack) {

    }

    private boolean isSetPositionValid(boolean isAttack) {
        return true;
    }



    private void attack(int opponentMonsterPositionNumber) {

    }

    private boolean isAttackValid(int opponentMonsterPositionNumber) {
        return true;
    }

    private void directAttack() {

    }

    private boolean isDirectAttackValid() {
        return true;
    }

    private void activateSpell() {

    }

    private boolean isActivatingSpellValid() {
        return true;
    }

    private void setSpellOrTrap() {

    }

    private boolean isSetSpellOrTrapValid() {
        return true;
    }

    private void ritualSummonMenu(String card, ArrayList cards) {

    }

    private void specialSummonMenu(String cardName) {

    }

    private void showGraveyard() {

    }

    private void showSelectedCard() {

    }

    private void cheatShowRivalHand() {

    }

    private void cheatTakeMoreCards() {

    }

    private void cheatIncreaseLP() {

    }

    private void cheatSetWinner() {

    }

    private void cheatSeeMyDeck() {

    }

    private void cheatChooseFromGraveyard() {

    }

    private void cheatDecreaseLP() {

    }

    public MonsterCard getMonsterAttacking() {

    }

    public MonsterCard forManEaterBug() {
        // ask the client to choose card
    }

    public MonsterCard forScanner() {

    }

    public MonsterCard forTexChanger() {
        //remove it from the place u're getting it
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
}
