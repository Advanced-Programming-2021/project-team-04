package yugioh.view;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import yugioh.controller.DuelController;
import yugioh.controller.MainController;

public class DuelFirstPage {

    public static void run() {
        LoginView.duelFirstScene.lookup("#oneRound").getStyleClass().remove("radio-button");
        LoginView.duelFirstScene.lookup("#threeRounds").getStyleClass().remove("radio-button");
    }

    public void mainMenu() {
        LoginView.stage.setScene(LoginView.mainScene);
        LoginView.stage.centerOnScreen();
    }

    public void playButton() {
        int rounds = ((ToggleButton) LoginView.duelFirstScene.lookup("#oneRound")).isSelected() ? 1 : 3;
        String username = ((TextField) LoginView.duelFirstScene.lookup("#username")).getText();
        if (MainController.getInstance().newDuel(username, rounds))
            DuelView.coin();
    }
}
