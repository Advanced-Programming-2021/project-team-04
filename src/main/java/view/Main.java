package view;

import controller.ImportAndExport;
import controller.ShopController;
import view.LoginView;

public class Main{
    public static void main(String[] args) {
        ShopController.getInstance();
        ImportAndExport.getInstance().readAllUsers();
        LoginView.run(args);
        ImportAndExport.getInstance().writeAllUsers();
    }
}
