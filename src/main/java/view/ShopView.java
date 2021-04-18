package view;

public class ShopView {
    private static ShopView singleInstance = null;
    private ShopView() {

    }
    public static ShopView getInstance() {
        if (singleInstance == null)
            singleInstance = new ShopView();
        return singleInstance;
    }
    public void run() {

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
