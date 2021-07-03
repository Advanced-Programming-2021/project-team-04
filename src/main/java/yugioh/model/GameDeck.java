package yugioh.model;


import com.fasterxml.jackson.annotation.JsonProperty;
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


    private ArrayList<Card> mainDeck = new ArrayList<>();
    private ArrayList<Card> sideDeck = new ArrayList<>();
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


}