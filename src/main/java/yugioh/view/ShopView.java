package yugioh.view;

import yugioh.controller.MainController;
import yugioh.controller.ShopController;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import yugioh.model.cards.Card;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ShopView {
    private static Card firstCard;
    private static Card secondCard;
    private static int navigate;
    private static final ArrayList<Card> allCards = Card.getAllCards();

    public static void run() {
        showCards();
        LoginView.shopScene.lookup("#back").setDisable(true);
    }

    private static void showCards() {
        firstCard = allCards.get(navigate * 2);
        secondCard = allCards.get(navigate * 2 + 1);
        setImages();
        checkAmount();
        checkMoney();
    }

    private static void setImages() {
        ImageView first = (ImageView) LoginView.shopScene.lookup("#first");
        ImageView second = (ImageView) LoginView.shopScene.lookup("#second");
        Image firstImage = new Image(ShopView.class.getResourceAsStream("cardimages/" + firstCard.getName() + ".jpg"));
        Image secondImage = new Image(ShopView.class.getResourceAsStream("cardimages/" + secondCard.getName() + ".jpg"));
        first.setImage(firstImage);
        second.setImage(secondImage);
    }

    private static void checkAmount() {
        LinkedHashMap<String, Short> userCards = MainController.getInstance().getLoggedIn().getAllCardsHashMap();
        if (userCards.containsKey(firstCard.getName())) {
            int firstAmount = userCards.get(firstCard.getName());
            ((Label) LoginView.shopScene.lookup("#firstAmount")).setText(firstAmount + "");
        } else ((Label) LoginView.shopScene.lookup("#firstAmount")).setText("0");
        if (userCards.containsKey(secondCard.getName())) {
            int secondAmount = userCards.get(secondCard.getName());
            ((Label) LoginView.shopScene.lookup("#secondAmount")).setText(secondAmount + "");
        } else ((Label) LoginView.shopScene.lookup("#secondAmount")).setText("0");
    }

    private static void checkMoney() {
        int userMoney = MainController.getInstance().getLoggedIn().getMoney();
        LoginView.shopScene.lookup("#firstBuy").setDisable(userMoney < firstCard.getPrice());
        LoginView.shopScene.lookup("#secondBuy").setDisable(userMoney < secondCard.getPrice());
    }

    public void buyFirstCard() {
        buyCard(firstCard.getName());
    }

    public void buySecondCard() {
        buyCard(secondCard.getName());
    }

    private void buyCard(String cardName) {
        ShopController.getInstance().buyCard(cardName);
        checkMoney();
        checkAmount();
    }

    public void back() {
        if (navigate == 37) LoginView.shopScene.lookup("#next").setDisable(false);
        if (navigate == 1) LoginView.shopScene.lookup("#back").setDisable(true);
        navigate--;
        showCards();
    }

    public void next() {
        if (navigate == 0) LoginView.shopScene.lookup("#back").setDisable(false);
        if (navigate == 36) LoginView.shopScene.lookup("#next").setDisable(true);
        navigate++;
        showCards();
    }

    public void mainMenu() {
        LoginView.stage.setScene(LoginView.mainScene);
        LoginView.stage.centerOnScreen();
    }
}
