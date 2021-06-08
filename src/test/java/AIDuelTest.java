import controller.MainController;
import controller.ShopController;
import model.*;
import model.cards.SpellAndTrapCard;
import org.junit.jupiter.api.BeforeAll;

public class AIDuelTest {

    public static Account thisAccount = new Account("Pvt. Witt", "I_ACTUALLY_SAW_ANOTHER_WORLD_BITCH", "Witt");
    static SpellAndTrapCard card;

    @BeforeAll
    public static void setup() {
        ShopController.getInstance();
        MainController.getInstance().setLoggedIn(thisAccount);
        var cardName = "Dark Hole";
        PlayerDeck playerDeck = new PlayerDeck("Damaged");
        for (int i = 0; i < 40; i++)
            playerDeck.addCardToMainDeck(cardName);
        thisAccount.setActivePlayerDeck("Damaged");
        MainController.getInstance().newAIDuel(1);
    }
}
