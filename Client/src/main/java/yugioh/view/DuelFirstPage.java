package yugioh.view;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import yugioh.controller.DuelController;
import yugioh.controller.MainController;
import yugioh.model.AI;

public class DuelFirstPage {

    public static void run() {
        LoginView.duelFirstScene.lookup("#oneRound").getStyleClass().remove("radio-button");
        LoginView.duelFirstScene.lookup("#threeRounds").getStyleClass().remove("radio-button");
        handleResume();
    }

    public void mainMenu() {
        LoginView.stage.setScene(LoginView.mainScene);
        LoginView.stage.centerOnScreen();
    }

    public void playButton() {
        if (MainController.getInstance().newDuel(((TextField) LoginView.duelFirstScene.lookup("#username")).getText(), ((ToggleButton) LoginView.duelFirstScene.lookup("#oneRound")).isSelected() ? 1 : 3))
            DuelView.coin();
    }

    public void resumeButton() {
        MainView.gameMusic.play();
        DuelView.secondStage.show();
        LoginView.stage.setScene(LoginView.mainGameSceneOne);
        DuelView.run();
    }

    public static void handleResume() {
        Button button = (Button) LoginView.duelFirstScene.lookup("#resume");
        button.setDisable(DuelController.getInstance().getGame() == null);
    }

}
