package controller;

import model.Account;
import model.Game;
import view.DuelView;
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
            DuelView.getInstance().runForRPS();
            DuelView.getInstance().run();
        }
    }

    private boolean errorForNewGame(String username, int rounds) {
        if (!Account.getAllAccounts().contains(Account.getAccountByUsername(username))) {
            Output.getInstance().playerDoesntExist();
            return false;
        }
        Account player2 = Account.getAccountByUsername(username);
        if (loggedIn.getActiveDeck() == null) {
            Output.getInstance().noActiveDeck(loggedIn.getUsername());
            return false;
        }
        if (player2.getActiveDeck() == null) {
            Output.getInstance().noActiveDeck(player2.getUsername());
            return false;
        }
        if (loggedIn.getActiveDeck().isDeckValid()) {
            Output.getInstance().invalidDeck(loggedIn.getUsername());
            return false;
        }
        if (player2.getActiveDeck().isDeckValid()) {
            Output.getInstance().invalidDeck(player2.getUsername());
            return false;
        }
        if (rounds != 1 && rounds != 3) {
            Output.getInstance().invalidNumOfRounds();
            return false;
        }
        return true;
    }

    public void cheatIncreaseMoney(int amount) {
        loggedIn.setMoney(loggedIn.getMoney() + amount);
        Output.getInstance().cheatIncreaseMoney();
    }

    public void cheatIncreaseScore(int amount) {
        loggedIn.setScore(loggedIn.getScore() + amount);
        Output.getInstance().cheatIncreaseScore();
    }
}
