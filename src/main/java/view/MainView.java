package view;

import controller.MainController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainView extends Menu {

    private static MainView singleInstance = null;

    private final Pattern enterMenuPattern = Pattern.compile("(?:menu )?enter (?<name>\\S+)");
    private final Pattern newDuelPattern = Pattern.compile("^d(?:uel)? (?=.*(?:\\-(?:(?:\\-new)|(?:n))))" +
            "(?=.*(?:\\-(?:(?:\\-second\\-player)|(?:s)|(?:s\\-p)) (?<secondPlayerUsername>\\S+)))" +
            "(?=.*(?:\\-(?:(?:\\-round(?:s)?)|(?:r)) (?<roundsNumber>\\d+))).+$");
    private final Pattern newDuelAIPattern = Pattern.compile("^d(?:uel)? (?=.*(?:\\-(?:(?:\\-new)|(?:n))))" +
            "(?=.*(?:\\-\\-ai))(?=.*(?:\\-(?:(?:\\-round(?:s)?)|(?:r)) (?<roundsNumber>\\d+))).+$");

    private boolean continueLoop = true;

    private MainView() {
    }

    public static MainView getInstance() {
        if (singleInstance == null)
            singleInstance = new MainView();
        return singleInstance;
    }

    @Override
    public void run() {
        String command;
        continueLoop = true;
        while (continueLoop) {
            continueLoop = !(command = Input.getInputMessage()).matches("(?:menu )?exit");
            Matcher enterMenuMatcher = enterMenuPattern.matcher(command);
            Matcher newDuelMatcher = newDuelPattern.matcher(command);
            Matcher newDuelAIMatcher = newDuelAIPattern.matcher(command);
            if (command.matches("(?:menu )?(?:show|s)\\-(?:current|c)"))
                showCurrentMenu();
            else if (command.matches("(?:user )?logout"))
                logout();
            else if (enterMenuMatcher.matches())
                enterMenu(enterMenuMatcher);
            else if (newDuelMatcher.matches())
                newDuel(newDuelMatcher);
            else if (newDuelAIMatcher.matches())
                newDuelAI();
            else if (command.matches("The Aurora Strikes \\d+"))
                cheatIncreaseScore(command);
            else if (command.matches("The Hanged Man Rusts \\d+"))
                cheatIncreaseMoney(command);
            else Output.getInstance().printInvalidCommand();
        }
    }

    @Override
    public void showCurrentMenu() {
        Output.getInstance().printMainMenuName();
    }

    private void enterMenu(Matcher matcher) {
        String menuName = matcher.group("name");
        if (menuName.matches("(?:L|l)ogin(?: (?:M|m)enu)?"))
            continueLoop = false;
        else if (menuName.matches("(?:S|s)coreboard(?: (?:M|m)enu)?"))
            ScoreboardView.getInstance().run();
        else if (menuName.matches("(?:D|d)uel(?: (?:M|m)enu)?"))
            DuelView.getInstance().run();
        else if (menuName.matches("(?:D|d)eck(?: (?:M|m)enu)?"))
            DeckView.getInstance().run();
        else if (menuName.matches("(?:P|p)rofile(?: (?:M|m)enu)?"))
            ProfileView.getInstance().run();
        else if (menuName.matches("(?:S|s)hop(?: (?:M|m)enu)?"))
            ShopView.getInstance().run();
        else Output.getInstance().printInvalidCommand();
    }

    private void logout() {
        Output.getInstance().printUserLoggedOut();
        continueLoop = false;
    }

    private void newDuel(Matcher matcher) {
        MainController.getInstance().newDuel(matcher.group("secondPlayerUsername"),
                Integer.parseInt(matcher.group("roundsNumber")));
    }

    private void newDuelAI() {
        //TODO call whatever menu we should call after AI is done
    }

    private void cheatIncreaseScore(String string) {
        Pattern pattern = Pattern.compile("The Aurora Strikes (\\d+)");
        Matcher matcher = pattern.matcher(string);
        matcher.find();
        int amount = Integer.parseInt(matcher.group(1));
        MainController.getInstance().cheatIncreaseScore(amount);
    }

    private void cheatIncreaseMoney(String string) {
        Pattern pattern = Pattern.compile("The Hanged Man Rusts (\\d+)");
        Matcher matcher = pattern.matcher(string);
        matcher.find();
        int amount = Integer.parseInt(matcher.group(1));
        MainController.getInstance().cheatIncreaseMoney(amount);
    }
}
