package controller;

import lombok.Getter;
import model.Account;
import model.Game;

import java.util.HashMap;

public class MainController {

    @Getter
    private static final HashMap<String, Account> loggedInAccounts = new HashMap<>();

    public static Object getThisAccount(String[] command) {
        return loggedInAccounts.get(command[1]);
    }

    public static String newDuel(String[] command) {
        String username = command[4];
        int rounds = Integer.parseInt(command[5]);
        Account loggedIn = loggedInAccounts.get(command[1]);
        String result = errorForNewGame(username, rounds, loggedIn);
        if (result.equals("success"))
            DuelController.setGame(new Game(loggedIn, Account.getAccountByUsername(username), rounds, false));
        return result;
    }

    public static String logout(String[] command) {
        loggedInAccounts.remove(command[1]);
        return "success";
    }


    private static String errorForNewGame(String username, int rounds, Account loggedIn) {
        if (!Account.getAllAccounts().contains(Account.getAccountByUsername(username)) || username.equals(loggedIn.getUsername())) {
            return "there is no player with this username";
        }
        if (rounds != 1 && rounds != 3) {
            return "invalid number of rounds";
        }
        Account player2 = Account.getAccountByUsername(username);
        if (loggedIn.getActiveDeck() == null) {
            return loggedIn.getUsername() + " has no active deck";
        }
        if (player2.getActiveDeck() == null) {
            return username + " has no active deck";
        }
        if (loggedIn.getActiveDeck().isDeckInvalid()) {
            return loggedIn.getUsername() + "'s deck is invalid";
        }
        if (player2.getActiveDeck().isDeckInvalid()) {
            return username + "'s deck is invalid";
        }
        return "success";
    }
}
