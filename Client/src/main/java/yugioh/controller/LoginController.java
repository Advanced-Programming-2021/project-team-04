package yugioh.controller;

import yugioh.utils.Connection;
import yugioh.view.IO;


public class LoginController {

    private static LoginController singleInstance = null;


    public static LoginController getInstance() {
        if (singleInstance == null)
            singleInstance = new LoginController();
        return singleInstance;
    }

    public boolean loginUser(String username, String password) {
        String result = Connection.getResult("LoginController loginUser " + username + " " + password);
        if (result.startsWith("success")) {
            Connection.setToken(result.split(" ")[1]);
            return true;
        } else {
            IO.getInstance().showMessage(result);
            return false;
        }
    }


    public void createUser(String username, String password, String nickname) {
        String result = Connection.getResult("LoginController createUser " + username + " " + password + " " + nickname);
        if (!result.equals("success")) IO.getInstance().showMessage(result);
    }
}