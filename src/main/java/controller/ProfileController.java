package controller;

public class ProfileController {
    private static ProfileController singleInstance = null;
    private ProfileController() {

    }
    public static ProfileController getInstance() {
        if (singleInstance == null)
            singleInstance = new ProfileController();
        return singleInstance;
    }
    public void run() {

    }
    private void changeNickname(String name) {

    }
    private boolean isChangingNicknameValid(String nickname) {
        return true;
    }
    private void changePassword(String oldPassword, String newPassword) {

    }
    private boolean isChangingPasswordValid(String oldPassword, String newPassword) {
        return true;
    }
}
