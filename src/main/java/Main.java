import controller.ImportAndExport;
import controller.ShopController;
import model.Account;
import model.Card;
import model.MonsterCard;
import view.LoginView;

import javax.management.monitor.Monitor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        ShopController.getInstance();
        ImportAndExport.getInstance().readAllUsers();
        LoginView.getInstance().run();
        ImportAndExport.getInstance().writeAllUsers();
    }
}
