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

    public void loginUser(String username, String password) {
        if (isLoggingInValid(username, password)) {
            MainController.getInstance().setLoggedIn(Account.getAccountByUsername(username));
            Output.getInstance().loggedIn();
            MainView.getInstance().run();
        }
    }

    public void createUser(String username, String password, String nickname) {
        if (errorsForCreatingUser(username, nickname))
            new Account(username, password, nickname);
        Output.getInstance().userCreated();
    }

    private boolean errorsForCreatingUser(String username, String nickname) {
        if (Account.getAllUsernames().contains(username)) {
            Output.getInstance().userWithUsernameExists(username);
            return false;
        } else if (Account.getAllNicknames().contains(nickname)) {
            Output.getInstance().userWithNicknameExists(nickname);
            return false;
        }
        return true;
    }

    public boolean isLoggingInValid(String username, String password) {
        if (Account.getAccountByUsername(username) == null) {
            Output.getInstance().passwordDoesntMatch();
            return false;
        } else if (!Account.getAccountByUsername(username).getPassword().equals(password)) {
            Output.getInstance().passwordDoesntMatch();
            return false;
        }
        return true;
    }
}
