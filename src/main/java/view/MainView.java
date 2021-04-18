package view;
public class MainView {
    private static MainView singleInstance = null;
    private MainView() {

    }
    public static MainView getInstance() {
        if (singleInstance == null)
            singleInstance = new MainView();
        return singleInstance;
    }
    public void run() {

    }
    private void enterMenu(String input) {

    }
    private void showCurrent() {

    }
    private void logout() {

    }
    private void newDuel(String input) {

    }
    private void cheatIncreaseMoney() {

    }
    private void cheatIncreaseScore() {

    }
}
