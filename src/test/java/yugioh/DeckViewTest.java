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

public class DeckViewTest {

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
        // TODO: 6/25/2021 debug this son of a bitch
        MonsterCard monsterCard = new ManEaterBug();
        monsterCard.setOwner(Account.getAccountByUsername("Amanita"));
        PlayerDeck lito = new PlayerDeck("Lito");
        Account.getAccountByUsername("Amanita").addDeck(lito);
        Account.getAccountByUsername("Amanita").setActivePlayerDeck("Lito");
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("deck add-card --card ManEater Bug --deck Lito\r\nmenu exit\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        yugioh.view.DeckView.getInstance().run();
        Assertions.assertTrue(lito.getMainDeckCards().containsKey("ManEater Bug"));
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
    public void printAllDecksWithActiveDeck() {
        Account.getAccountByUsername("Amanita").setAllPlayerDecks(new ArrayList<>());
        MonsterCard monsterCard = new Suijin();
        monsterCard.setOwner(Account.getAccountByUsername("Amanita"));
        PlayerDeck lito = new PlayerDeck("Lito");
        lito.addCardToMainDeck(monsterCard.toString());
        Account.getAccountByUsername("Amanita").getAllPlayerDecks().add(lito);
        Account.getAccountByUsername("Amanita").setActivePlayerDeck("Lito");
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
                Lito: main deck 1, side deck 0, invalid
                Other decks:\s
                """, outputStream.toString());
        Account.getAccountByUsername("Amanita").setAllPlayerDecks(new ArrayList<>());
    }

    @Test
    public void printDeck() {
        MonsterCard monsterCard = new Suijin();
        monsterCard.setOwner(Account.getAccountByUsername("Amanita"));
        PlayerDeck lito = new PlayerDeck("Lito");
        lito.addCardToMainDeck(monsterCard.getName());
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
                Deck: Lito
                Main deck:
                Monsters:
                Suijin: During damage calculation in your opponent's turn, if this card is being attacked: You can target the attacking monster; make that target's ATK 0 during damage calculation only (this is a Quick Effect). This effect can only be used once while this card is face-up on the field.
                Spell and Traps:
                """, outputStream.toString());
        Account.getAccountByUsername("Amanita").setAllPlayerDecks(new ArrayList<>());
    }

    @Test
    public void printAllDecksWithoutActiveDeck() {
        Account.getAccountByUsername("Amanita").setActivePlayerDeck(null);
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
}
