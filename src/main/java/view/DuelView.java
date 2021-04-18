package view;

import model.Phases;

public class DuelView {
    private static DuelView singleInstance = null;
    private DuelView() {

    }
    public static DuelView getInstance() {
        if (singleInstance == null)
            singleInstance = new DuelView();
        return singleInstance;
    }
    public void run() {

    }
    private Phases currentPhase;
    private void drawPhase() {

    }
    private void standbyPhase() {

    }
    private void firstMainPhase() {

    }
    private void battlePhase() {

    }
    private void secondMainPhase() {

    }
    private void endPhase() {

    }
    private void handleMovesOutOfTurn() {

    }
    private void showCurrent() {

    }
    private void selectCard(String input) {

    }
    private void deselectCard() {

    }
    private void nextPhase() {

    }
    private void summon() {

    }
    private void set() {

    }
    private void setPosition(String input) {

    }
    private void flipSummon() {

    }
    private void attack(String input) {

    }
    private void directAttack() {

    }
    private void activateSpell() {

    }
    private void setSpellOrTrap() {

    }
    private void ritualSummonMenu() {

    }
    private void specialSummonMenu() {

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

}
