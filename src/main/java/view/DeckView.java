package view;

import controller.DeckController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeckView extends Menu {

    private static DeckView singleInstance = null;

    private final Pattern createDeckPattern = Pattern.compile("(?:deck )?create (?<name>\\S+)");
    private final Pattern deleteDeckPattern = Pattern.compile("(?:deck )?delete (?<name>\\S+)");
    private final Pattern activateDeckPattern = Pattern.compile("(?:deck )?" +
            "(?:(?:set)|(?:activate)|(?:set\\-activ(?:at)?e)) (?<name>\\S+)");
    private final Pattern addCardToDeckPattern = Pattern.compile("^(?:deck )?a(?:dd)?(?:\\-| )c(?:ard) " +
            "(?=.*(?:\\-(?:(?:\\-card)|(?:c))) (?<cardName>\\S+))(?=.*(?:\\-(?:(?:\\-deck)|(?:d))) (?<deckName>\\S+)).+$");
    private final Pattern removeCardFromDeckPattern = Pattern.compile("^(?:deck )?r(?:m)?(?:\\-| )c(?:ard) " +
            "(?=.*(?:\\-(?:(?:\\-card)|(?:c))) (?<cardName>\\S+))(?=.*(?:\\-(?:(?:\\-deck)|(?:d))) (?<deckName>\\S+)).+$");
    private final Pattern showDeckPattern = Pattern.compile("^(?:deck )??show " +
            "(?=.*(?:\\-(?:(?:\\-deck)|(?:d))) (?<deckName>\\S+)).+$");

    private DeckView() {}

    public static DeckView getInstance() {
        if (singleInstance == null)
            singleInstance = new DeckView();
        return singleInstance;
    }

    @Override
    public void run() {
        String command;
        while (!(command = Input.getInputMessage()).matches("(?:menu )?exit") &&
                !command.matches("(?:menu )?enter (?:M|m)ain(?: (?:M|m)enu)?")) {
            Matcher createDeckMatcher = createDeckPattern.matcher(command);
            Matcher deleteDeckMatcher = deleteDeckPattern.matcher(command);
            Matcher activateDeckMatcher = activateDeckPattern.matcher(command);
            Matcher addCardToDeckMatcher = addCardToDeckPattern.matcher(command);
            Matcher removeCardFromDeckMatcher = removeCardFromDeckPattern.matcher(command);
            Matcher showDeckMatcher = showDeckPattern.matcher(command);
            if (command.matches("(?:menu )?(?:show|s)\\-(?:current|c)"))
                showCurrentMenu();
            else if (command.matches("(?:menu )?enter \\S+"))
                Output.getInstance().printMenuNavigationImpossible();
            else if (command.matches("(?:deck )?show \\-(?:(?:\\-all)|(?:a))"))
                printAllDecks();
            else if (showDeckMatcher.matches())
                printDeck(showDeckMatcher, !command.contains("-s"));
            else if (command.matches("(?:deck )?show \\-(?:(?:\\-cards)|(?:c))"))
                printAllCards();
            else if (createDeckMatcher.matches())
                createDeck(createDeckMatcher);
            else if (deleteDeckMatcher.matches())
                deleteDeck(deleteDeckMatcher);
            else if (activateDeckMatcher.matches())
                activateDeck(activateDeckMatcher);
            else if (addCardToDeckMatcher.matches())
                addCardToDeck(addCardToDeckMatcher, !command.contains("-s"));
            else if (removeCardFromDeckMatcher.matches())
                removeCardFromDeck(removeCardFromDeckMatcher, !command.contains("-s"));
            else Output.getInstance().printInvalidCommand();
        }
    }

    @Override
    public void showCurrentMenu() {
        Output.getInstance().printDeckMenuName();
    }

    private void createDeck(Matcher matcher) {
        DeckController.getInstance().createDeck(matcher.group("name"));
    }

    private void deleteDeck(Matcher matcher) {
        DeckController.getInstance().deleteDeck(matcher.group("name"));
    }

    private void activateDeck(Matcher matcher) {
        DeckController.getInstance().activateDeck(matcher.group("name"));
    }

    private void addCardToDeck(Matcher matcher, boolean isMainDeck) {
        DeckController.getInstance().addCardToDeck(matcher.group("deckName"), matcher.group("cardName"), isMainDeck);
    }

    private void removeCardFromDeck(Matcher matcher, boolean isMainDeck) {
        DeckController.getInstance().removeCardFromDeck(matcher.group("deckName"), matcher.group("cardName"), isMainDeck);
    }

    private void printAllDecks() {
        DeckController.getInstance().printAllDecks();
    }

    private void printDeck(Matcher matcher, boolean isMainDeck) {
        DeckController.getInstance().printDeck(matcher.group("deckName"), isMainDeck);
    }

    private void printAllCards() {
        DeckController.getInstance().printAllCards();
    }

}
