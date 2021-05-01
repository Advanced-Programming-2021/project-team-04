package view;

import controller.DuelController;
import model.MonsterCard;
import model.Phases;

import java.util.ArrayList;

public class DuelView extends Menu {
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
    

    public boolean wantsToActivate(String cardName) {
        Output.getForNow();
        String response = Input.getInputMessage();
        while (true) {
            if (response.matches("[Yy]")) return true;
            if (response.matches("[Nn]")) return false;
            Output.getForNow();
        }
    }

    private String getRPSInput() {
        Output.getForNow(); //maybe it could get the users name as input to say who should enter
        String input = Input.getInputMessage().toLowerCase();
        while (!(input.matches("rock") || input.matches("paper") ||
                input.matches("scissors"))) {
            Output.getForNow();
            input = Input.getInputMessage();
        }
        return input;
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

    @Override
    public void run() {

    }

    @Override
    public void showCurrentMenu() {
        Output.getInstance().printDuelMenuName();
    }

    public MonsterCard getRitualCard() {
    return null;
    }

    public ArrayList<MonsterCard> getTributes() {
        return null;
    }

    public String monsterMode() {
        return "";
    }

    public MonsterCard getMonsterCardFromHand() {
        return null;
    }
    public boolean ordinaryOrSpecial() {
        // ordinary false
        // special true
        boolean isSpecial = true;
        return isSpecial;
    }
}
