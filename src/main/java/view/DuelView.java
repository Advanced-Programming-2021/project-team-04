package view;

import controller.DuelController;
import model.Card;
import model.CardStatusInField;
import model.MonsterCard;
import model.SpellAndTrapCard;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DuelView extends ViewMenu {

    private static DuelView singleInstance = null;

    private boolean isRPSDone = false;

    private final Pattern selectCardPattern = Pattern.compile("^s(?:elect)? " +
            "(?=.*(?:(?:-(?:(?:-(?:monster)|(?:spell)|(?:hand))|(?:[msh])) (?<number>\\d+)?)|(?:-(?:(?:-field)|(?:f))))).+");

    private DuelView() {

    }

    @Override
    public void run() {
        String command;
        while (!(command = IO.getInstance().getInputMessage()).matches("(?:menu )?exit") &&
                !command.matches("(?:menu )?enter main(?: menu)?") &&
                !DuelController.getInstance().getGame().isGameFinished()) {
            Matcher selectCardMatcher = selectCardPattern.matcher(command);
            if (command.matches("(?:menu )?(?:s(?:how)?)-(?:c(?:urrent)?)"))
                showCurrentMenu();
            else if (command.matches("(?:(?:select -d)|(?:deselect))"))
                deselectCard();
            else if (selectCardMatcher.matches())
                selectCard(selectCardMatcher, command.contains("-o"), CardStatusInField.getCardStatusInField(command));
            else if (command.matches("sum(?:mon)?"))
                DuelController.getInstance().summon();
            else if (command.matches("se(?:t)?"))
                DuelController.getInstance().set();
            else if (command.matches("set -(?:(?:-position)|(?:p)) (?:att(?:ack)?|def(?:ense)?)"))
                setPosition(command);
            else if (command.matches("f(?:lip)?-sum(?:mon)?"))
                DuelController.getInstance().flipSummon();
            else if (command.matches("att(?:ack)? \\d+"))
                attack(command);
            else if (command.matches("att(?:ack)? d(?:ir(?:ect)?)?"))
                DuelController.getInstance().directAttack();
            else if (command.matches("activ(?:at)?e(?: effect)?"))
                DuelController.getInstance().activateSpell();
            else if (command.matches("(?:show )?grave(?:yard)?"))
                showGraveyard();
            else if (command.matches("(?:c(?:ard)? )?show \\-(?:(?:\\-select(?:ed)?)|(?:s))"))
                DuelController.getInstance().showSelectedCard();
            else if (command.matches("sur(?:render)?"))
                DuelController.getInstance().surrender();
            else if (command.matches("next(?: phase)?"))
                DuelController.getInstance().nextPhase();
            else if (command.matches("death(?: to)?(?: the)? mechanisms (\\d+)"))
                cheatDecreaseLP(command);
            else if (command.matches("underworld blues (\\d+)"))
                cheatIncreaseLP(command);
            else if (command.matches("person(?: of)? interest"))
                DuelController.getInstance().cheatSeeMyDeck();
            else if (command.matches("conspiracy(?: to) commit treason"))
                DuelController.getInstance().cheatSetWinner();
            else if (command.matches("drunk space pirate"))
                DuelController.getInstance().cheatShowRivalHand();
            else IO.getInstance().printInvalidCommand();
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
        IO.getInstance().wantToActivate(cardName);
        String activate = IO.getInstance().getInputMessage();
        return activate.toLowerCase().matches("y(?:es)?");
    }

    private String getRPSInput() {
        IO.getInstance().pickRPS();
        String input = IO.getInstance().getInputMessage().toLowerCase();
        while (!(input.matches("r(?:ock)?") || input.matches("p(?:aper)?") ||
                input.matches("s(?:cissors)?"))) {
            IO.getInstance().invalidSelection();
            input = IO.getInstance().getInputMessage();
        }
        return input;
    }

    public void chooseStarter(String winnerUsername) {
        //TODO WTF? what is the point of winnerUsername?
        IO.getInstance().chooseStarter();
        String username = IO.getInstance().getInputMessage();
        DuelController.getInstance().chooseStarter(username);
    }

    public int getTribute() {
        IO.getInstance().chooseTribute();
        String cardNumber = IO.getInstance().getInputMessage();
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
        Pattern pattern = Pattern.compile("set \\-(?:(?:\\-position)|(?:p)) (att(?:ack)?|def(?:ense)?)");
        Matcher matcher = pattern.matcher(input);
        matcher.find();
        String position = matcher.group(1);
        DuelController.getInstance().setPosition(position.matches("att(?:ack)?"));
    }

    private void attack(String input) {
        Pattern pattern = Pattern.compile("att(?:ack)? (\\d+)");
        Matcher matcher = pattern.matcher(input);
        matcher.find();
        int number = Integer.parseInt(matcher.group(1));
        DuelController.getInstance().attack(number);
    }

    private void showGraveyard() {
        DuelController.getInstance().showGraveyard();
        while (true) {
            String command = IO.getInstance().getInputMessage();
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
        Pattern pattern = Pattern.compile("Death(?: to)?(?: the)? Mechanisms (\\d+)");
        Matcher matcher = pattern.matcher(input);
        matcher.find();
        int number = Integer.parseInt(matcher.group(1));
        DuelController.getInstance().cheatDecreaseLP(number);
    }

    @Override
    public void showCurrentMenu() {
        IO.getInstance().printDuelMenuName();
    }

    public MonsterCard getRitualCard() {
        IO.getInstance().chooseRitualCard();
        String ritualCardNumber = IO.getInstance().getInputMessage();
        if (ritualCardNumber.equals("cancel")) return null;
        int number = Integer.parseInt(ritualCardNumber);
        return (MonsterCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand().get(number);
    }

    public MonsterCard getOpponentMonster() {
        IO.getInstance().chooseMonster();
        String monsterCardNumber = IO.getInstance().getInputMessage();
        if (monsterCardNumber.equals("cancel")) return null;
        int number = Integer.parseInt(monsterCardNumber);
        return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getMonsterCards().get(number);
    }

    public ArrayList<MonsterCard> getTributes() {
        IO.getInstance().chooseTributes();
        String numbers = IO.getInstance().getInputMessage();
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
        IO.getInstance().chooseMonsterMode();
        return IO.getInstance().getInputMessage();
    }

    public MonsterCard getMonsterCardFromHand() {
        IO.getInstance().chooseMonster();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        int monsterNumber = Integer.parseInt(number);
        return (MonsterCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand().get(monsterNumber);
    }

    public MonsterCard getFromMyGY() {
        IO.getInstance().chooseMonster();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        int monsterNumber = Integer.parseInt(number);
        return (MonsterCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getGraveyard().get(monsterNumber);
    }

    public Card getCardFromMyGY() {
        IO.getInstance().chooseCard();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        int cardNumber = Integer.parseInt(number);
        return DuelController.getInstance().getGame().getCurrentPlayer().getField().getGraveyard().get(cardNumber);
    }

    public Card getCardFromOpponentGY() {
        IO.getInstance().chooseCard();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        int cardNumber = Integer.parseInt(number);
        return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getGraveyard().get(cardNumber);
    }

    public MonsterCard getFromMyDeck() {
        IO.getInstance().chooseMonster();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        int monsterNumber = Integer.parseInt(number);
        return (MonsterCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getDeckZone().get(monsterNumber);
    }

    public MonsterCard getFromOpponentGY() {
        IO.getInstance().chooseMonster();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        int monsterNumber = Integer.parseInt(number);
        return (MonsterCard) DuelController.getInstance().getGame().getTheOtherPlayer().getField().getGraveyard().get(monsterNumber);
    }

    public boolean isMine() {
        IO.getInstance().isMine();
        String isMine = IO.getInstance().getInputMessage();
        return isMine.toLowerCase().matches("y(?:es)?");
    }

    public SpellAndTrapCard getFieldSpellFromDeck() {
        IO.getInstance().chooseFieldSpell();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        int spellNumber = Integer.parseInt(number);
        return (SpellAndTrapCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getDeckZone().get(spellNumber);
    }

    public MonsterCard getMonsterToEquip() {
        IO.getInstance().chooseMonster();
        String monsterCardNumber = IO.getInstance().getInputMessage();
        if (monsterCardNumber.equals("cancel")) return null;
        int number = Integer.parseInt(monsterCardNumber);
        return DuelController.getInstance().getGame().getCurrentPlayer().getField().getMonsterCards().get(number);
    }

    public MonsterCard getHijackedCard() {
        IO.getInstance().chooseMonster();
        String monsterCardNumber = IO.getInstance().getInputMessage();
        if (monsterCardNumber.equals("cancel")) return null;
        int number = Integer.parseInt(monsterCardNumber);
        return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getMonsterCards().get(number);
    }

    public boolean wantsToActivateTrap(String name) {
        if (DuelController.getInstance().handleMirageDragon("")) return false; //TODO name of the person activating this trap
        IO.getInstance().wantToActivate(name);
        String activate = IO.getInstance().getInputMessage();
        return activate.toLowerCase().matches("y(?:es)?");
    }

    public boolean ordinaryOrSpecial() {
        IO.getInstance().summonMode();
        String ordinaryOrSpecial = IO.getInstance().getInputMessage().toLowerCase();
        return !ordinaryOrSpecial.matches("o(?:rd(?:inary)?)?");
    }

    public int numOfSpellCardsToDestroy() {
        IO.getInstance().numOfCards();
        String number = IO.getInstance().getInputMessage();
        return Integer.parseInt(number);
    }

    public Card getCardFromHand() {
        IO.getInstance().chooseCard();
        String cardNumber = IO.getInstance().getInputMessage();
        if (cardNumber.matches("cancel")) return null;
        return DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand().get(Integer.parseInt(cardNumber));
    }

    public boolean summonGateGuardian() {
        IO.getInstance().gateGuardian();
        String input = IO.getInstance().getInputMessage().toLowerCase();
        return input.matches("y(?:es)?");
    }

    public int barbaros() {
        IO.getInstance().barbaros();
        String summonMode = IO.getInstance().getInputMessage();
        return Integer.parseInt(summonMode);
        // 1 summon with 2 tributes
        // 2 summon normally
        // 3 summon with 3 tributes
    }

    public boolean killMessengerOfPeace() {
        IO.getInstance().killMessengerOfPeace();
        String input = IO.getInstance().getInputMessage().toLowerCase();
        return input.matches("y(?:es)?");
    }

    public SpellAndTrapCard getFromMyField() {
        IO.getInstance().chooseSpell();
        String spellNumber = IO.getInstance().getInputMessage();
        if (spellNumber.matches("cancel")) return null;
        return DuelController.getInstance().getGame().getCurrentPlayer().getField().getTrapAndSpell().get(Integer.parseInt(spellNumber));
    }

    public SpellAndTrapCard getFromOpponentField() {
        IO.getInstance().chooseSpell();
        String spellNumber = IO.getInstance().getInputMessage();
        if (spellNumber.matches("cancel")) return null;
        return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getTrapAndSpell().get(Integer.parseInt(spellNumber));
    }

    public int whereToSummonFrom() {
        IO.getInstance().whereToSummonFrom();
        String originOfSummon = IO.getInstance().getInputMessage();
        return Integer.parseInt(originOfSummon);
        // 1 hand
        // 2 deck
        // 3 gy
    }

    public String getCardName() {
        IO.getInstance().cardName();
        return IO.getInstance().getInputMessage();
    }

    public void setRPSDone(boolean RPSDone) {
        isRPSDone = RPSDone;
    }

    public boolean wantsToExchange() {
        IO.getInstance().wantsToExchange();
        String input = IO.getInstance().getInputMessage().toLowerCase();
        return input.matches("y(?:es)?");
    }

    public String[] cardsToExchange() {
        IO.getInstance().cardsToExchange();
        //card from side deck
        // *
        //card from main deck
        String input = IO.getInstance().getInputMessage();
        String[] names = input.split(" \\* ");
        return names;
    }
}
