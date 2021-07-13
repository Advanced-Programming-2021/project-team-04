package yugioh.model.cards;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import yugioh.model.AI;
import yugioh.model.Account;
import yugioh.model.Duelist;

import java.util.ArrayList;


@Getter
@Setter
public class Card {

    protected static ArrayList<Card> allCards;

    static {
        allCards = new ArrayList<>();
    }

    @JsonProperty
    protected String name;
    @JsonProperty
    protected int price;
    @JsonProperty
    protected String description;
    protected int allowedNumber = 3;
    protected Duelist owner;
    @JsonProperty
    protected String ownerUsername;
    protected boolean hasBeenUsedInThisTurn = false;
    protected boolean hasBeenSetOrSummoned = false;
    @JsonProperty
    protected boolean isConverted = false;
    @JsonProperty
    protected boolean isOriginal = true;


    public Card() {
        allCards.add(this);
    }

    public static Card getCardByName(String name) {
        return allCards.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    public Duelist getOwner() {
        if (owner == null) {
            if (ownerUsername.equals(AI.AI_USERNAME)) owner = AI.getInstance();
            else owner = Account.getAccountByUsername(ownerUsername);
        }
        return owner;
    }

    public void reset() {
    }


}