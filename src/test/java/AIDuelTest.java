import controller.MainController;
import controller.ShopController;
import model.*;
import org.junit.jupiter.api.BeforeAll;

public class AIDuelTest {

    public static Account thisAccount = new Account("Pvt. Witt", "I_ACTUALLY_SAW_ANOTHER_WORLD_BITCH", "Witt");
    static SpellAndTrapCard card;

    @BeforeAll
    public static void setup() {
        ShopController.getInstance();
        MainController.getInstance().setLoggedIn(thisAccount);
        card = (SpellAndTrapCard) Card.getCardByName("Dark Hole");
        GameDeck gameDeck = new GameDeck("Damaged");
        for (int i = 0; i < 40; i++)
            gameDeck.getMainDeck().add(card);
        thisAccount.setActiveDeck(gameDeck);
        thisAccount.setActiveDeck(gameDeck);
        MainController.getInstance().newAIDuel(1);
    }

}
