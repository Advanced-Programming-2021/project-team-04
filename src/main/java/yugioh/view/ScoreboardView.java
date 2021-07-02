package yugioh.view;

import yugioh.controller.MainController;
import yugioh.controller.ScoreboardController;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class ScoreboardView{

    private static ArrayList<String> congrats = new ArrayList<>();

    static {
        congrats.add("Io vengo dalla luna");
        congrats.add("Tieniti la terra uomo, io voglio la luna");
        congrats.add("Ora ho la forza necessaria per rinascere");
        congrats.add("trovo inopportuna la paura");
        congrats.add("La sua follia perversa");
        congrats.add("Che quando mi vede sterza");
        congrats.add("Adesso vi guardo dal cielo perchè sì ho imparato a volare");
        congrats.add("E piscio sul tuo show che fila liscio come il Truman");
        congrats.add("La verità nella tua mentalità è che la fiction sia meglio della vita reale");
        congrats.add("Voglio entrare nei cuori, nei cuori di questa gente");
        congrats.add("Ci scrivo le parole");
        congrats.add("tu che lo fai per il dinero io per diventare immortale");
        congrats.add("Lasciami volare via");
        congrats.add("Io voglio rubare la libertà perchè me la son vista portare via");
        congrats.add("Ho solo il piano A+ non ho un piano B");
        congrats.add("sto puntando in cima siamo missili");
        congrats.add("se mi senti è perché lo so fare");
        congrats.add("sono l'unico fiore che sboccia anche senza aspettare questa primavera");
        congrats.add("sono ancora in piedi nonostante i segni che mi hanno lasciato sulla schiena");
        congrats.add("Se sono sere nere mi sentirò bene sotto la luna piena");
    }

    public static void run() {
        VBox vBox = (VBox)  LoginView.scoreboardScene.lookup("#scoreboard");
        vBox.getChildren().clear();
        ArrayList<String> sortedUsers = ScoreboardController.getInstance().getSortedUsers();
        int min = Math.min(sortedUsers.size(), 20);
        for (int i = 0; i < min; i++) {
            String thisLine = sortedUsers.get(i) + " : " + congrats.get(i);
            Label label = new Label(thisLine);
            if (sortedUsers.get(i).matches("\\d+\\. " + MainController.getInstance().getLoggedIn().getNickname() + " \\) \\d+"))
                label.setStyle("-fx-text-fill: #ff00c8");
            else
                label.setStyle("-fx-text-fill: #00F2FF");
            vBox.getChildren().add(label);
        }
    }

    public void mainMenu() {
        LoginView.stage.setScene(LoginView.mainScene);
        LoginView.stage.centerOnScreen();
    }
}
