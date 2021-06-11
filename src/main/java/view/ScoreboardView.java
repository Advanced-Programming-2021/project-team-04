package view;

import controller.LoginController;
import controller.MainController;
import controller.ScoreboardController;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class ScoreboardView{

    public static void run() {
        VBox vBox = (VBox)  LoginView.scoreboardScene.lookup("#scoreboard");
        ArrayList<String> sortedUsers = ScoreboardController.getInstance().getSortedUsers();
        int min = Math.min(sortedUsers.size(), 20);
        for (int i = 0; i < min; i++) {
            String thisLine = sortedUsers.get(i);
            Label label = new Label(thisLine);
            if (thisLine.contains(MainController.getInstance().getLoggedIn().getNickname()))
                label.setStyle("-fx-text-fill: #FF00C8");
            else
                label.setStyle("-fx-text-fill: #00F2FF");
            vBox.getChildren().add(label);
        }
    }

    public void backButton() {
        LoginView.stage.setScene(LoginView.mainScene);
    }

}
