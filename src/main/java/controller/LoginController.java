package controller;

public class LoginController {
    private static LoginController singleInstance = null;
    private LoginController() {

    }
    public static LoginController getInstance() {
        if (singleInstance == null)
            singleInstance = new LoginController();
        return singleInstance;
    }
    public void run() {

    }
    private void createUser(String username, String password, String nickname){

    }
    private boolean isCreatingUserValid(String username, String nickname) {
        return true;
    }
    private void loginUser(String username, String password) {

    }
    private boolean isLoggingInValid(String username, String password) {
        return true;
    }

}
