package view;

import controller.ShopController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShopView extends Menu {

    private static ShopView singleInstance = null;

    private final Pattern buyCardPattern = Pattern.compile("(?:shop )?b(?:uy)? (?<name>\\S+)");

    private ShopView() {}

    public static ShopView getInstance() {
        if (singleInstance == null)
            singleInstance = new ShopView();
        return singleInstance;
    }

    @Override
    public void run() {
        String command;
        while (!(command = Input.getInputMessage()).matches("(?:menu )?exit") &&
                !command.matches("(?:menu )?enter (?:M|m)ain(?: (?:M|m)enu)?")) {
            Matcher buyCardMatcher = buyCardPattern.matcher(command);
            if (command.matches("(?:menu )?(?:show|s)\\-(?:current|c)"))
                showCurrentMenu();
            else if (command.matches("(?:menu )?enter \\S+"))
                Output.getInstance().printMenuNavigationImpossible();
            else if (command.matches("(?:shop )?s(?:how)? \\-(?:(?:\\-all)|(?:a))"))
                showAllCards();
            else if (buyCardMatcher.matches())
                buyCard(buyCardMatcher);
            else Output.getInstance().printInvalidCommand();
        }
    }

    @Override
    public void showCurrentMenu() {
        Output.getInstance().printShopMenuName();
    }

    private void showAllCards() {
        ShopController.getInstance().showAllCards();
    }

    private void showCard(String input) {
        //TODO check the doc. is this method necessary?
    }

    private void buyCard(Matcher matcher) {
        ShopController.getInstance().buyCard(matcher.group("name"));
    }

}
