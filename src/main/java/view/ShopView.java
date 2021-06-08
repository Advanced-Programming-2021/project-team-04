package view;

import controller.ShopController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShopView extends ViewMenu {

    private static ShopView singleInstance = null;

    private final Pattern buyCardPattern = Pattern.compile("(?:shop )?b(?:uy)? (?<name>[^-]+)");

    private ShopView() {}

    public static ShopView getInstance() {
        if (singleInstance == null)
            singleInstance = new ShopView();
        return singleInstance;
    }

    @Override
    public void run() {
        String command;
        while (!(command = IO.getInstance().getInputMessage()).matches("(?:menu )?exit") &&
                !command.matches("(?:menu )?enter [Mm]ain(?: menu)?")) {
            Matcher buyCardMatcher = buyCardPattern.matcher(command);
            if (command.matches("(?:menu )?s(?:how)?-c(?:urrent)?"))
                showCurrentMenu();
            else if (command.matches("(?:menu )?enter \\S+"))
                IO.getInstance().printMenuNavigationImpossible();
            else if (command.matches("(?:shop )?s(?:how)? -(?:-all|a)"))
                showAllCards();
            else if (buyCardMatcher.matches())
                buyCard(buyCardMatcher);
            else if (command.startsWith("card show"))
                showCard(command);
            else IO.getInstance().printInvalidCommand();
        }
    }

    @Override
    public void showCurrentMenu() {
        IO.getInstance().printShopMenuName();
    }

    private void showAllCards() {
        ShopController.getInstance().showAllCards();
    }

    private void showCard(String input) {
        String cardName = input.substring(10);
        if (!ShopController.getInstance().showCard(cardName)) IO.getInstance().printInvalidCommand();
    }

    private void buyCard(Matcher matcher) {
        ShopController.getInstance().buyCard(matcher.group("name"));
    }

}
