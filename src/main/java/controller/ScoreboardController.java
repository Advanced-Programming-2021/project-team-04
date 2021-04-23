package controller;

import model.Account;
import view.Output;
import java.util.Collections;
import java.util.Comparator;

public class ScoreboardController {

private static ScoreboardController singleInstance = null;

    public static ScoreboardController getInstance() {
        if (singleInstance == null)
            singleInstance = new ScoreboardController();
        return singleInstance;
    }
    public void run() {
        sortedAccounts();
        String sorted = "";
        int count = 0;
        int countForEquals = 0;
        int previousScore = -1;
        for (Account account : Account.getAllAccounts()) {
            count++;
            if (previousScore != account.getScore()) {
                countForEquals = count;
                sorted += count + "- " + account.getNickname() + ": " + account.getScore() + "\n";
            }
            else{
                sorted += countForEquals + "- " + account.getNickname() + ": " + account.getScore() + "\n";
            }
            previousScore = account.getScore();
        }
        Output.getForNow();
    }

    private void sortedAccounts() {
        Collections.sort(Account.getAllAccounts(), new Comparator<Account>() {
            @Override
            public int compare(Account o1, Account o2) {
                int compared = -Integer.compare(o1.getScore(), o2.getScore());
                if (compared == 0) compared = o1.getNickname().compareTo(o2.getNickname());
                return compared;
            }
        });
    }
}
