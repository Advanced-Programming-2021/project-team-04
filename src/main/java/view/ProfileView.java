package view;

public class ProfileView {
    private static ProfileView singleInstance = null;
    private ProfileView() {

    }
    public static ProfileView getInstance() {
        if (singleInstance == null)
            singleInstance = new ProfileView();
        return singleInstance;
    }
    public void run() {

    }
    private void changeNickname(String input) {

    }
    private void changePassword(String input) {

    }
    private void showCurrent() {

    }
}
