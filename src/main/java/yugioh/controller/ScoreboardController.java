package yugioh.controller;

import yugioh.model.Account;

import java.util.ArrayList;

public class ScoreboardController {

private static ScoreboardController singleInstance = null;

    public static ScoreboardController getInstance() {
        if (singleInstance == null)
            singleInstance = new ScoreboardController();
        return singleInstance;
    }

    public ArrayList<String> getSortedUsers() {
        sortedAccounts();
        ArrayList<String> sortedUsers = new ArrayList<>();
        int count = 0;
        int countForEquals = 0;
        int previousScore = -1;
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

    private void sortedAccounts() {
        Account.getAllAccounts().sort((o1, o2) -> {
            int compared = -Integer.compare(o1.getScore(), o2.getScore());
            if (compared == 0) compared = o1.getNickname().compareTo(o2.getNickname());
            return compared;
        });
    }
}
