package view;

import controller.MainController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainView {

    public static Scene mainScene;
    private static MediaPlayer mainMusic;
    public static Scene scoreboardScene;


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
    public void enterScoreboard() throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(MainView.class.getResource("ScoreboardView.fxml"));
        scoreboardScene = new Scene(fxmlLoader.load());
        LoginView.stage.setScene(scoreboardScene);
        ScoreboardView.run();
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

    public void mute() {
        if (((ToggleButton) mainScene.lookup("#mute")).isSelected()) mainMusic.pause();
        else mainMusic.play();
    }
}
