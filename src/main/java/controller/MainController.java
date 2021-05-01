package controller;

import model.Account;
import model.Game;
import view.Output;

public class MainController {
    private Account loggedIn;
    private static MainController singleInstance = null;

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

    public void newDuel(String username, int rounds) {
        if (errorForNewGame(username, rounds)) {
            new Game(loggedIn, Account.getAccountByUsername(username), rounds);
            Output.getForNow();
            view.MainView.getInstance().run();
        }
    }

    private boolean errorForNewGame(String username, int rounds) {
        if (!Account.getAllAccounts().contains(Account.getAccountByUsername(username))) {
            Output.getForNow();
            return false;
        } else if (rounds != 1 && rounds != 3) {
            Output.getForNow();
            return false;
        }
        return true;
    }

    public void cheatIncreaseMoney(int amount) {
        loggedIn.setMoney(loggedIn.getMoney() + amount);
    }

    public void cheatIncreaseScore(int amount) {
        loggedIn.setScore(loggedIn.getScore() + amount);
    }
}
