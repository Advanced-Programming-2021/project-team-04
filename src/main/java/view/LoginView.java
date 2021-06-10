package view;

import controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginView extends Application {
    public static Stage stage;
    public static Scene loginScene;
    public static Scene signUpScene;
    public static MediaPlayer IntroMusic;

    public static void run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        playIntroMusic();
        LoginView.stage = stage;
        setSignupScene();
        stage.show();
    }

    private static void setLoginScene() throws Exception{
        if (loginScene == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("LoginView.fxml"));
            LoginView.loginScene = new Scene(fxmlLoader.load());
        }
        stage.setScene(loginScene);
        stage.setTitle("Log In");
    }

    private static void setSignupScene() throws Exception{
        if (signUpScene == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("SignupView.fxml"));
            LoginView.signUpScene = new Scene(fxmlLoader.load());
        }
        stage.setScene(signUpScene);
        stage.setTitle("Sign Up");
    }

    public void createUser() {
        String username = ((TextField) loginScene.lookup("#username")).getText();
        String nickname = ((TextField) loginScene.lookup("#nickname")).getText();
        String password = ((PasswordField) loginScene.lookup("#password")).getText();
        LoginController.getInstance().createUser(username, password, nickname);
    }

    public void loginScene() throws Exception{
       setLoginScene();
    }

    public void signupScene() throws Exception{
        setSignupScene();
    }

    public void login() throws IOException {
        String username = ((TextField) loginScene.lookup("#username")).getText();
        String password = ((PasswordField) loginScene.lookup("#password")).getText();
        if (LoginController.getInstance().loginUser(username, password)){
            IntroMusic.stop();
            MainView.playMainMusic();
            FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("MainView.fxml"));
            MainView.mainScene = new Scene(fxmlLoader.load());
            stage.setScene(MainView.mainScene);
        }
    }

    public static void playIntroMusic() {
        Media main = new Media(LoginView.class.getResource("TheAuroraStrikes.mp3").toExternalForm());
        IntroMusic = new MediaPlayer(main);
        IntroMusic.setCycleCount(MediaPlayer.INDEFINITE);
        IntroMusic.play();
    }

    public void muteLogin() {
        if (((ToggleButton) loginScene.lookup("#mute")).isSelected()) IntroMusic.stop();
        else IntroMusic.play();
    }

    public void muteSignup() {
        System.out.println("heh");
        if (((ToggleButton) signUpScene.lookup("#mute")).isSelected()) IntroMusic.stop();
        else IntroMusic.play();
    }
}
