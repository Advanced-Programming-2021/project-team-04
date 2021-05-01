package view;

public class ShopView extends Menu {

    private static ShopView singleInstance = null;

    private ShopView() {

    }
    public static ShopView getInstance() {
        if (singleInstance == null)
            singleInstance = new ShopView();
        return singleInstance;
    }

    @Override
    public void run() {

    }

    @Override
    public void showCurrentMenu() {
        Output.getInstance().printShopMenuName();
    }

    private void showAllCards() {

    }
    private void showCard(String input) {

    }
    private void buyCard(String input) {

    }
    private void showCurrent() {

    }
}
