package controller;

import model.Account;
import view.IO;

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
            IO.getInstance().nicknameChanged();
        }
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (isChangingPasswordValid(oldPassword, newPassword)) {
            MainController.getInstance().getLoggedIn().setPassword(newPassword);
            IO.getInstance().passwordChanged();
        }
    }
    private boolean isChangingPasswordValid(String oldPassword, String newPassword) {
        Account player = MainController.getInstance().getLoggedIn();
        if (!player.getPassword().equals(oldPassword)) {
            IO.getInstance().invalidCurrentPassword();
            return false;
        }
        else if (oldPassword.equals(newPassword)) {
            IO.getInstance().enterANewPassword();
            return false;
        }
        return true;

    }
    private boolean isChangingNicknameValid(String nickname) {
        if (Account.getAllNicknames().contains(nickname)) {
            IO.getInstance().userWithNicknameExists(nickname);
            return false;
        }
        return true;
    }
}
