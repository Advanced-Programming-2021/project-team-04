package yugioh.controller;


import lombok.Getter;
import lombok.Setter;
import yugioh.model.AI;
import yugioh.model.Account;
import yugioh.model.Game;
import yugioh.utils.Connection;
import yugioh.view.IO;


@Getter
@Setter
public class MainController {


    private static MainController singleInstance = null;

    public static MainController getInstance() {
        if (singleInstance == null)
            singleInstance = new MainController();
        return singleInstance;
    }

    public Account getLoggedIn() {
        return (Account) Connection.getObject("MainController getThisAccount");
    }


    public boolean newDuel(String username, int rounds) {
        String result = Connection.getResult("MainController newDuel" + " " + username + " " + rounds);
        if (result.equals("success")) return true;
        IO.getInstance().showMessage(result);
        return false;
    }

    public void logout() {
        Connection.getResult("MainController logout");
        Connection.setToken("-");
    }


//    public boolean newAIDuel(int rounds, AI.AIDifficulty difficulty) {
//        if (errorForNewAIGame(rounds)) {
//            AI.getInstance().setActivePlayerDeck(difficulty.toString());
//            DuelController.getInstance().setGame(new Game(loggedIn, AI.getInstance(), rounds, true));
//            return true;
//        }
//        return false;
//    }


//    private boolean errorForNewAIGame(int rounds) {
//        if (loggedIn.getActiveDeck() == null) {
//            IO.getInstance().noActiveDeck(loggedIn.getUsername());
//            return false;
//        }
//        if (loggedIn.getActiveDeck().isDeckInvalid()) {
//            IO.getInstance().invalidDeck(loggedIn.getUsername());
//            return false;
//        }
//        return rounds == 1 || rounds == 3;
//    }
}