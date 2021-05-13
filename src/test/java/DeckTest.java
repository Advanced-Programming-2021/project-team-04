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
        thisAccount.setActiveDeck(null);
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
        DeckController.getInstance().createDeck("Save Me");
        Deck deck = thisAccount.getDeckByName("Save Me");
        Card card = Card.getCardByName("Mind Crush");
        deck.getMainDeck().add(card);
        deck.getMainDeck().add(card);
        deck.getMainDeck().add(card);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DeckController.getInstance().addCardToDeck("Save Me", "Mind Crush", true);
        Assertions.assertEquals("there are already too many cards with name Mind Crush in deck Save Me\r\n", outputStream.toString());
    }

    @Test
    public void removeCardTest() {
        ShopController.getInstance().buyCard("Slot Machine");
        DeckController.getInstance().createDeck("Virkelighetens Etterklang");
        Deck deck = thisAccount.getDeckByName("Virkelighetens Etterklang");
        Card card = Card.getCardByName("Slot Machine");
        deck.getMainDeck().add(card);
        DeckController.getInstance().removeCardFromDeck("Virkelighetens Etterklang", "Slot Machine", true);
        Assertions.assertFalse(deck.getMainDeck().contains(card));
    }

    @Test
    public void deckDoesntExistTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DeckController.getInstance().removeCardFromDeck("Virkelighetens Etterklang", "Slot Machine", true);
        Assertions.assertEquals("deck with name Virkelighetens Etterklang does not exist\r\n", outputStream.toString());
    }

    @Test
    public void cardDoesntExistInMainTest() {
        DeckController.getInstance().createDeck("The Last Shadow Puppets");
        thisAccount.getDeckByName("The Last Shadow Puppets");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DeckController.getInstance().removeCardFromDeck("The Last Shadow Puppets", "Slot Machine", true);
        Assertions.assertEquals("card with name Slot Machine does not exist in main deck\r\n", outputStream.toString());
    }

    @Test
    public void cardDoesntExistInSideTest() {
        DeckController.getInstance().createDeck("Woods of Ypres");
        thisAccount.getDeckByName("Woods of Ypres");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        DeckController.getInstance().removeCardFromDeck("Woods of Ypres", "Slot Machine", false);
        Assertions.assertEquals("card with name Slot Machine does not exist in side deck\r\n", outputStream.toString());
    }
}
