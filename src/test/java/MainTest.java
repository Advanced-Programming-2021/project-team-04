import controller.MainController;
import model.Account;
import model.Deck;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class MainTest {
    public static Account thisAccount = new Account("Gerard Keay", "bean$le", "Erard Ke");
    public static Account someoneElse = new Account("Jan Kilbride", "Astronaut", "vast");
    @BeforeAll
    public static void setLoggedIn() {
        MainController.getInstance().setLoggedIn(thisAccount);
    }
    @Test
    public void userDoesntExistTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        MainController.getInstance().newDuel("Manuela Dominguez", 3);
        Assertions.assertEquals("there is no player with this username\r\n", outputStream.toString());
    }
    @Test
    public void activeDeckTestForThisUser() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        MainController.getInstance().newDuel("Jan Kilbride", 1);
        Assertions.assertEquals(thisAccount.getUsername() + " has no active deck\r\n", outputStream.toString());
    }
    @Test
    public void activeDeckForTheOtherPlayer() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        Deck deck = new Deck("No Pressure");
        thisAccount.setActiveDeck(deck);
        MainController.getInstance().newDuel("Jan Kilbride", 1);
        Assertions.assertEquals(someoneElse.getUsername() + " has no active deck\r\n", outputStream.toString());
    }
}
