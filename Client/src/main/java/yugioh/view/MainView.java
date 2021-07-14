package yugioh.view;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import yugioh.controller.MainController;

import java.util.Objects;

import static yugioh.view.LoginView.sceneCreator;

public class MainView {

    public static MediaPlayer mainMusic;
    public static MediaPlayer gameMusic;
    public static MediaPlayer attack;
    public static MediaPlayer gameFinished;
    public static MediaPlayer spell;
    public static MediaPlayer monster;
    public static boolean isMute;
    public static boolean isGameMute;

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
        assert LoginView.deckScene != null;
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
    public void enterChatRoom() {
        ChatView.run();
    }

    @FXML
    public void startNewGame() {
        LoginView.stage.setScene(LoginView.duelFirstScene);
        LoginView.stage.centerOnScreen();
        playGameMusic();
        DuelFirstPage.run();
    }

    @FXML
    public void logout() {
        MainController.getInstance().logout();
        try {
            if (!isMute) {
                LoginView.introMusic.play();
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


    public static void playMainMusic() {
        Media main = new Media(Objects.requireNonNull(MainView.class.getResource("OurBoyJack.mp3")).toExternalForm());
        mainMusic = new MediaPlayer(main);
        mainMusic.setCycleCount(MediaPlayer.INDEFINITE);
        mainMusic.play();
    }

    public static void playGameMusic() {
        if (mainMusic != null) mainMusic.pause();
        Media main = new Media(Objects.requireNonNull(MainView.class.getResource("TheAuroraStrikes.mp3")).toExternalForm());
        gameMusic = new MediaPlayer(main);
        gameMusic.setCycleCount(MediaPlayer.INDEFINITE);
        gameMusic.play();
    }

    public void mute(MouseEvent mouseEvent) {
        isMute = ((ToggleButton) mouseEvent.getTarget()).isSelected();
        if (isMute) mainMusic.pause();
        else if (mainMusic == null) playMainMusic();
        else mainMusic.play();
    }

    public static void playAttackSong() {
        Media main = new Media(Objects.requireNonNull(MainView.class.getResource("attack.mp3")).toExternalForm());
        attack = new MediaPlayer(main);
        attack.setCycleCount(1);
        attack.play();
    }

    public static void playSpellSong() {
        Media main = new Media(Objects.requireNonNull(MainView.class.getResource("SetSpell.mp3")).toExternalForm());
        spell = new MediaPlayer(main);
        spell.setCycleCount(1);
        spell.play();
    }

    public static void gameFinishedSong() {
        Media main = new Media(Objects.requireNonNull(MainView.class.getResource("GameFinished.mp3")).toExternalForm());
        gameFinished = new MediaPlayer(main);
        gameFinished.setCycleCount(1);
        gameFinished.play();
    }

    public static void monsterSong() {
        Media main = new Media(Objects.requireNonNull(MainView.class.getResource("monster.mp3")).toExternalForm());
        monster = new MediaPlayer(main);
        monster.setCycleCount(1);
        monster.play();
    }

}
