package yugioh.view;

import yugioh.controller.DeckController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeckView extends ViewMenu {

    private static DeckView singleInstance = null;

    private final Pattern createDeckPattern = Pattern.compile("(?:deck )?create (?<name>\\S+)");
    private final Pattern deleteDeckPattern = Pattern.compile("(?:deck )?delete (?<name>\\S+)");
    private final Pattern activateDeckPattern = Pattern.compile("(?:deck )?" +
            "(?:set|activate|set-activ(?:at)?e) (?<name>\\S+)");
    private final Pattern addCardToDeckPattern = Pattern.compile("^(?:deck )?a(?:dd)?[- ]c(?:ard)? " +
            "(?=.*-(?:-card|c) (?<cardName>[^-]+))(?=.*-(?:-deck|d) (?<deckName>\\S+)).+$");
    private final Pattern removeCardFromDeckPattern = Pattern.compile("^(?:deck )?rm?[- ]c(?:ard)? " +
            "(?=.*-(?:-card|c) (?<cardName>[^-]+))(?=.*-(?:-deck|d) (?<deckName>\\S+)).+$");
    private final Pattern showDeckPattern = Pattern.compile("^(?:deck )?show " +
            "(?=.*-(?:-deck|d) (?<deckName>\\S+)).+$");

    private DeckView() {}

    public static DeckView getInstance() {
        if (singleInstance == null)
            singleInstance = new DeckView();
        return singleInstance;
    }

    @Override
    public void run() {
        System.out.println("wtf");
        String command;
        while (!(command = IO.getInstance().getInputMessage()).matches("(?:menu )?exit") &&
                !command.matches("(?:menu )?enter [Mm]ain(?: menu)?")) {
            Matcher createDeckMatcher = createDeckPattern.matcher(command);
            Matcher deleteDeckMatcher = deleteDeckPattern.matcher(command);
            Matcher activateDeckMatcher = activateDeckPattern.matcher(command);
            Matcher addCardToDeckMatcher = addCardToDeckPattern.matcher(command);
            Matcher removeCardFromDeckMatcher = removeCardFromDeckPattern.matcher(command);
            Matcher showDeckMatcher = showDeckPattern.matcher(command);
            if (command.matches("(?:menu )?s(?:how)?-c(?:urrent)?"))
                showCurrentMenu();
            else if (command.matches("(?:menu )?enter \\S+"))
                IO.getInstance().printMenuNavigationImpossible();
            else if (command.matches("(?:deck )?show -(?:-all|a)"))
                printAllDecks();
            else if (showDeckMatcher.matches())
                printDeck(showDeckMatcher, !command.contains("-s"));
            else if (command.matches("(?:deck )?show -(?:-cards|c)"))
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
            else IO.getInstance().printInvalidCommand();
        }
    }

    @Override
    public void showCurrentMenu() {
        IO.getInstance().printDeckMenuName();
    }

    private void createDeck(Matcher matcher) {
        DeckController.getInstance().createDeck(matcher.group("name").trim());
    }

    private void deleteDeck(Matcher matcher) {
        DeckController.getInstance().deleteDeck(matcher.group("name").trim());
    }

    private void activateDeck(Matcher matcher) {
        DeckController.getInstance().activateDeck(matcher.group("name").trim());
    }

    private void addCardToDeck(Matcher matcher, boolean isMainDeck) {
        DeckController.getInstance().addCardToDeck(matcher.group("deckName").trim(), matcher.group("cardName").trim(), isMainDeck);
    }

    private void removeCardFromDeck(Matcher matcher, boolean isMainDeck) {
        DeckController.getInstance().removeCardFromDeck(matcher.group("deckName").trim(), matcher.group("cardName").trim(), isMainDeck);
    }

    private void printAllDecks() {
        DeckController.getInstance().printAllDecks();
    }

    private void printDeck(Matcher matcher, boolean isMainDeck) {
        DeckController.getInstance().printDeck(matcher.group("deckName").trim(), isMainDeck);
    }

    private void printAllCards() {
        DeckController.getInstance().printAllCards();
    }

}
