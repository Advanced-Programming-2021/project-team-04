import controller.DeckController;
import controller.DuelController;
import controller.MainController;
import controller.ShopController;
import model.Account;
import model.Card;
import model.Deck;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class DeckTest {
    public static Account thisAccount = new Account("Gerard Keay", "bean$le", "Erard Ke");

    @BeforeAll
    public static void setLoggedIn() {
        MainController.getInstance().setLoggedIn(thisAccount);
    }

    @Test
    public void createDeckTest() {
        DeckController.getInstance().createDeck("Speck of Dust");
        Assertions.assertNotNull(thisAccount.getDeckByName("Speck of Dust"));
    }

    @Test
    public void errorForCreatingDeckTest() {
        DeckController.getInstance().createDeck("The Return of Thunder");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DeckController.getInstance().createDeck("The Return of Thunder");
        Assertions.assertEquals("deck with name The Return of Thunder already exists\r\n", outputStream.toString());
    }

    @Test
    public void deleteDeckTest() {
        DeckController.getInstance().createDeck("The Frozen Moment");
        Assertions.assertNotNull(thisAccount.getDeckByName("The Frozen Moment"));
        DeckController.getInstance().deleteDeck("The Frozen Moment");
        Assertions.assertNull(thisAccount.getDeckByName("The Frozen Moment"));
    }

    @Test
    public void activateDeckTest() {
        DeckController.getInstance().createDeck("Despair");
        DeckController.getInstance().activateDeck("Despair");
        Assertions.assertNotNull(thisAccount.getActiveDeck());
    }

    @Test
    public void cardDoesntExistTest() {
        DeckController.getInstance().createDeck("your wave caresses me");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DeckController.getInstance().addCardToDeck("your wave caresses me", "euphoria", true);
        Assertions.assertEquals("card with name euphoria does not exist\r\n", outputStream.toString());
    }

    @Test
    public void deckDoesntExist() {
        ShopController.getInstance().buyCard("Magic Cylinder");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DeckController.getInstance().addCardToDeck("New Horizons", "Magic Cylinder", true);
        Assertions.assertEquals("deck with name New Horizons does not exist\r\n", outputStream.toString());
    }

    @Test
    public void fullMainDeckTest() {
        ShopController.getInstance().buyCard("Magic Cylinder");
        DeckController.getInstance().createDeck("Sleeping Beauty Syndrome");
        Deck deck = thisAccount.getDeckByName("Sleeping Beauty Syndrome");
        Card card = Card.getCardByName("Mind Crush");
        for (int i = 0; i < 60; i++)
            deck.getMainDeck().add(card);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DeckController.getInstance().addCardToDeck("Sleeping Beauty Syndrome", "Magic Cylinder", true);
        Assertions.assertEquals("main deck is full\r\n", outputStream.toString());
    }

    @Test
    public void fullSideDeckTest() {
        ShopController.getInstance().buyCard("Magic Cylinder");
        DeckController.getInstance().createDeck("Sleeping Beauty Syndrome");
        Deck deck = thisAccount.getDeckByName("Sleeping Beauty Syndrome");
        Card card = Card.getCardByName("Mind Crush");
        for (int i = 0; i < 15; i++)
            deck.getSideDeck().add(card);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DeckController.getInstance().addCardToDeck("Sleeping Beauty Syndrome", "Magic Cylinder", false);
        Assertions.assertEquals("side deck is full\r\n", outputStream.toString());
    }

    @Test
    public void repeatedCardsTest() {
        ShopController.getInstance().buyCard("Mind Crush");
        DeckController.getInstance().createDeck("Sleeping Beauty Syndrome");
        Deck deck = thisAccount.getDeckByName("Sleeping Beauty Syndrome");
        Card card = Card.getCardByName("Mind Crush");
        deck.getMainDeck().add(card);
        deck.getMainDeck().add(card);
        deck.getMainDeck().add(card);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DeckController.getInstance().addCardToDeck("Sleeping Beauty Syndrome", "Mind Crush", true);
        Assertions.assertEquals("there are already too many cards with name Mind Crush in deck Sleeping Beauty Syndrome\r\n", outputStream.toString());
    }
}
