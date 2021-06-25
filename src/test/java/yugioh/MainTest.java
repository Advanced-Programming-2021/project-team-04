package yugioh;

import yugioh.controller.MainController;
import yugioh.model.Account;
import yugioh.model.PlayerDeck;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;

public class MainTest {
    public static Account thisAccount = new Account("Gerard Keay", "bean$le", "Erard Ke");
    public static Account someoneElse = new Account("Jan Kilbride", "Astronaut", "vast");

    @BeforeAll
    public static void setLoggedIn() {
        MainController.getInstance().setLoggedIn(thisAccount);
        thisAccount.addDeck(new PlayerDeck("our boy jack"));
        someoneElse.addDeck(new PlayerDeck("no happy ending"));
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
        thisAccount.addDeck(new PlayerDeck("No Pressure"));
        thisAccount.setActivePlayerDeck("No Pressure");
        MainController.getInstance().newDuel("Jan Kilbride", 1);
        Assertions.assertEquals(someoneElse.getUsername() + " has no active deck\r\n", outputStream.toString());
        thisAccount.setActivePlayerDeck(null);
    }

    @Test
    public void invalidDeckTestForThisUser() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        PlayerDeck ourBoyJack = thisAccount.getDeckByName("our boy jack");
        ourBoyJack.setMainDeckCards(new LinkedHashMap<>());
        thisAccount.setActivePlayerDeck("our boy jack");
        someoneElse.setActivePlayerDeck("no happy ending");
        MainController.getInstance().newDuel("Jan Kilbride", 3);
        Assertions.assertEquals("Gerard Keay’s deck is invalid\r\n", outputStream.toString());
        thisAccount.setActivePlayerDeck(null);
        someoneElse.setActivePlayerDeck(null);
    }

    @Test
    public void invalidDeckTestForTheOtherPlayer() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        var cardName = "Call of The Haunted";
        PlayerDeck ourBoyJack = thisAccount.getDeckByName("our boy jack");
        for (int i = 0; i < 40; i++)
            ourBoyJack.addCardToMainDeck(cardName);
        thisAccount.setActivePlayerDeck("our boy jack");
        someoneElse.setActivePlayerDeck("no happy ending");
        MainController.getInstance().newDuel("Jan Kilbride", 3);
        Assertions.assertEquals("Jan Kilbride’s deck is invalid\r\n", outputStream.toString());
        thisAccount.setActivePlayerDeck(null);
        someoneElse.setActivePlayerDeck(null);
    }

    @Test
    public void invalidRoundTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        PlayerDeck playerDeck = new PlayerDeck("our boy jack");
        var cardName = "Call of The Haunted";
        for (int i = 0; i < 40; i++)
            playerDeck.addCardToMainDeck(cardName);
        thisAccount.setActivePlayerDeck("our boy jack");
        someoneElse.setActivePlayerDeck("our boy jack");
        MainController.getInstance().newDuel("Jan Kilbride", 4);
        Assertions.assertEquals("number of rounds is not supported\r\n", outputStream.toString());
        thisAccount.setActivePlayerDeck(null);
        someoneElse.setActivePlayerDeck(null);
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
