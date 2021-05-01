package view;

import controller.LoginController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginView extends Menu {

    private static LoginView singleInstance = null;

    private Pattern createUserPattern = Pattern.compile("^user create (?=.*(?:\\-(?:(?:\\-username)|(?:u))) (?<username>\\S+))" +
            "(?=.*(?:\\-(?:(?:\\-password)|(?:p))) (?<password>\\S+))(?=.*(?:\\-(?:(?:\\-nickname)|(?:n))) (?<nickname>\\S+)).+$");
    private Pattern loginUserPattern = Pattern.compile("^user create (?=.*(?:\\-(?:(?:\\-username)|(?:u))) (?<username>\\S+))" +
            "(?=.*(?:\\-(?:(?:\\-password)|(?:p))) (?<password>\\S+)).+$");

    private LoginView() {}

    public static LoginView getInstance() {
        if (singleInstance == null)
            singleInstance = new LoginView();
        return singleInstance;
    }

    @Override
    public void run() {
        String command;
        while (!(command = Input.getInputMessage()).equals("menu exit")) {
            Matcher createUserMatcher = createUserPattern.matcher(command), loginUserMatcher = loginUserPattern.matcher(command);
            if (command.matches("menu (?:show|s)\\-(?:current|c)"))
                showCurrentMenu();
            else if (createUserMatcher.matches())
                createUser(createUserMatcher);
            else if (loginUserMatcher.matches())
                loginUser(loginUserMatcher);
        }
    }

    @Override
    public void showCurrentMenu() {
        Output.getInstance().printLoginMenuName();
    }

    private void createUser(Matcher matcher) {
        LoginController.getInstance().createUser(matcher.group("username"), matcher.group("password"), matcher.group("nickname"));
    }

    private void loginUser(Matcher matcher) {
        LoginController.getInstance().loginUser(matcher.group("username"), matcher.group("password"));
    }

}
