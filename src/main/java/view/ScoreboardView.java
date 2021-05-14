package view;

import controller.ScoreboardController;

public class ScoreboardView extends Menu {
    private static ScoreboardView singleInstance = null;

    private ScoreboardView() {

    }

    public static ScoreboardView getInstance() {
        if (singleInstance == null)
            singleInstance = new ScoreboardView();
        return singleInstance;
    }

    @Override
    public void run() {
        String command;
        while (!(command = Input.getInputMessage()).matches("(?:menu )?exit") && !command.matches("(?:menu )?enter (?:M|m)ain(?: (?:M|m)enu)?")) {
            if (command.matches("(?:menu )?(?:show|s)\\-(?:current|c)"))
                showCurrentMenu();
            else if (command.matches("(?:scoreboard )?show"))
                showScoreboard();
            else if (command.matches("(?:menu )?enter \\S+"))
                Output.getInstance().printMenuNavigationImpossible();
            else Output.getInstance().printInvalidCommand();
        }
    }

    @Override
    public void showCurrentMenu() {
        Output.getInstance().printScoreboardMenuName();
    }

    private void showScoreboard() {
        ScoreboardController.getInstance().run();
    }

}
