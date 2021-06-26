package yugioh;

import yugioh.controller.ImportAndExport;
import yugioh.controller.ShopController;
import yugioh.view.LoginView;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        var AAA = new ArrayList<>(List.of("AAA", "BBB"));
        AAA.remove("CCC");
        System.out.println(AAA);
//        ShopController.getInstance();
//        ImportAndExport.getInstance().readAllUsers();
//        LoginView.getInstance().run();
//        ImportAndExport.getInstance().writeAllUsers();
    }
}
