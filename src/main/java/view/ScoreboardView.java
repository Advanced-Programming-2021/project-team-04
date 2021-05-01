package view;

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

    }

    @Override
    public void showCurrentMenu() {
        Output.getInstance().printScoreboardMenuName();
    }
}
