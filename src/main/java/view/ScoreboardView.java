package view;

public class ScoreboardView {
    private static ScoreboardView singleInstance = null;
    private ScoreboardView() {

    }
    public static ScoreboardView getInstance() {
        if (singleInstance == null)
            singleInstance = new ScoreboardView();
        return singleInstance;
    }
    public void run() {

    }
    private void showCurrent() {

    }
}
