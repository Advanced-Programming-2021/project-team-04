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
import java.util.ArrayList;

public class DeckView {

    @BeforeAll
    public static void setAccount() {
        Account account = new Account("Amanita", "Nomi", "Neets");
        MainController.getInstance().setLoggedIn(account);
    }

    @Test
    public void createDeck() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("deck create amnesia\r\nmenu exit\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.DeckView.getInstance().run();
        Assertions.assertEquals("deck created successfully!\r\n", outputStream.toString());
        Account.getAccountByUsername("Amanita").setAllPlayerDecks(new ArrayList<>());
    }

    @Test
    public void deleteDeck() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("deck delete Lito\r\nmenu exit\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        Account.getAccountByUsername("Amanita").addDeck(new PlayerDeck("Lito"));
        yugioh.view.DeckView.getInstance().run();
        Assertions.assertTrue(Account.getAccountByUsername("Amanita").getAllPlayerDecks().isEmpty());
    }

    @Test
    public void activateTest() {
        Account.getAccountByUsername("Amanita").addDeck(new PlayerDeck("Lito"));
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("set activate Lito\r\nmenu exit\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.DeckView.getInstance().run();
        Assertions.assertNotNull(Account.getAccountByUsername("Amanita").getActiveDeck());
        Account.getAccountByUsername("Amanita").setAllPlayerDecks(new ArrayList<>());
    }

}
