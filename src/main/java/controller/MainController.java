package controller;

import model.Account;

public class MainController {
    private Account loggedIn;
    private static MainController singleInstance = null;
    private MainController() {

    }
    public static MainController getInstance() {
        if (singleInstance == null)
            singleInstance = new MainController();
        return singleInstance;
    }
    public void run() {

    }
    private void newDuel(String username, int rounds) {

    }
    private void cheatIncreaseMoney() {

    }
    private void cheatIncreaseScore() {

    }
}
