package yugioh;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import yugioh.controller.MainController;
import yugioh.model.Account;
import yugioh.model.PlayerDeck;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.specialcards.CommandKnight;
import yugioh.model.cards.specialcards.ManEaterBug;
import yugioh.model.cards.specialcards.Suijin;
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
    public void deleteTest() {
        Account.getAccountByUsername("Amanita").addDeck(new PlayerDeck("Lito"));
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("deck delete Lito\r\nmenu exit\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        yugioh.view.DeckView.getInstance().run();
        Assertions.assertTrue(Account.getAccountByUsername("Amanita").getAllPlayerDecks().isEmpty());
    }

    @Test
    public void activateTest() {
        Account.getAccountByUsername("Amanita").addDeck(new PlayerDeck("Lito"));
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("deck set-activate Lito\r\nmenu exit\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        yugioh.view.DeckView.getInstance().run();
        Assertions.assertNotNull(Account.getAccountByUsername("Amanita").getActiveDeck());
        Account.getAccountByUsername("Amanita").setAllPlayerDecks(new ArrayList<>());
    }

    @Test
    public void addCard() {
        MonsterCard monsterCard = new ManEaterBug();
        monsterCard.setOwner(Account.getAccountByUsername("Amanita"));
        PlayerDeck lito = new PlayerDeck("Lito");
        Account.getAccountByUsername("Amanita").addDeck(lito);
        Account.getAccountByUsername("Amanita").setActivePlayerDeck("Lito");
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("deck add-card --card Man-Eater Bug --deck Lito\r\nmenu exit\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        yugioh.view.DeckView.getInstance().run();
        Assertions.assertTrue(lito.getMainDeckCards().containsKey("Man-Eater Bug"));
    }

    @Test
    public void removeCard() {
        MonsterCard monsterCard = new Suijin();
        monsterCard.setOwner(Account.getAccountByUsername("Amanita"));
        PlayerDeck lito = new PlayerDeck("Lito");
        lito.addCardToMainDeck(monsterCard.toString());
        Account.getAccountByUsername("Amanita").addDeck(lito);
        Account.getAccountByUsername("Amanita").setActivePlayerDeck("Lito");
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("deck rm-card --card Suijin --deck Lito\r\nmenu exit\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        yugioh.view.DeckView.getInstance().run();
        Assertions.assertFalse(lito.getMainDeckCards().containsKey("Suijin"));
    }

    @Test
    public void printAllDecks() {
        MonsterCard monsterCard = new Suijin();
        monsterCard.setOwner(Account.getAccountByUsername("Amanita"));
        PlayerDeck lito = new PlayerDeck("Lito");
        lito.addCardToMainDeck(monsterCard.toString());
        Account.getAccountByUsername("Amanita").getAllPlayerDecks().add(lito);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("deck show --all\r\nmenu exit\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.DeckView.getInstance().run();
        Assertions.assertEquals("""
                Decks:
                Active deck:
                Other decks:\s
                Lito: main deck 1, side deck 0, invalid\r
                """, outputStream.toString());
        Account.getAccountByUsername("Amanita").setAllPlayerDecks(new ArrayList<>());
    }

    @Test
    public void printDeck() {
        MonsterCard monsterCard = new Suijin();
        monsterCard.setOwner(Account.getAccountByUsername("Amanita"));
        PlayerDeck lito = new PlayerDeck("Lito");
        lito.addCardToMainDeck(monsterCard.toString());
        Account.getAccountByUsername("Amanita").getAllPlayerDecks().add(lito);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("deck show --deck-name Lito\r\nmenu exit\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.DeckView.getInstance().run();
        Assertions.assertEquals("""
                Decks:
                Active deck:
                Other decks:\s
                Lito: main deck 1, side deck 0, invalid\r
                """, outputStream.toString());
        Account.getAccountByUsername("Amanita").setAllPlayerDecks(new ArrayList<>());
    }

    @Test
    public void printAllCards() {
        MonsterCard monsterCard = new CommandKnight();
        monsterCard.setOwner(Account.getAccountByUsername("Amanita"));
        Account.getAccountByUsername("Amanita").getAllCardsHashMap().put(monsterCard.getName(), (short) 1);
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("deck show --cards\r\nmenu exit\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.DeckView.getInstance().run();
        Assertions.assertEquals("""
                Command Knight: All Warrior-Type monsters you control gain 400 ATK. If you control another monster, monsters your opponent controls cannot target this card for an attack.
                \r
                """, outputStream.toString());
    }
}
