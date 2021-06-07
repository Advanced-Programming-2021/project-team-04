import controller.ImportAndExport;
import controller.ShopController;
import model.Account;
import model.Card;
import model.MonsterCard;
import view.LoginView;

import javax.management.monitor.Monitor;

public class Main {
    public static void main(String[] args) {
        ShopController.getInstance();
        ImportAndExport.getInstance().readAllUsers();
        LoginView.getInstance().run();
        ImportAndExport.getInstance().writeAllUsers();
    }
}
