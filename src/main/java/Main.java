import controller.ImportAndExport;
import controller.ShopController;
import model.Card;
import view.LoginView;

public class Main {
    public static void main(String[] args) {
        ShopController.getInstance();
        ImportAndExport.getInstance().readAllUsers();
        LoginView.getInstance().run();
        ImportAndExport.getInstance().writeAllUsers();
    }
}
