package view;

import controller.MainController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainView extends ViewMenu {

    private static MainView singleInstance = null;

    private final Pattern enterMenuPattern = Pattern.compile("(?:menu )?enter (?<name>\\S+)");
    private final Pattern newDuelPattern = Pattern.compile("^d(?:uel)? (?=.*(?:-(?:(?:-new)|(?:n))))" +
            "(?=.*(?:-(?:(?:-second-player)|(?:s)|(?:s-p)) (?<secondPlayerUsername>\\S+)))" +
            "(?=.*(?:-(?:(?:-round(?:s)?)|(?:r)) (?<roundsNumber>\\d+))).+$");
    private final Pattern newDuelAIPattern = Pattern.compile("^d(?:uel)? (?=.*(?:-(?:(?:-new)|(?:n))))" +
            "(?=.*(?:--ai))(?=.*(?:-(?:(?:-round(?:s)?)|(?:r)) (?<roundsNumber>\\d+))).+$");

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
            continueLoop = !(command = IO.getInstance().getInputMessage()).matches("(?:menu )?exit");
            Matcher enterMenuMatcher = enterMenuPattern.matcher(command);
            Matcher newDuelMatcher = newDuelPattern.matcher(command);
            Matcher newDuelAIMatcher = newDuelAIPattern.matcher(command);
            if (command.matches("(?:menu )?(?:s(?:how)?)-(?:c(?:urrent)?)"))
                showCurrentMenu();
            else if (command.matches("(?:user )?logout")) {
                logout();
                return;
            }else if (enterMenuMatcher.matches())
                enterMenu(enterMenuMatcher);
            else if (newDuelMatcher.matches())
                newDuel(newDuelMatcher);
            else if (newDuelAIMatcher.matches())
                newDuelAI(newDuelAIMatcher);
            else if (command.matches("The Aurora Strikes \\d+"))
                cheatIncreaseScore(command);
            else if (command.matches("The Hanged Man Rusts \\d+"))
                cheatIncreaseMoney(command);
            else IO.getInstance().printInvalidCommand();
        }
    }

    @Override
    public void showCurrentMenu() {
        IO.getInstance().printMainMenuName();
    }

    private void enterMenu(Matcher matcher) {
        String menuName = matcher.group("name");
        if (menuName.matches("[Ll]ogin(?: [Mm]enu)?"))
            continueLoop = false;
        else if (menuName.matches("[Ss]coreboard(?: [Mm]enu)?"))
            ScoreboardView.getInstance().run();
        else if (menuName.matches("[Dd]uel(?: [Mm]enu)?"))
            DuelView.getInstance().run();
        else if (menuName.matches("[Dd]eck(?: [Mm]enu)?"))
            DeckView.getInstance().run();
        else if (menuName.matches("[Pp]rofile(?: [Mm]enu)?"))
            ProfileView.getInstance().run();
        else if (menuName.matches("[Ss]hop(?: [Mm]enu)?"))
            ShopView.getInstance().run();
        else IO.getInstance().printInvalidCommand();
    }

    private void logout() {
        MainController.getInstance().setLoggedIn(null);
        IO.getInstance().printUserLoggedOut();
        continueLoop = false;
    }

    private void newDuel(Matcher matcher) {
        if (MainController.getInstance().newDuel(matcher.group("secondPlayerUsername"),
                Integer.parseInt(matcher.group("roundsNumber")))) {
            DuelView.getInstance().runForRPS();
            DuelView.getInstance().run();
        }
    }

    private void newDuelAI(Matcher matcher) {
        if (MainController.getInstance().newAIDuel(Integer.parseInt(matcher.group("roundsNumber")))) {
            DuelView.getInstance().runForRPSAgainstAI();
            DuelView.getInstance().run();
        }
    }

    //TODO change the methods below

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
