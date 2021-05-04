package view;

import controller.DuelController;
import model.Card;
import model.CardStatusInField;
import model.MonsterCard;
import model.SpellAndTrapCard;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DuelView extends Menu {

    private static DuelView singleInstance = null;

    public boolean isRPSDone = false;

    private final Pattern selectCardPattern = Pattern.compile("^s(?:elect)? " +
            "(?=.*(?:(?:\\-(?:(?:\\-(?:monster)|(?:spell)|(?:hand))|(?:[msh])) (?<number>\\d+)?)|(?:\\-(?:(?:\\-field)|(?:f))))).+");

    @Override
    public void run() {
        String command;
        while (!(command = Input.getInputMessage()).matches("(?:menu )?exit") &&
                !command.matches("(?:menu )?enter (?:M|m)ain(?: (?:M|m)enu)?") &&
                !DuelController.getInstance().getGame().isGameFinished()) {
            Matcher selectCardMatcher = selectCardPattern.matcher(command);
            if (command.matches("(?:menu )?(?:show|s)\\-(?:current|c)"))
                showCurrentMenu();
            else if (command.matches("(?:(?:select \\-d)|(?:deselect))"))
                deselectCard();
            else if (selectCardMatcher.matches())
                selectCard(selectCardMatcher, command.contains("-o"), CardStatusInField.getCardStatusInField(command));
            else if (command.matches("summon"))
                DuelController.getInstance().summon();
            else if (command.matches("set"))
                DuelController.getInstance().set();
            else if (command.matches("set --(position|p) (attack|defense)"))
                setPosition(command);
            else if (command.matches("flip-summon"))
                DuelController.getInstance().flipSummon();
            else if (command.matches("attack \\d"))
                attack(command);
            else if (command.matches("attack direct"))
                DuelController.getInstance().directAttack();
            else if (command.matches("activate effect"))
                DuelController.getInstance().activateSpell();
            else if (command.matches("show graveyard"))
                showGraveyard();
            else if (command.matches("card show --(selected|s)"))
                DuelController.getInstance().showSelectedCard();
            else if (command.matches("surrender"))
                DuelController.getInstance().getGame().finishGame();
            else if (command.matches("next phase"))
                DuelController.getInstance().nextPhase();
            else if (command.matches("Death to the Mechanisms (\\d+)"))
                cheatDecreaseLP(command);
            else if (command.matches("Underworld Blues (\\d+)"))
                cheatIncreaseLP(command);
            else if (command.matches("Person of Interest"))
                DuelController.getInstance().cheatSeeMyDeck();
            else if (command.matches("Conspiracy to Commit Treason"))
                DuelController.getInstance().cheatSetWinner();
            else if (command.matches("Drunk Space Pirate"))
                DuelController.getInstance().cheatShowRivalHand();
            else Output.getInstance().printInvalidCommand();
        }
    }

    public static DuelView getInstance() {
        if (singleInstance == null)
            singleInstance = new DuelView();
        return singleInstance;
    }

    public void runForRPS() {
        while (!isRPSDone) {
            String firstPlayersChoice = getRPSInput();
            String secondPlayersChoice = getRPSInput();
            DuelController.getInstance().rockPaperScissor(firstPlayersChoice, secondPlayersChoice);
        }
    }


    public boolean wantsToActivate(String cardName) {
        Output.getForNow();
        String activate = Input.getInputMessage();
        if (activate.toLowerCase().matches("yes|y")) return true;
        else return false;
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
        Output.getForNow();
        String cardNumber = Input.getInputMessage();
        return Integer.parseInt(cardNumber);
    }


    private void selectCard(Matcher matcher, boolean isPlayersCard, CardStatusInField cardStatusInField) {
        DuelController.getInstance().selectCard(isPlayersCard, cardStatusInField,
                cardStatusInField == CardStatusInField.FIELD_ZONE ? 0 : Integer.parseInt(matcher.group("number")));
    }

    private void deselectCard() {
        DuelController.getInstance().deselectCard();
    }

    private void setPosition(String input) {
        Pattern pattern = Pattern.compile("set --(?:position|p) (attack|defense)");
        Matcher matcher = pattern.matcher(input);
        matcher.find();
        String position = matcher.group(1);
        if (position.equals("attack")) DuelController.getInstance().setPosition(true);
        else DuelController.getInstance().setPosition(false);
    }

    private void attack(String input) {
        Pattern pattern = Pattern.compile("attack (\\d)");
        Matcher matcher = pattern.matcher(input);
        matcher.find();
        int number = Integer.parseInt(matcher.group(1));
        DuelController.getInstance().attack(number);
    }

    private void showGraveyard() {
        DuelController.getInstance().showGraveyard();
        while (true) {
            String command = Input.getInputMessage();
            if (command.matches("back")) break;
        }
    }

    private void cheatIncreaseLP(String string) {
        Pattern pattern = Pattern.compile("Underworld Blues (\\d+)");
        Matcher matcher = pattern.matcher(string);
        matcher.find();
        int number = Integer.parseInt(matcher.group(1));
        DuelController.getInstance().cheatIncreaseLP(number);
    }

    private void cheatDecreaseLP(String input) {
        Pattern pattern = Pattern.compile("Death to the Mechanisms (\\d+)");
        Matcher matcher = pattern.matcher(input);
        matcher.find();
        int number = Integer.parseInt(matcher.group(1));
        DuelController.getInstance().cheatDecreaseLP(number);
    }

    @Override
    public void showCurrentMenu() {
        Output.getInstance().printDuelMenuName();
    }

    public MonsterCard getRitualCard() {
        Output.getForNow();
        String ritualCardNumber = Input.getInputMessage();
        if (ritualCardNumber.equals("cancel")) return null;
        int number = Integer.parseInt(ritualCardNumber);
        return (MonsterCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand().get(number);
    }

    public MonsterCard getOpponentMonster() {
        Output.getForNow();
        String monsterCardNumber = Input.getInputMessage();
        if (monsterCardNumber.equals("cancel")) return null;
        int number = Integer.parseInt(monsterCardNumber);
        return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getMonsterCards().get(number);
    }

    public ArrayList<MonsterCard> getTributes() {
        Output.getForNow();
        String numbers = Input.getInputMessage();
        if (numbers.equals("cancel")) return null;
        String[] afterSplit = numbers.split(" ");
        int[] tributes = new int[afterSplit.length];
        for (int i = 0; i < afterSplit.length; i++)
            tributes[i] = Integer.parseInt(afterSplit[i]);
        ArrayList<MonsterCard> toTribute = new ArrayList<>();
        for (int number : tributes)
            toTribute.add(DuelController.getInstance().getGame().getCurrentPlayer().getField().getMonsterCards().get(number));
        return toTribute;
    }

    public String monsterMode() {
        Output.getForNow();
        return Input.getInputMessage();
    }

    public MonsterCard getMonsterCardFromHand() {
        Output.getForNow();
        String number = Input.getInputMessage();
        if (number.equals("cancel")) return null;
        int monsterNumber = Integer.parseInt(number);
        return (MonsterCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand().get(monsterNumber);
    }

    public MonsterCard getFromMyGY() {
        Output.getForNow();
        String number = Input.getInputMessage();
        if (number.equals("cancel")) return null;
        int monsterNumber = Integer.parseInt(number);
        return (MonsterCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getGraveyard().get(monsterNumber);
    }

    public Card getCardFromMyGY() {
        Output.getForNow();
        String number = Input.getInputMessage();
        if (number.equals("cancel")) return null;
        int cardNumber = Integer.parseInt(number);
        return DuelController.getInstance().getGame().getCurrentPlayer().getField().getGraveyard().get(cardNumber);
    }

    public Card getCardFromOpponentGY() {
        Output.getForNow();
        String number = Input.getInputMessage();
        if (number.equals("cancel")) return null;
        int cardNumber = Integer.parseInt(number);
        return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getGraveyard().get(cardNumber);
    }

    public MonsterCard getFromMyDeck() {
        Output.getForNow();
        String number = Input.getInputMessage();
        if (number.equals("cancel")) return null;
        int monsterNumber = Integer.parseInt(number);
        return (MonsterCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getDeckZone().get(monsterNumber);
    }

    public MonsterCard getFromOpponentGY() {
        Output.getForNow();
        String number = Input.getInputMessage();
        if (number.equals("cancel")) return null;
        int monsterNumber = Integer.parseInt(number);
        return (MonsterCard) DuelController.getInstance().getGame().getTheOtherPlayer().getField().getGraveyard().get(monsterNumber);
    }

    public boolean isMine() {
        Output.getForNow();
        String isMine = Input.getInputMessage();
        if (isMine.toLowerCase().matches("yes|y"))
            return true;
        else return false;
    }

    public SpellAndTrapCard getFieldSpellFromDeck() {
        Output.getForNow();
        String number = Input.getInputMessage();
        if (number.equals("cancel")) return null;
        int spellNumber = Integer.parseInt(number);
        return (SpellAndTrapCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getDeckZone().get(spellNumber);
    }

    public MonsterCard getMonsterToEquip() {
        Output.getForNow();
        String monsterCardNumber = Input.getInputMessage();
        if (monsterCardNumber.equals("cancel")) return null;
        int number = Integer.parseInt(monsterCardNumber);
        return DuelController.getInstance().getGame().getCurrentPlayer().getField().getMonsterCards().get(number);
    }

    public MonsterCard getHijackedCard() {
        Output.getForNow();
        String monsterCardNumber = Input.getInputMessage();
        if (monsterCardNumber.equals("cancel")) return null;
        int number = Integer.parseInt(monsterCardNumber);
        return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getMonsterCards().get(number);
    }

    public boolean wantsToActivateTrap(String name) {
        if (DuelController.getInstance().handleMirageDragon("")) return false; //TODO name of the person activating this trap
        Output.getForNow();
        String activate = Input.getInputMessage();
        if (activate.toLowerCase().matches("yes|y")) return true;
        else return false;
    }

    public boolean ordinaryOrSpecial() {
        Output.getForNow();
        String ordinaryOrSpecial = Input.getInputMessage().toLowerCase();
        if (ordinaryOrSpecial.matches("ordinary")) return false;
        else return true;
    }

    public int numOfSpellCardsToDestroy() {
        Output.getForNow();
        String number = Input.getInputMessage();
        return Integer.parseInt(number);
    }

    public Card getCardFromHand() {
        Output.getForNow();
        String cardNumber = Input.getInputMessage();
        if (cardNumber.matches("cancel")) return null;
        return DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand().get(Integer.parseInt(cardNumber));
    }

    public boolean summonGateGuardian() {
        Output.getForNow();
        String input = Input.getInputMessage().toLowerCase();
        if (input.matches("yes|y")) return true;
        return false;
    }

    public int barbaros() {
        Output.getForNow();
        String summonMode = Input.getInputMessage();
        return Integer.parseInt(summonMode);
        // 1 summon with 2 tributes
        // 2 summon normally
        // 3 summon with 3 tributes
    }

    public boolean killMessengerOfPeace() {
        Output.getForNow();
        String input = Input.getInputMessage().toLowerCase();
        if (input.matches("yes|y")) return true;
        else return false;
    }

    public SpellAndTrapCard getFromMyField() {
        Output.getForNow();
        String spellNumber = Input.getInputMessage();
        if (spellNumber.matches("cancel")) return null;
        return DuelController.getInstance().getGame().getCurrentPlayer().getField().getTrapAndSpell().get(Integer.parseInt(spellNumber));
    }

    public SpellAndTrapCard getFromOpponentField() {
        Output.getForNow();
        String spellNumber = Input.getInputMessage();
        if (spellNumber.matches("cancel")) return null;
        return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getTrapAndSpell().get(Integer.parseInt(spellNumber));
    }

    public int whereToSummonFrom() {
        Output.getForNow();
        String originOfSummon = Input.getInputMessage();
        return Integer.parseInt(originOfSummon);
        // 1 hand
        // 2 deck
        // 3 gy
    }

    public String getCardName() {
        Output.getForNow();
        return Input.getInputMessage();
    }

}
