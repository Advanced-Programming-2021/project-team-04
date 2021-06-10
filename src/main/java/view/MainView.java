package view;

import controller.MainController;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainView extends ViewMenu {

    public static Scene mainScene;
    private static MediaPlayer mainMusic;

//    private final Pattern enterMenuPattern = Pattern.compile("(?:menu )?enter (?<name>\\S+)");
//    private final Pattern newDuelPattern = Pattern.compile("^d(?:uel)? (?=.*-(?:-new|n))" +
//            "(?=.*-(?:-second-player|s|s-p) (?<secondPlayerUsername>\\S+))" +
//            "(?=.*-(?:-rounds?|r) (?<roundsNumber>\\d+)).+$");
//    private final Pattern newDuelAIPattern = Pattern.compile("^d(?:uel)? (?=.*-(?:-new|n))" +
//            "(?=.*--ai)(?=.*-(?:-rounds?|r) (?<roundsNumber>\\d+)).+$");

    @Override
    public void run() {
//        String command;
//        continueLoop = true;
//        while (continueLoop) {
//            continueLoop = !(command = IO.getInstance().getInputMessage()).matches("(?:menu )?exit");
//            Matcher enterMenuMatcher = enterMenuPattern.matcher(command);
//            Matcher newDuelMatcher = newDuelPattern.matcher(command);
//            Matcher newDuelAIMatcher = newDuelAIPattern.matcher(command);
//            if (command.matches("(?:menu )?s(?:how)?-c(?:urrent)?"))
//                showCurrentMenu();
//            else if (command.matches("(?:user )?logout")) {
//                logout();
//                return;
//            }else if (enterMenuMatcher.matches())
//                enterMenu(enterMenuMatcher);
//            else if (newDuelMatcher.matches())
//                newDuel(newDuelMatcher);
//            else if (newDuelAIMatcher.matches())
//                newDuelAI(newDuelAIMatcher);
//            else if (command.matches("The Aurora Strikes \\d+"))
//                cheatIncreaseScore(command);
//            else if (command.matches("The Hanged Man Rusts \\d+"))
//                cheatIncreaseMoney(command);
//            else IO.getInstance().printInvalidCommand();
        }


    @Override
    public void showCurrentMenu() {
        IO.getInstance().printMainMenuName();
    }

//    public void enterMenu(Matcher matcher) {
//        String menuName = matcher.group("name");
//        if (menuName.matches("[Ll]ogin(?: [Mm]enu)?"))
//            continueLoop = false;
//        else if (menuName.matches("[Ss]coreboard(?: [Mm]enu)?"))
//            ScoreboardView.getInstance().run();
//        else if (menuName.matches("[Dd]uel(?: [Mm]enu)?"))
//            DuelView.getInstance().run();
//        else if (menuName.matches("[Dd]eck(?: [Mm]enu)?"))
//            DeckView.getInstance().run();
//        else if (menuName.matches("[Pp]rofile(?: [Mm]enu)?"))
//            ProfileView.getInstance().run();
//        else if (menuName.matches("[Ss]hop(?: [Mm]enu)?"))
//            ShopView.getInstance().run();
//        else IO.getInstance().printInvalidCommand();
//    }

    @FXML
    public void enterShopMenu() {
        ShopView.getInstance().run();
        //TODO change scene
    }

    @FXML
    public void enterProfileMenu() {
        ProfileView.getInstance().run();
        //TODO change scene
    }

    @FXML
    public void enterScoreboard() {
        ScoreboardView.getInstance().run();
        //TODO change scene
    }

    @FXML
    public void enterDeckMenu() {
        DeckView.getInstance().run();
        //TODO change scene
    }

    @FXML
    public void playWithAI() {
        mainMusic.stop();
        DuelView.getInstance().runForRPSAgainstAI();
        DuelView.getInstance().run();
        //TODO change scene and rps and rounds
    }

    @FXML
    public void playWithRival() {
        mainMusic.stop();
        DuelView.getInstance().runForRPS();
        DuelView.getInstance().run();
        //TODO change scene and rps and rounds
    }
    @FXML
    public void logout() {
        MainController.getInstance().setLoggedIn(null);
        IO.getInstance().printUserLoggedOut();
        try {
            mainMusic.stop();
            LoginView.IntroMusic.play();
            LoginView.stage.setScene(LoginView.loginScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void newDuel(Matcher matcher) {
//        if (MainController.getInstance().newDuel(matcher.group("secondPlayerUsername"),
//                Integer.parseInt(matcher.group("roundsNumber")))) {
//            DuelView.getInstance().runForRPS();
//            DuelView.getInstance().run();
//        }
//    }
//
//    private void newDuelAI(Matcher matcher) {
//        if (MainController.getInstance().newAIDuel(Integer.parseInt(matcher.group("roundsNumber")))) {
//            DuelView.getInstance().runForRPSAgainstAI();
//            DuelView.getInstance().run();
//        }
//    }

    //TODO change the methods below

    private void cheatIncreaseScore(String string) {
        Pattern pattern = Pattern.compile("The Aurora Strikes (\\d+)");
        Matcher matcher = pattern.matcher(string);
        matcher.find();
        int amount = Integer.parseInt(matcher.group(1));
        MainController.getInstance().cheatIncreaseScore(amount);
    }

    private void cheatIncreaseMoney(String string) {
        Pattern pattern = Pattern.compile("The Hanged Man Rusts (\\d+)");
        Matcher matcher = pattern.matcher(string);
        matcher.find();
        int amount = Integer.parseInt(matcher.group(1));
        MainController.getInstance().cheatIncreaseMoney(amount);
    }

    public static void playMainMusic() {
        Media main = new Media(MainView.class.getResource("OurBoyJack.mp3").toExternalForm());
        mainMusic = new MediaPlayer(main);
        mainMusic.setCycleCount(MediaPlayer.INDEFINITE);
        mainMusic.play();
    }

}
