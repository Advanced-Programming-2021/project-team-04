package yugioh;

import yugioh.controller.ImportAndExport;
import yugioh.controller.ShopController;
import yugioh.view.LoginView;

public class Main{
    public static void main(String[] args) {
        ShopController.getInstance();
        ImportAndExport.getInstance().readAllUsers();
        LoginView.run(args);
        ImportAndExport.getInstance().writeAllUsers();
    }
}
