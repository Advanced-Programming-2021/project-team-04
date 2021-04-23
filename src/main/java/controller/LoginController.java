package controller;

import model.Account;
import view.MainView;
import view.Output;

public class LoginController {
    private static LoginController singleInstance = null;

    public static LoginController getInstance() {
        if (singleInstance == null)
            singleInstance = new LoginController();
        return singleInstance;
    }

    private void loginUser(String username, String password) {
        if (isLoggingInValid(username, password)) {
            MainController.getInstance().setLoggedIn(Account.getAccountByUsername(username));
            MainView.getInstance().run();
        }
    }
    private boolean isLoggingInValid(String username, String password) {
        if (Account.getAccountByUsername(username) == null) {
            Output.getForNow();
            return false;
        }
        else if (!Account.getAccountByUsername(username).getPassword().equals(password)) {
            Output.getForNow();
            return false;
        }
        return true;
    }

    private void createUser(String username, String password, String nickname){
        if (errorsForCreatingUser(username, nickname))
            new Account(username, password, nickname);
    }

    private boolean errorsForCreatingUser(String username, String nickname) {
       if (Account.getAllUsernames().contains(username)){
           Output.getForNow();
           return false;
       }
       else if (Account.getAllNicknames().contains(nickname)) {
           Output.getForNow();
           return false;
       }
       return true;
    }
}
