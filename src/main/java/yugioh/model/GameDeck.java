package yugioh.model;

import com.google.gson.annotations.Expose;
import yugioh.controller.ImportAndExport;
import lombok.Getter;
import lombok.Setter;
import yugioh.model.cards.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

@Getter
@Setter
public class GameDeck {

    @Expose
    private ArrayList<Card> mainDeck = new ArrayList<>();
    @Expose
    private ArrayList<Card> sideDeck = new ArrayList<>();
    @Expose
    private String deckName;

    public GameDeck(PlayerDeck playerDeck) {
        deckName = playerDeck.getDeckName();
        playerDeck.getMainDeckCards().keySet().forEach(n -> {
            for (var i = 0; i < playerDeck.getMainDeckCards().get(n); i++) {
                try {
                    mainDeck.add(Objects.requireNonNull(ImportAndExport.getInstance().readCard(n)));
                } catch (Exception ignored) { }
            }
        });
        Collections.shuffle(mainDeck);
        playerDeck.getSideDeckCards().keySet().forEach(n -> {
            for (var i = 0; i < playerDeck.getSideDeckCards().get(n); i++) {
                try {
                    sideDeck.add(Objects.requireNonNull(ImportAndExport.getInstance().readCard(n)));
                } catch (Exception ignored) { }
            }
        });
        Collections.shuffle(sideDeck);
    }

    public boolean mainDeckHasCard(String cardName) {
        //TODO still doubt
        return mainDeck.contains(Card.getCardByName(cardName));
    }

    public boolean sideDeckHasCard(String cardName) {
        //TODO still doubt
        return sideDeck.contains(Card.getCardByName(cardName));
    }

    @Override
    public String toString() {
        return "";
    }

}
