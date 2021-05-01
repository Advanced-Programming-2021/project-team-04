package view;

public class ProfileView extends Menu {
    private static ProfileView singleInstance = null;
    private ProfileView() {

    }
    public static ProfileView getInstance() {
        if (singleInstance == null)
            singleInstance = new ProfileView();
        return singleInstance;
    }

    @Override
    public void run() {

    }

    @Override
    public void showCurrentMenu() {
        Output.getInstance().printProfileMenuName();
    }

    private void changeNickname(String input) {

    }
    private void changePassword(String input) {

    }
    private void showCurrent() {

    }
}
