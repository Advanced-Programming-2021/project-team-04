package yugioh.view;

import yugioh.controller.ScoreboardController;

public class ScoreboardView extends ViewMenu {
    private static ScoreboardView singleInstance = null;

    private ScoreboardView() { }

    public static ScoreboardView getInstance() {
        if (singleInstance == null)
            singleInstance = new ScoreboardView();
        return singleInstance;
    }

    @Override
    public void run() {
        String command;
        while (!(command = IO.getInstance().getInputMessage()).matches("(?:menu )?exit") &&
                !command.matches("(?:menu )?enter [Mm]ain(?: menu)?")) {
            if (command.matches("(?:menu )?s(?:how)?-c(?:urrent)?"))
                showCurrentMenu();
            else if (command.matches("(?:scoreboard )?show"))
                showScoreboard();
            else if (command.matches("(?:menu )?enter \\S+"))
                IO.getInstance().printMenuNavigationImpossible();
            else IO.getInstance().printInvalidCommand();
        }
    }

    @Override
    public void showCurrentMenu() {
        IO.getInstance().printScoreboardMenuName();
    }

    private void showScoreboard() {
        ScoreboardController.getInstance().run();
    }

}
