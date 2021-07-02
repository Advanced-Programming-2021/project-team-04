package yugioh.view;


import lombok.Setter;
import yugioh.controller.DuelController;
import yugioh.model.AI;
import yugioh.model.cards.Card;
import yugioh.model.CardStatusInField;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;


import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class DuelView extends ViewMenu {


    private static final SecureRandom RANDOM = new SecureRandom();


    private static final String[] RPS = {"r", "p", "s"};


    private static DuelView singleInstance = null;


    private final Pattern selectCardPattern = Pattern.compile("^s(?:elect)? " +
            "(?=.*(?:-(?:(?:-monster|-spell|-hand)|[msh])|-(?:-field|f)))(?=.*(?<number>\\d+)).+");
    private final Pattern attackPattern = Pattern.compile("att(?:ack)? (?<number>\\d+)");
    private final Pattern cheatDecreaseLPPattern = Pattern.compile("Death(?: to)?(?: the)? Mechanisms (?<number>\\d+)");
    private final Pattern cheatIncreaseLPPattern = Pattern.compile("Underworld Blues (?<number>\\d+)");


    @Setter
    private boolean isRPSDone = false;


    private DuelView() { }


    public static DuelView getInstance() {
        if (singleInstance == null)
            singleInstance = new DuelView();
        return singleInstance;
    }


    @Override
    public void run() {
        String command;
        while (!DuelController.getInstance().getGame().isGameFinished() &&
                !(command = IO.getInstance().getInputMessage()).matches("(?:menu )?exit") &&
                !command.matches("(?:menu )?enter [Mm]ain(?: menu)?")) {
            Matcher selectCardMatcher = selectCardPattern.matcher(command);
            Matcher attackMatcher = attackPattern.matcher(command);
            Matcher cheatDecreaseLPMatcher = cheatDecreaseLPPattern.matcher(command);
            Matcher cheatIncreaseLPMatcher = cheatIncreaseLPPattern.matcher(command);
            if (command.matches("(?:menu )?s(?:how)?-c(?:urrent)?"))
                showCurrentMenu();
            else if (command.matches("select -d|deselect"))
                DuelController.getInstance().deselectCard();
            else if (selectCardMatcher.matches())
                selectCard(selectCardMatcher, !command.contains("-o"), CardStatusInField.getCardStatusInField(command));
            else if (command.matches("sum(?:mon)?"))
                DuelController.getInstance().summon();
            else if (command.matches("set?"))
                DuelController.getInstance().set();
            else if (command.matches("set -(?:-position|p) (?:att(?:ack)?|def(?:ense)?)"))
                DuelController.getInstance().setPosition(command.contains("att"));
            else if (command.matches("f(?:lip)?-sum(?:mon)?"))
                DuelController.getInstance().flipSummon();
            else if (attackMatcher.matches())
                DuelController.getInstance().attack(Integer.parseInt(attackMatcher.group("number")) - 1);
            else if (command.matches("att(?:ack)? d(?:ir(?:ect)?)?"))
                DuelController.getInstance().directAttack();
            else if (command.matches("activ(?:at)?e(?: effect)?"))
                DuelController.getInstance().activateSpell();
            else if (command.matches("(?:show )?grave(?:yard)?"))
                showGraveyard();
            else if (command.matches("(?:c(?:ard)? )?show -(?:-select(?:ed)?|s)"))
                DuelController.getInstance().showSelectedCard();
            else if (command.matches("sur(?:render)?"))
                DuelController.getInstance().surrender();
            else if (command.matches("next(?: phase)?"))
                DuelController.getInstance().nextPhase();
            else if (cheatDecreaseLPMatcher.matches())
                DuelController.getInstance().cheatDecreaseLP(Integer.parseInt(cheatDecreaseLPMatcher.group("number")));
            else if (cheatIncreaseLPMatcher.matches())
                DuelController.getInstance().cheatIncreaseLP(Integer.parseInt(cheatIncreaseLPMatcher.group("number")));
            else if (command.matches("Person(?: of)? Interest"))
                DuelController.getInstance().cheatSeeMyDeck();
            else if (command.matches("Conspiracy(?: to)? Commit Treason"))
                DuelController.getInstance().cheatSetWinner();
            else if (command.matches("Drunk Space Pirate"))
                DuelController.getInstance().cheatShowRivalHand();
            else IO.getInstance().printInvalidCommand();
        }
    }


    public void runForRPS() {
        isRPSDone = false;
        while (!isRPSDone) {
            String firstPlayersChoice = getRPSInput();
            String secondPlayersChoice = getRPSInput();
            DuelController.getInstance().rockPaperScissor(firstPlayersChoice, secondPlayersChoice);
        }
    }


    public void runForRPSAgainstAI() {
        isRPSDone = false;
        while (!isRPSDone) {
            String playersChoice = getRPSInput();
            String AIsChoice = RPS[RANDOM.nextInt(3)];
            IO.getInstance().printAIsRPS(AIsChoice);
            DuelController.getInstance().rockPaperScissor(playersChoice, AIsChoice);
        }
    }


    public boolean wantsToActivate(String cardName) {
        IO.getInstance().wantToActivate(cardName);
        return IO.getInstance().getInputMessage().toLowerCase().matches("y(?:es)?");
    }


    public String getRPSInput() {
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
        if (winnerUsername.equals(AI.AI_USERNAME)) {
            DuelController.getInstance().chooseStarter(winnerUsername);
            return;
        }
        IO.getInstance().chooseStarter();
        String username = IO.getInstance().getInputMessage();
        DuelController.getInstance().chooseStarter(username);
    }


    public int getTribute() {
        IO.getInstance().chooseTribute();
        return Integer.parseInt(IO.getInstance().getInputMessage()) - 1;
    }


    private void selectCard(Matcher matcher, boolean isPlayersCard, CardStatusInField cardStatusInField) {
        DuelController.getInstance().selectCard(isPlayersCard, cardStatusInField,
                cardStatusInField == CardStatusInField.FIELD_ZONE ? 0 : Integer.parseInt(matcher.group("number")) - 1);
    }


    public void showGraveyard() {
        DuelController.getInstance().showGraveyard();
        while (true) {
            String command = IO.getInstance().getInputMessage();
            if (command.matches("back")) break;
            IO.getInstance().printInvalidCommand();
        }
    }


    @Override
    public void showCurrentMenu() {
        IO.getInstance().printDuelMenuName();
    }


    public MonsterCard getRitualCard() {
        IO.getInstance().chooseRitualCard();
        String ritualCardNumber = IO.getInstance().getInputMessage();
        if (ritualCardNumber.equals("cancel")) return null;
        try {
            return (MonsterCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand().get(Integer.parseInt(ritualCardNumber) - 1);
        } catch (Exception e) {
            IO.getInstance().invalidSelection();
            return getRitualCard();
        }
    }


    public MonsterCard getOpponentMonster() {
        IO.getInstance().chooseMonster();
        String monsterCardNumber = IO.getInstance().getInputMessage();
        if (monsterCardNumber.equals("cancel")) return null;
        try {
            return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getMonsterCards().get(Integer.parseInt(monsterCardNumber) - 1);
        } catch (Exception e) {
            IO.getInstance().invalidSelection();
            return getOpponentMonster();
        }
    }


    public ArrayList<MonsterCard> getTributes() {
        IO.getInstance().chooseTributes();
        try {
            return getTributeMonsterCards();
        } catch (Exception exception) {
            return getTributesAgain();
        }
    }


    private ArrayList<MonsterCard> getTributesAgain() {
        IO.getInstance().chooseTributesAgain();
        try {
            return getTributeMonsterCards();
        } catch (Exception exception) {
            return getTributesAgain();
        }
    }


    private ArrayList<MonsterCard> getTributeMonsterCards(){
        var input = IO.getInstance().getInputMessage();
        if (input.equals("cancel")) return null;
        return Arrays.stream(input.split(" ")).map(Integer::parseInt).map(i -> DuelController.getInstance().getGame().getCurrentPlayer().getField().getMonsterCards().get(i - 1)).collect(Collectors.toCollection(ArrayList::new));
    }


    public String monsterMode() {
        IO.getInstance().chooseMonsterMode();
        return IO.getInstance().getInputMessage();
    }


    public MonsterCard getMonsterCardFromHand() {
        IO.getInstance().chooseMonster();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return (MonsterCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            IO.getInstance().invalidSelection();
            return getMonsterCardFromHand();
        }
    }


    public MonsterCard getFromMyGY() {
        IO.getInstance().chooseMonster();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return (MonsterCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getGraveyard().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            IO.getInstance().invalidSelection();
            return getFromMyGY();
        }
    }


    public Card getCardFromMyGY() {
        IO.getInstance().chooseCard();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return DuelController.getInstance().getGame().getCurrentPlayer().getField().getGraveyard().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            IO.getInstance().invalidSelection();
            return getCardFromMyGY();
        }
    }


    public Card getCardFromOpponentGY() {
        IO.getInstance().chooseCard();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getGraveyard().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            IO.getInstance().invalidSelection();
            return getCardFromOpponentGY();
        }
    }


    public MonsterCard getFromOpponentGY() {
        IO.getInstance().chooseMonster();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return (MonsterCard) DuelController.getInstance().getGame().getTheOtherPlayer().getField().getGraveyard().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            IO.getInstance().invalidSelection();
            return getFromOpponentGY();
        }
    }


    public boolean isMine() {
        IO.getInstance().isMine();
        return IO.getInstance().getInputMessage().toLowerCase().matches("y(?:es)?");
    }


    public SpellAndTrapCard getFieldSpellFromDeck() {
        IO.getInstance().printString(DuelController.getInstance().sortFieldCards());
        IO.getInstance().chooseFieldSpell();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return (SpellAndTrapCard) DuelController.getInstance().getGame().getCurrentPlayer().getField().getDeckZone().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            IO.getInstance().invalidSelection();
            return getFieldSpellFromDeck();
        }
    }


    public MonsterCard getMonsterToEquip() {
        IO.getInstance().chooseMonster();
        String monsterCardNumber = IO.getInstance().getInputMessage();
        if (monsterCardNumber.equals("cancel")) return null;
        try {
            return DuelController.getInstance().getGame().getCurrentPlayer().getField().getMonsterCards().get(Integer.parseInt(monsterCardNumber) - 1);
        } catch (Exception e) {
            IO.getInstance().invalidSelection();
            return getMonsterToEquip();
        }
    }


    public MonsterCard getHijackedCard() {
        IO.getInstance().chooseMonster();
        String monsterCardNumber = IO.getInstance().getInputMessage();
        if (monsterCardNumber.equals("cancel")) return null;
        try {
            return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getMonsterCards().get(Integer.parseInt(monsterCardNumber) - 1);
        } catch (Exception e) {
            IO.getInstance().invalidSelection();
            return getHijackedCard();
        }
    }


    public boolean wantsToActivateTrap(String name, String username) {
        if (DuelController.getInstance().handleMirageDragon(username))
            return false;
        IO.getInstance().wantToActivate(name);
        return IO.getInstance().getInputMessage().toLowerCase().matches("y(?:es)?");
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
        try {
            return DuelController.getInstance().getGame().getCurrentPlayer().getField().getHand().get(Integer.parseInt(cardNumber) - 1);
        } catch (Exception e) {
            IO.getInstance().invalidSelection();
            return getCardFromHand();
        }
    }


    public Card getCardFromOpponentHand() {
        IO.getInstance().chooseCard();
        String cardNumber = IO.getInstance().getInputMessage();
        if (cardNumber.matches("cancel")) return null;
        try {
            return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getHand().get(Integer.parseInt(cardNumber) - 1);
        } catch (Exception e) {
            IO.getInstance().invalidSelection();
            return getCardFromOpponentHand();
        }
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
        try {
            return DuelController.getInstance().getGame().getCurrentPlayer().getField().getSpellAndTrapCards().get(Integer.parseInt(spellNumber) - 1);
        } catch (Exception e) {
            IO.getInstance().invalidSelection();
            return getFromMyField();
        }
    }


    public SpellAndTrapCard getFromOpponentField() {
        IO.getInstance().chooseSpell();
        String spellNumber = IO.getInstance().getInputMessage();
        if (spellNumber.matches("cancel")) return null;
        try {
            return DuelController.getInstance().getGame().getTheOtherPlayer().getField().getSpellAndTrapCards().get(Integer.parseInt(spellNumber) - 1);
        } catch (Exception e) {
            IO.getInstance().invalidSelection();
            return getFromOpponentField();
        }
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


    public boolean wantsToExchange() {
        IO.getInstance().wantsToExchange();
        return IO.getInstance().getInputMessage().toLowerCase().matches("y(?:es)?");
    }


    public String[] cardsToExchange() {
        IO.getInstance().cardsToExchange();
        //card from side deck
        // *
        //card from main deck
        return IO.getInstance().getInputMessage().split(" \\* ");
    }


    public MonsterCard getFromMyDeck(boolean isOpponent) {
        IO.getInstance().chooseMonster();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return (MonsterCard) DuelController.getInstance().getGame().getTheOtherPlayer().getField().getDeckZone().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            IO.getInstance().invalidSelection();
            return getFromMyDeck(isOpponent);
        }
    }


    public MonsterCard getFromMyGY(boolean isOpponent) {
        IO.getInstance().chooseMonster();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return (MonsterCard) DuelController.getInstance().getGame().getTheOtherPlayer().getField().getGraveyard().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            IO.getInstance().invalidSelection();
            return getFromMyGY(isOpponent);
        }
    }


    public MonsterCard getMonsterCardFromHand(boolean isOpponent) {
        IO.getInstance().chooseMonster();
        String number = IO.getInstance().getInputMessage();
        if (number.equals("cancel")) return null;
        try {
            return (MonsterCard) DuelController.getInstance().getGame().getTheOtherPlayer().getField().getHand().get(Integer.parseInt(number) - 1);
        } catch (Exception e) {
            IO.getInstance().invalidSelection();
            return getMonsterCardFromHand(isOpponent);
        }
    }


}