package controller;

import model.Account;
import view.Output;

public class ProfileController {
    private static ProfileController singleInstance = null;

    public static ProfileController getInstance() {
        if (singleInstance == null)
            singleInstance = new ProfileController();
        return singleInstance;
    }

    public void changeNickname(String name) {
        if (isChangingNicknameValid(name)) {
            MainController.getInstance().getLoggedIn().setNickname(name);
            Output.getForNow();
        }
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (isChangingPasswordValid(oldPassword, newPassword)) {
            MainController.getInstance().getLoggedIn().setPassword(newPassword);
            Output.getForNow();
        }
    }
    private boolean isChangingPasswordValid(String oldPassword, String newPassword) {
        Account player = MainController.getInstance().getLoggedIn();
        if (!player.getPassword().equals(oldPassword)) {
            Output.getForNow();
            return false;
        }
        else if (oldPassword.equals(newPassword)) {
            Output.getForNow();
            return false;
        }
        return true;

    }
    private boolean isChangingNicknameValid(String nickname) {
        if (Account.getAllNicknames().contains(nickname)) {
            Output.getForNow();
            return false;
        }
        return true;
    }
}
