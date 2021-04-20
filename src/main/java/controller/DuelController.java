package controller;

import model.*;

import java.util.ArrayList;

public class DuelController {
    private static DuelController singleInstance = null;
    private DuelController() {

    }
    public static DuelController getInstance() {
        if (singleInstance == null)
            singleInstance = new DuelController();
        return singleInstance;
    }
    private Game game;
    public void run() {

    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    private void selectCard(boolean myCard, CardStatusInField cardStatusInField, int number) {

    }
    private boolean isSelectingCardValid(boolean myCard, CardStatusInField cardStatusInField, int number) {
        return true;
    }
    private void deselectCard() {

    }
    private boolean isDeselectingCardValid() {
        return true;
    }
    private void nextPhase() {

    }
    private void summon() {

    }
    private boolean isSummonValid() {
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
    private void flipSummon() {

    }
    private boolean isFlipSummonValid() {
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
