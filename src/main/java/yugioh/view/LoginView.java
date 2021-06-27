package yugioh.view;

import yugioh.controller.LoginController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginView extends ViewMenu {

    private static LoginView singleInstance = null;

    private final Pattern createUserPattern = Pattern.compile("^(?:user )?create (?=.*-(?:-username|u) (?<username>\\S+))" +
            "(?=.*-(?:-password|p) (?<password>\\S+))(?=.*-(?:-nickname|n) (?<nickname>\\S+)).+$");
    private final Pattern loginUserPattern = Pattern.compile("^(?:user )?login (?=.*-(?:-username|u) (?<username>\\S+))" +
            "(?=.*-(?:-password|p) (?<password>\\S+)).+$");

    private LoginView() {}

    public static LoginView getInstance() {
        if (singleInstance == null)
            singleInstance = new LoginView();
        return singleInstance;
    }

    @Override
    public void run() {
        String command;
        while (!(command = IO.getInstance().getInputMessage()).matches("(?:menu )?exit")) {
            Matcher createUserMatcher = createUserPattern.matcher(command), loginUserMatcher = loginUserPattern.matcher(command);
            if (command.matches("(?:menu )?s(?:how)?-c(?:urrent)?"))
                showCurrentMenu();
            else if (createUserMatcher.matches())
                createUser(createUserMatcher);
            else if (loginUserMatcher.matches())
                loginUser(loginUserMatcher);
            else if (command.matches("(?:menu )?enter \\S+"))
                IO.getInstance().printLoginFirst();
            else IO.getInstance().printInvalidCommand();
        }
    }

    @Override
    public void showCurrentMenu() {
        IO.getInstance().printLoginMenuName();
    }

    private void createUser(Matcher matcher) {
        LoginController.getInstance().createUser(matcher.group("username"), matcher.group("password"), matcher.group("nickname"));
    }

    private void loginUser(Matcher matcher) {
         if (LoginController.getInstance().loginUser(matcher.group("username"), matcher.group("password")))
             MainView.getInstance().run();
    }

}
