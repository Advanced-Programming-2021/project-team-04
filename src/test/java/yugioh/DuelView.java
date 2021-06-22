package yugioh;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import yugioh.controller.DuelController;
import yugioh.controller.MainController;
import yugioh.model.Account;
import yugioh.model.Game;
import yugioh.model.PlayerDeck;
import yugioh.view.IO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
public class DuelView {
    private static Game lastWar;
    private static Account serenade;
    private static Account noRemedy;

    @BeforeAll
    public static void createGame() {
        PlayerDeck remedy = new PlayerDeck("alone");
        PlayerDeck ser = new PlayerDeck("waldron");
        noRemedy = new Account("Aint", "R", "no");
        noRemedy.getAllPlayerDecks().add(remedy);
        noRemedy.setActivePlayerDeck(remedy.getDeckName());
        serenade = new Account("Sad", "D", "Dull");
        serenade.getAllPlayerDecks().add(ser);
        serenade.setActivePlayerDeck(ser.getDeckName());
        serenade.getAllCardsHashMap().put("Alexandrite Dragon", (short) 1);
        noRemedy.getAllCardsHashMap().put("Battle OX", (short) 1);
        ser.getMainDeckCards().put("Alexandrite Dragon", (short) 50);
        remedy.getMainDeckCards().put("Battle OX", (short) 40);
        MainController.getInstance().setLoggedIn(noRemedy);
        lastWar = new Game(noRemedy, serenade, 1, false);
        DuelController.getInstance().setGame(lastWar);
    }

    @Test
    public void runTest() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream(("""
                cause im a freak\r
                menu exit\r
                """).getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.DuelView.getInstance().run();
        Assertions.assertEquals("invalid command\r\n", outputStream.toString());
    }

    @Test
    public void selectTest() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream(("""
                select --h 1\r
                menu exit\r
                """).getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.DuelView.getInstance().run();
        Assertions.assertEquals("""
                card selected\r
                Dull: 8000
                \tc \tc \tc \tc \tc\s
                45
                \tE \tE \tE \tE \tE\s
                \tE \tE \tE \tE \tE\s
                0\t\t\t\t\t\tE\s

                --------------------------

                E \t\t\t\t\t\t0
                \tE \tE \tE \tE \tE\s
                \tE \tE \tE \tE \tE\s
                  \t\t\t\t\t\t35
                c \tc \tc \tc \tc \t
                no: 8000\r
                """, outputStream.toString());
    }

    @Test
    public void graveyardTest() {
        lastWar.setSelectedCard(noRemedy.getField().getHand().get(0));
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream(("show graveyard\r\nback\r\nmenu exit").getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.DuelView.getInstance().run();
        Assertions.assertEquals("\r\n", outputStream.toString());
    }

}
