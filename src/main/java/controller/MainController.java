package controller;

import model.AI;
import model.Account;
import model.Game;
import view.IO;

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

    public boolean newDuel(String username, int rounds) {
        if (errorForNewGame(username, rounds)) {
            DuelController.getInstance().setGame(new Game(loggedIn, Account.getAccountByUsername(username), rounds, false));
            return true;
        }
        return false;
    }

    public boolean newAIDuel(int rounds) {
        if (errorForNewAIGame(rounds)) {
            new Game(loggedIn, AI.getInstance(), rounds, true);
            return true;
        }
        return false;
    }

    private boolean errorForNewGame(String username, int rounds) {
        if (!Account.getAllAccounts().contains(Account.getAccountByUsername(username))) {
            IO.getInstance().playerDoesntExist();
            return false;
        }
        Account player2 = Account.getAccountByUsername(username);
        if (loggedIn.getActiveDeck() == null) {
            IO.getInstance().noActiveDeck(loggedIn.getUsername());
            return false;
        }
        if (player2.getActiveDeck() == null) {
            IO.getInstance().noActiveDeck(player2.getUsername());
            return false;
        }
        if (!loggedIn.getActiveDeck().isDeckValid()) {
            IO.getInstance().invalidDeck(loggedIn.getUsername());
            return false;
        }
        if (!player2.getActiveDeck().isDeckValid()) {
            IO.getInstance().invalidDeck(player2.getUsername());
            return false;
        }
        if (rounds != 1 && rounds != 3) {
            IO.getInstance().invalidNumOfRounds();
            return false;
        }
        return true;
    }

    private boolean errorForNewAIGame(int rounds) {
        if (loggedIn.getActiveDeck() == null) {
            IO.getInstance().noActiveDeck(loggedIn.getUsername());
            return false;
        }
        if (!loggedIn.getActiveDeck().isDeckValid()) {
            IO.getInstance().invalidDeck(loggedIn.getUsername());
            return false;
        }
        if (rounds != 1 && rounds != 3) {
            IO.getInstance().invalidNumOfRounds();
            return false;
        }
        return true;
    }

    public void cheatIncreaseMoney(int amount) {
        loggedIn.setMoney(loggedIn.getMoney() + amount);
        IO.getInstance().cheatIncreaseMoney();
    }

    public void cheatIncreaseScore(int amount) {
        loggedIn.setScore(loggedIn.getScore() + amount);
        IO.getInstance().cheatIncreaseScore();
    }
}
