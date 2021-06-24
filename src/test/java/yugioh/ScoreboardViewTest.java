package yugioh;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import yugioh.model.Account;
import yugioh.view.IO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class ScoreboardViewTest {
    @BeforeAll
    public static void createAccounts() {
        Account ulysses = new Account("Ulysses", "greek", "Dies at Dawn");
        Account hades = new Account("Hades", "greek", "God of Death");
        ulysses.setScore(444);
        hades.setScore(4444);
    }

    @Test
    public void showScoreboard() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream(("""
                scoreboard show\r
                menu exit\r
                """).getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.ScoreboardView.getInstance().run();
        Assertions.assertEquals("1- God of Death: 4444\n" +
                "2- Dies at Dawn: 444\n\r\n", outputStream.toString());
    }

    @Test
    public void currentMenu() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream(("""
                menu show-current\r
                menu exit\r
                """).getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.ScoreboardView.getInstance().run();
        Assertions.assertEquals("Scoreboard Menu\r\n", outputStream.toString());
    }

    @Test
    public void run() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream(("""
                kill me\r
                menu exit\r
                """).getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.ScoreboardView.getInstance().run();
        Assertions.assertEquals("invalid command\r\n", outputStream.toString());
    }
}
