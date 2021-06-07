package view;

import controller.ProfileController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileView extends ViewMenu {

    private static ProfileView singleInstance = null;

    private final Pattern changeNicknamePattern = Pattern.compile("^(?:profile )?change " +
            "(?=.*-(?:-nickname|n) (?<nickname>\\S+)).+$");
    private final Pattern changePasswordPattern = Pattern.compile("^(?:profile )?change (?=.*-(?:-password|p))" +
            "(?=.*-(?:-current|c) (?<currentPassword>\\S+))(?=.*-(?:-new|n) (?<newPassword>\\S+)).+$");

    private ProfileView() { }

    public static ProfileView getInstance() {
        if (singleInstance == null)
            singleInstance = new ProfileView();
        return singleInstance;
    }

    @Override
    public void run() {
        String command;
        while (!(command = IO.getInstance().getInputMessage()).matches("(?:menu )?exit") &&
                !command.matches("(?:menu )?enter [Mm]ain(?: menu)?")) {
            Matcher changeNicknameMatcher = changeNicknamePattern.matcher(command);
            Matcher changePasswordMatcher = changePasswordPattern.matcher(command);
            if (command.matches("(?:menu )?s(?:how)?-c(?:urrent)?"))
                showCurrentMenu();
            else if (command.matches("(?:menu )?enter \\S+"))
                IO.getInstance().printMenuNavigationImpossible();
            else if (changeNicknameMatcher.matches())
                changeNickname(changeNicknameMatcher);
            else if (changePasswordMatcher.matches())
                changePassword(changePasswordMatcher);
            else IO.getInstance().printInvalidCommand();
        }
    }

    @Override
    public void showCurrentMenu() {
        IO.getInstance().printProfileMenuName();
    }

    private void changeNickname(Matcher matcher) {
        ProfileController.getInstance().changeNickname(matcher.group("nickname"));
    }

    private void changePassword(Matcher matcher) {
        ProfileController.getInstance().changePassword(matcher.group("currentPassword"), matcher.group("newPassword"));
    }

}
