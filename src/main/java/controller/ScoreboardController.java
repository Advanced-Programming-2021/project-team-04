package controller;

public class ScoreboardController {

private static ScoreboardController singleInstance = null;
    private ScoreboardController() {

    }
    public static ScoreboardController getInstance() {
        if (singleInstance == null)
            singleInstance = new ScoreboardController();
        return singleInstance;
    }
    public void run() {

    }
}
