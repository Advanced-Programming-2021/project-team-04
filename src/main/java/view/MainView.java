package view;

import controller.MainController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.extern.java.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainView {

    private static MediaPlayer mainMusic;


    @FXML
    public void enterShopMenu() {
        LoginView.stage.setScene(LoginView.shopScene);
        ShopView.getInstance().run();
    }

    @FXML
    public void enterProfileMenu() {
        LoginView.stage.setScene(LoginView.profileScene);
        ProfileView.getInstance().run();
    }

    @FXML
    public void enterScoreboard() {
        LoginView.stage.setScene(LoginView.scoreboardScene);
        ScoreboardView.run();
    }

    @FXML
    public void enterDeckMenu() {
        LoginView.stage.setScene(LoginView.deckScene);
        DeckView.getInstance().run();
    }

    @FXML
    public void playWithAI() {
        LoginView.stage.setScene(LoginView.duelScene);
        mainMusic.stop();
        DuelView.getInstance().runForRPSAgainstAI();
        DuelView.getInstance().run();
        //TODO change rps and rounds
    }

    @FXML
    public void playWithRival() {
        LoginView.stage.setScene(LoginView.duelScene);
        mainMusic.stop();
        DuelView.getInstance().runForRPS();
        DuelView.getInstance().run();
        //TODO change rps and rounds
    }
    @FXML
    public void logout() {
        MainController.getInstance().setLoggedIn(null);
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
        if (((ToggleButton) LoginView.mainScene.lookup("#mute")).isSelected()) mainMusic.pause();
        else mainMusic.play();
    }
}
