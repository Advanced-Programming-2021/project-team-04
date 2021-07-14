package controller;

import model.Account;

import java.util.ArrayList;

public class ScoreboardController {

    public static ArrayList<String> getSortedUsers() {
        sortAccounts();
        ArrayList<String> sortedUsers = new ArrayList<>();
        var count = 0;
        var countForEquals = 0;
        var previousScore = -1;
        for (Account account : Account.getAllAccounts()) {
            count++;
            if (previousScore != account.getScore()) {
                countForEquals = count;
                sortedUsers.add(count + ". " + account.getNickname() + " ) " + account.getScore());
            }
            else {
                sortedUsers.add(countForEquals + ". " + account.getNickname() + " ) " + account.getScore());
            }
            previousScore = account.getScore();
        }
        return sortedUsers;
    }

    private static void sortAccounts() {
        Account.getAllAccounts().sort((o1, o2) -> {
            int compared = -Integer.compare(o1.getScore(), o2.getScore());
            if (compared == 0) compared = o1.getNickname().compareTo(o2.getNickname());
            return compared;
        });
    }
}
