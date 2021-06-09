package view;

import controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginView extends Application {
    public static Stage stage;
    public static Scene loginScene;

    public static void run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        LoginView.stage = stage;
        changeScene("SignupView.fxml", "Signup");
        stage.show();
    }

    private static void changeScene(String fileName, String title) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource(fileName));
        Scene scene = new Scene(fxmlLoader.load());
        LoginView.loginScene = scene;
        stage.setScene(scene);
        stage.setTitle(title);
    }

    public void createUser() {
        String username = ((TextField) loginScene.lookup("#username")).getText();
        String nickname = ((TextField) loginScene.lookup("#nickname")).getText();
        String password = ((PasswordField) loginScene.lookup("#password")).getText();
        LoginController.getInstance().createUser(username, password, nickname);
    }

    public void loginScene() throws Exception{
        changeScene("LoginView.fxml", "Login");
    }

    public void signupScene() throws Exception{
        changeScene("SignupView.fxml", "Signup");
    }

    public void login() {
        String username = ((TextField) loginScene.lookup("#username")).getText();
        String password = ((PasswordField) loginScene.lookup("#password")).getText();
        if (LoginController.getInstance().loginUser(username, password)); //TODO main menu
    }

}
