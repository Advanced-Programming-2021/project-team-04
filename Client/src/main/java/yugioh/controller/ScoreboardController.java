package yugioh.controller;
import yugioh.utils.Connection;

import java.util.ArrayList;

public class ScoreboardController {

private static ScoreboardController singleInstance = null;

    public static ScoreboardController getInstance() {
        if (singleInstance == null)
            singleInstance = new ScoreboardController();
        return singleInstance;
    }

    public ArrayList<String> getSortedUsers() {
        return (ArrayList<String>) Connection.getObject("ScoreboardController getSortedUsers");
    }
}
