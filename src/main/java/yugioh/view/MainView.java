package yugioh.view;

import yugioh.controller.DuelController;
import yugioh.controller.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import yugioh.model.Game;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static yugioh.view.LoginView.sceneCreator;

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
    public void importAndExport() {
        LoginView.stage.setScene(LoginView.importAndExportScene);
        LoginView.stage.centerOnScreen();
        ImportAndExportView.run();
    }

    @FXML
    public void startNewGame() {
        LoginView.stage.setScene(LoginView.duelFirstScene);
        LoginView.stage.centerOnScreen();
        DuelFirstPage.run();
//        DuelView.getInstance().runForRPS();
//        DuelView.getInstance().run();
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

    @FXML
    public void createCard() {
        CreatorView.start();
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
