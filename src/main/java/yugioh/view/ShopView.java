package yugioh.view;

import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import yugioh.controller.MainController;
import yugioh.controller.ShopController;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import yugioh.model.cards.Card;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ShopView {

    private static final ArrayList<Card> ALL_CARDS = ShopController.getAllCards();

    private static Card firstCard;
    private static Card secondCard;
    private static int navigate;

    public static void run() {
        navigate = 0;
        showCards();
        LoginView.shopScene.lookup("#back").setDisable(true);
    }

    private static void showCards() {
        firstCard = ALL_CARDS.get(navigate * 2);
        if ((navigate * 2 + 1) == ALL_CARDS.size()) secondCard = null;
        else secondCard = ALL_CARDS.get(navigate * 2 + 1);
        setImages();
        checkAmount();
        checkMoney();
    }


    private static void setImages() {
        ImageView first = (ImageView) LoginView.shopScene.lookup("#first");
        ImageView second = (ImageView) LoginView.shopScene.lookup("#second");
        Image firstImage;
        Image secondImage;
        if (!firstCard.isOriginal() || firstCard.isConverted())
            firstImage = new Image(ShopView.class.getResourceAsStream("cardimages/JonMartin.jpg"));
        else firstImage = new Image(ShopView.class.getResourceAsStream("cardimages/" + firstCard.getName() + ".jpg"));
        if (secondCard == null)
            secondImage = new Image(ShopView.class.getResourceAsStream("cardimages/empty.jpg"));
        else if (!secondCard.isOriginal() || secondCard.isConverted())
            secondImage = new Image(ShopView.class.getResourceAsStream("cardimages/JonMartin.jpg"));
        else secondImage = new Image(ShopView.class.getResourceAsStream("cardimages/" + secondCard.getName() + ".jpg"));
        first.setImage(firstImage);
        second.setImage(secondImage);
        Tooltip.install(first, getToolTip(firstCard));
        if (secondCard != null) Tooltip.install(second, getToolTip(secondCard));
        else Tooltip.install(second, new Tooltip("Empty"));
    }

    private static Tooltip getToolTip(Card card) {
        StringBuilder cardInformation = new StringBuilder(card.getName()).append("\n");
        if (card instanceof MonsterCard) {
            MonsterCard monsterCard = (MonsterCard) card;
            cardInformation.append("Monster Card\nATK: ").append(monsterCard.getClassAttackPower()).
                    append("\nDEF: ").append(monsterCard.getClassDefensePower()).append("\nLEVEL: ").append(monsterCard.getLevel());
        } else {
            SpellAndTrapCard spellAndTrapCard = (SpellAndTrapCard) card;
            cardInformation.append(spellAndTrapCard.isSpell() ? "Spell Card" : "Trap Card");
        }
        cardInformation.append("\nPRICE: ").append(card.getPrice());
        Tooltip tooltip = new Tooltip(cardInformation.toString());
        tooltip.setShowDelay(Duration.seconds(0));
        tooltip.setStyle("-fx-font-size: 17");
        return tooltip;
    }

    private static void checkAmount() {
        LinkedHashMap<String, Short> userCards = MainController.getInstance().getLoggedIn().getAllCardsHashMap();
        if (userCards.containsKey(firstCard.getName())) {
            int firstAmount = userCards.get(firstCard.getName());
            ((Label) LoginView.shopScene.lookup("#firstAmount")).setText(firstAmount + "");
        } else ((Label) LoginView.shopScene.lookup("#firstAmount")).setText("0");
        if (secondCard != null && userCards.containsKey(secondCard.getName())) {
            int secondAmount = userCards.get(secondCard.getName());
            ((Label) LoginView.shopScene.lookup("#secondAmount")).setText(secondAmount + "");
        } else ((Label) LoginView.shopScene.lookup("#secondAmount")).setText("0");
    }

    private static void checkMoney() {
        int userMoney = MainController.getInstance().getLoggedIn().getMoney();
        LoginView.shopScene.lookup("#firstBuy").setDisable(userMoney < firstCard.getPrice());
        LoginView.shopScene.lookup("#secondBuy").setDisable(secondCard == null || userMoney < secondCard.getPrice());
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
        if (navigate == (ALL_CARDS.size() - 1) / 2) LoginView.shopScene.lookup("#next").setDisable(false);
        if (navigate == 1) LoginView.shopScene.lookup("#back").setDisable(true);
        navigate--;
        showCards();
    }

    public void next() {
        if (navigate == 0) LoginView.shopScene.lookup("#back").setDisable(false);
        if (navigate == ((ALL_CARDS.size() - 1) / 2) - 1) LoginView.shopScene.lookup("#next").setDisable(true);
        navigate++;
        showCards();
    }

    public void mainMenu() {
        LoginView.stage.setScene(LoginView.mainScene);
        LoginView.stage.centerOnScreen();
    }
}
