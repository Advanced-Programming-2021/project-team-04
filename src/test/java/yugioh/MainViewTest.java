package yugioh;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import yugioh.controller.MainController;
import yugioh.model.Account;
import yugioh.model.PlayerDeck;
import yugioh.view.IO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class MainViewTest {
    private static Account artie;
    @BeforeAll
    public static void createAccount() {
        artie = new Account("John", "Stella", "Artie");
        MainController.getInstance().setLoggedIn(artie);
        Account cruella = new Account("Emma", "Stella", "Cruella");
    }

    @Test
    public void runTest() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream(("""
                but I like to say that normal is the cruelest insult of them all\r
                menu exit\r
                """).getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.MainView.getInstance().run();
        Assertions.assertEquals("""
                invalid command\r
                invalid command\r
                """, outputStream.toString());
    }

    @Test
    public void enterMenu() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream(("""
                menu enter Artie\r
                menu exit\r
                """).getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.MainView.getInstance().run();
        Assertions.assertEquals("""
                invalid command\r
                invalid command\r
                """, outputStream.toString());
    }

    @Test
    public void newGame() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream(("""
                duel --new --second-player Emma --rounds 1 \r
                menu exit\r
                """).getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.MainView.getInstance().run();
        Assertions.assertEquals("""
                John has no active deck\r
                invalid command\r
                """, outputStream.toString());
    }

    @Test
    public void newGameAI() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream(("""
                duel --new --ai --rounds 1 \r
                menu exit\r
                """).getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.MainView.getInstance().run();
        Assertions.assertEquals("""
                John has no active deck\r
                invalid command\r
                """, outputStream.toString());
    }

    @Test
    public void cheatIncreaseScore() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream(("""
                The Aurora Strikes 444\r
                menu exit\r
                """).getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.MainView.getInstance().run();
        Assertions.assertEquals(444, artie.getScore());
    }

    @Test
    public void cheatIncreaseMoney() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream(("""
                The Hanged Man Rusts 44444\r
                menu exit\r
                """).getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.MainView.getInstance().run();
        Assertions.assertEquals(144444, artie.getMoney());
    }
}
