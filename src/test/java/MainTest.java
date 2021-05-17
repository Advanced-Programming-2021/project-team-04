import controller.MainController;
import model.Account;
import model.Card;
import model.GameDeck;
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
    public void activeDeckTestForTheOtherPlayer() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        GameDeck gameDeck = new GameDeck("No Pressure");
        thisAccount.setActiveDeck(gameDeck);
        MainController.getInstance().newDuel("Jan Kilbride", 1);
        Assertions.assertEquals(someoneElse.getUsername() + " has no active deck\r\n", outputStream.toString());
        thisAccount.setActiveDeck(null);
    }
    @Test
    public void invalidDeckTestForThisUser() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        thisAccount.setActiveDeck(new GameDeck("our boy jack"));
        someoneElse.setActiveDeck(new GameDeck("no happy ending"));
        MainController.getInstance().newDuel("Jan Kilbride", 3);
        Assertions.assertEquals("Gerard Keay’s deck is invalid\r\n", outputStream.toString());
        thisAccount.setActiveDeck(null);
        someoneElse.setActiveDeck(null);
    }
    @Test
    public void invalidDeckTestForTheOtherPlayer() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        GameDeck gameDeck = new GameDeck("our boy jack");
        for (int i = 0; i < 40; i++)
            gameDeck.getMainDeck().add(Card.getCardByName("Call of The Haunted"));
        thisAccount.setActiveDeck(gameDeck);
        someoneElse.setActiveDeck(new GameDeck("no happy ending"));
        MainController.getInstance().newDuel("Jan Kilbride", 3);
        Assertions.assertEquals("Jan Kilbride’s deck is invalid\r\n", outputStream.toString());
        thisAccount.setActiveDeck(null);
        someoneElse.setActiveDeck(null);
    }
    @Test
    public void invalidRoundTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        GameDeck gameDeck = new GameDeck("our boy jack");
        for (int i = 0; i < 40; i++)
            gameDeck.getMainDeck().add(Card.getCardByName("Call of The Haunted"));
        thisAccount.setActiveDeck(gameDeck);
        someoneElse.setActiveDeck(gameDeck);
        MainController.getInstance().newDuel("Jan Kilbride", 4);
        Assertions.assertEquals("number of rounds is not supported\r\n", outputStream.toString());
        thisAccount.setActiveDeck(null);
        someoneElse.setActiveDeck(null);
    }
    @Test
    public void cheatIncreaseMoneyTest() {
        int initialMoney = thisAccount.getMoney();
        MainController.getInstance().cheatIncreaseMoney(4444);
        Assertions.assertEquals(initialMoney + 4444, thisAccount.getMoney());
    }
    @Test
    public void cheatIncreaseScoreTest() {
        int initialScore = thisAccount.getScore();
        MainController.getInstance().cheatIncreaseScore(4444);
        Assertions.assertEquals(initialScore + 4444, thisAccount.getScore());
    }
}
