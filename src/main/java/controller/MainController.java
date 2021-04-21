package controller;

import model.Account;

public class MainController {
    private Account loggedIn;
    private static MainController singleInstance = null;
    private MainController() {
        getInstance();
    }

    public Account getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Account loggedIn) {
        this.loggedIn = loggedIn;
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
    private void cheatIncreaseMoney(int amount) {
        loggedIn.setMoney(loggedIn.getMoney() + amount);
    }
    private void cheatIncreaseScore(int amount) {
        loggedIn.setScore(loggedIn.getScore() + amount);
    }
}
