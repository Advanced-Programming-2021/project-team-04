package yugioh.controller;

import yugioh.model.Account;
import yugioh.view.IO;

public class ScoreboardController {

private static ScoreboardController singleInstance = null;

    public static ScoreboardController getInstance() {
        if (singleInstance == null)
            singleInstance = new ScoreboardController();
        return singleInstance;
    }
    public void run() {
        sortAccounts();
        StringBuilder sorted = new StringBuilder();
        int count = 0;
        int countForEquals = 0;
        int previousScore = -1;
        for (Account account : Account.getAllAccounts()) {
            count++;
            if (previousScore != account.getScore()) {
                countForEquals = count;
                sorted.append(count).append("- ").append(account.getNickname()).append(": ").append(account.getScore()).append("\n");
            }
            else{
                sorted.append(countForEquals).append("- ").append(account.getNickname()).append(": ").append(account.getScore()).append("\n");
            }
            previousScore = account.getScore();
        }
        IO.getInstance().printString(sorted.toString());
    }

    private void sortAccounts() {
        Account.getAllAccounts().sort((o1, o2) -> {
            int compared = -Integer.compare(o1.getScore(), o2.getScore());
            if (compared == 0) compared = o1.getNickname().compareTo(o2.getNickname());
            return compared;
        });
    }
}
