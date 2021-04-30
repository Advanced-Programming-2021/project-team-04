package view;

import controller.DuelController;
import model.Phases;

public class DuelView {
    private static DuelView singleInstance = null;
    public boolean isRPSDone = false;
    private DuelView() {

    }
    public static DuelView getInstance() {
        if (singleInstance == null)
            singleInstance = new DuelView();
        return singleInstance;
    }
    public void runForGame() {
        runForRPS();
        //TODO call the other methods here
    }
    public void runForRPS() {
        while(!isRPSDone) {
            String firstPlayersChoice = getRPSInput();
            String secondPlayersChoice = getRPSInput();
            DuelController.getInstance().rockPaperScissor(firstPlayersChoice, secondPlayersChoice);
        }
    }

    private String getRPSInput() {
        Output.getForNow(); //maybe it could get the users name as input to say who should enter
        String input = Input.getInputMessage();
        while (!(input.matches("[Rr]ock") || input.matches("[Pp]aper") ||
                input.matches("[Ss]cissors"))) {
            Output.getForNow();
            input = Input.getInputMessage();
        }
        return input.toLowerCase();
    }

    public void chooseStarter(String winnerUsername) {
        Output.getForNow();
        String username = Input.getInputMessage();
        DuelController.getInstance().chooseStarter(username);
    }

    public int getTribute() {
        return 0;
    }

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
