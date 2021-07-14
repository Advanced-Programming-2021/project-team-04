package yugioh.controller;

import yugioh.model.PlayerDeck;
import yugioh.utils.Connection;
import yugioh.view.IO;


public class DeckController {

    private static DeckController singleInstance = null;

    public static DeckController getInstance() {
        if (singleInstance == null)
            singleInstance = new DeckController();
        return singleInstance;
    }

    public void createDeck(String deckName) {
        String result = Connection.getResult("DeckController@createDeck@" + deckName);
        if (result.equals("success"))
            IO.getInstance().deckCreated();
        else
            IO.getInstance().showMessage(result);
    }

    public void deleteDeck(String deckName) {
        Connection.getResult("DeckController@deleteDeck@" + deckName);
    }

    public void activateDeck(String deckName) {
        Connection.getResult("DeckController@activateDeck@" + deckName);
    }

    public void addCardToDeck(String deckName, String cardName, boolean isMainDeck) {
        String result = Connection.getResult("DeckController@addCardToDeck@" + deckName);
        if (!result.equals("success")) IO.getInstance().showMessage(result);
    }

    public void removeCardFromDeck(String deckName, String cardName, boolean isMainDeck) {
        Connection.getResult("DeckController@removeCardFromDeck@" + deckName + "@" + cardName + "@" + isMainDeck);
    }


    public String getDeckCards(PlayerDeck deck) {
        return Connection.getResult("DeckController@getDeckCards@" + deck.getDeckName());
    }
}