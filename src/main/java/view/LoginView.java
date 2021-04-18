package view;
public class LoginView {
    private static LoginView singleInstance = null;
    private LoginView() {

    }
    public static LoginView getInstance() {
        if (singleInstance == null)
            singleInstance = new LoginView();
        return singleInstance;
    }
    public void run() {

    }
    public void showCurrent() {

    }
    private void createUser(String input) {

    }
    private void loginUser(String input) {

    }

}
