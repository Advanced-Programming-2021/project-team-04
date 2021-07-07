package yugioh.controller;


import yugioh.model.AI;
import yugioh.model.Account;
import yugioh.view.IO;

import java.util.Random;


public class LoginController {

    private Random random = new Random();
    private static LoginController singleInstance = null;


    public static LoginController getInstance() {
        if (singleInstance == null)
            singleInstance = new LoginController();
        return singleInstance;
    }


    public boolean loginUser(String username, String password) {
        if (isLoggingInValid(username, password)) {
            MainController.getInstance().setLoggedIn(Account.getAccountByUsername(username));
            return true;
        }
        return false;
    }


    public void createUser(String username, String password, String nickname) {
        if (errorsForCreatingUser(username, nickname)) {
            setRandomProfile(new Account(username, password, nickname));
        }
    }

    public void setRandomProfile(Account account) {
        if (account.getProfilePictureExtension() == null)
            account.setProfilePictureExtension(".jpg");
        if (account.getProfilePictureNumber() != 0) return;
        account.setProfilePictureNumber(random.nextInt(9) + 1);
    }


    private boolean errorsForCreatingUser(String username, String nickname) {
        if (Account.getAllUsernames().contains(username) || username.equals(AI.AI_USERNAME)) {
            IO.getInstance().userWithUsernameExists(username);
            return false;
        } else if (Account.getAllNicknames().contains(nickname)) {
            IO.getInstance().userWithNicknameExists(nickname);
            return false;
        }
        return true;
    }


    public boolean isLoggingInValid(String username, String password) {
        if (Account.getAccountByUsername(username) == null || !Account.getAccountByUsername(username).getPassword().equals(password)) {
            IO.getInstance().passwordDoesntMatch();
            return false;
        }
        return true;
    }
}