package yugioh.view;

import yugioh.controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginView extends Application {
    public static Stage stage;
    public static Scene loginScene;
    public static Scene signUpScene;
    public static Scene mainScene;
    public static Scene scoreboardScene;
    public static Scene deckScene;
    public static Scene duelFirstScene;
    public static Scene shopScene;
    public static Scene importAndExportScene;
    public static Scene profileScene;
    public static Scene importScene;
    public static Scene coinScene;
    public static Scene graveyardScene;
    public static Scene mainGameSceneOne;
    public static Scene mainGameSceneTwo;
    public static Scene finishGame;
    public static Scene choosingScene;
    public static MediaPlayer IntroMusic;
    public static boolean isMute;

    public static void run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        createAllScenes();
        playIntroMusic();
        LoginView.stage = stage;
        stage.setTitle("YO GI OH");
        stage.setScene(signUpScene);
        stage.show();
        stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
        stage.centerOnScreen();
    }

    public static void setSize(Scene scene) {
        double height = Screen.getPrimary().getVisualBounds().getHeight() - 20;
        ((GridPane) scene.getRoot()).setPrefHeight(height);
        ((GridPane) scene.getRoot()).setPrefWidth(2224 * height / 1424);
    }

    private static void createAllScenes() {
        loginScene = sceneCreator("LoginView.fxml");
        signUpScene = sceneCreator("SignupView.fxml");
        mainScene = sceneCreator("MainView.fxml");
        profileScene = sceneCreator("ProfileView.fxml");
        scoreboardScene = sceneCreator("ScoreboardView.fxml");
        shopScene = sceneCreator("ShopView.fxml");
        duelFirstScene = sceneCreator("DuelFirstPage.fxml");
        importAndExportScene = sceneCreator("ImportAndExportView.fxml");
        importScene = sceneCreator("ImportView.fxml");
        coinScene = sceneCreator("CoinPage.fxml");
        graveyardScene = sceneCreator("Graveyard.fxml");
        mainGameSceneOne = sceneCreator("MainGame.fxml");
        mainGameSceneTwo = sceneCreator("MainGame.fxml");
        finishGame = sceneCreator("FinishGame.fxml");
        choosingScene = sceneCreator("choosing.fxml");
    }

    public static Scene sceneCreator(String resource) {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource(resource));
        try {
            Scene scene = new Scene(fxmlLoader.load());
            setSize(scene);
            return scene;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createUser() {
        TextField username = ((TextField) signUpScene.lookup("#username"));
        TextField nickname = ((TextField) signUpScene.lookup("#nickname"));
        PasswordField password = ((PasswordField) signUpScene.lookup("#password"));
        LoginController.getInstance().createUser(username.getText(), password.getText(), nickname.getText());
        username.clear();
        nickname.clear();
        password.clear();
    }

    public void loginScene() {
       stage.setScene(loginScene);
       stage.centerOnScreen();
       if (isMute) ((ToggleButton) loginScene.lookup("#mute")).setSelected(true);
    }

    public void signupScene() {
        stage.setScene(signUpScene);
        stage.centerOnScreen();
        if (isMute) ((ToggleButton) signUpScene.lookup("#mute")).setSelected(true);
    }

    public void login() {
        String username = ((TextField) loginScene.lookup("#username")).getText();
        String password = ((PasswordField) loginScene.lookup("#password")).getText();
        if (LoginController.getInstance().loginUser(username, password)){
            IntroMusic.pause();
            if (!isMute) MainView.playMainMusic();
            ((ToggleButton) mainScene.lookup("#mute")).setSelected(isMute);
            MainView.isMute = isMute;
            stage.setScene(mainScene);
            stage.centerOnScreen();
        }
    }

    public static void playIntroMusic() {
        Media main = new Media(LoginView.class.getResource("TheAuroraStrikes.mp3").toExternalForm());
        IntroMusic = new MediaPlayer(main);
        IntroMusic.setCycleCount(MediaPlayer.INDEFINITE);
        IntroMusic.play();
    }

    public void mute(MouseEvent mouseEvent) {
        isMute = ((ToggleButton) mouseEvent.getTarget()).isSelected();
        if (isMute) IntroMusic.pause();
        else IntroMusic.play();
    }
}
