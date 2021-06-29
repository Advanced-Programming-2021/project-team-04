package view;

import controller.MainController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.extern.java.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static view.LoginView.sceneCreator;

public class MainView {

    private static MediaPlayer mainMusic;
    public static boolean isMute;


    @FXML
    public void enterShopMenu() {
        LoginView.stage.setScene(LoginView.shopScene);
        LoginView.stage.centerOnScreen();
        ShopView.run();
    }

    @FXML
    public void enterProfileMenu() {
        LoginView.stage.setScene(LoginView.profileScene);
        LoginView.stage.centerOnScreen();
        ProfileView.run();
    }

    @FXML
    public void enterScoreboard() {
        LoginView.stage.setScene(LoginView.scoreboardScene);
        LoginView.stage.centerOnScreen();
        ScoreboardView.run();
    }

    @FXML
    public void enterDeckMenu() {
        LoginView.deckScene = sceneCreator("DeckView.fxml");
        LoginView.setSize(LoginView.deckScene);
        LoginView.stage.setScene(LoginView.deckScene);
        LoginView.stage.centerOnScreen();
        DeckView.run();
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
            if (!isMute) {
                LoginView.IntroMusic.play();
                mainMusic.pause();
            }
            LoginView.isMute = isMute;
            ((ToggleButton) LoginView.loginScene.lookup("#mute")).setSelected(isMute);
            LoginView.stage.setScene(LoginView.loginScene);
            LoginView.stage.centerOnScreen();
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

    public void mute(MouseEvent mouseEvent) {
        isMute = ((ToggleButton) mouseEvent.getTarget()).isSelected();
        if (isMute) mainMusic.pause();
        else if (mainMusic == null) playMainMusic();
        else mainMusic.play();
    }
}
