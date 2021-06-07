package model;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Card {

    protected static ArrayList<Card> allCards = new ArrayList<>();

    @Expose()
    protected String name;
    @Expose()
    protected int price;
    @Expose()
    protected String description;
    protected int allowedNumber = 3;
    protected Duelist Owner;
    @Expose()
    protected String ownerUsername;
    protected boolean hasBeenUsedInThisTurn = false;
    protected boolean hasBeenSetOrSummoned = false;

    //TODO NEED a fucking method that takes the card name as input and shows whether it is a monster card or a spell or trap

    public Card() {
        allCards.add(this);
    }

    public Duelist getOwner() {
        if (Owner == null) Owner = Account.getAccountByUsername(ownerUsername);
        return Owner;
    }

    public static Card getCardByName(String name) {
        return allCards.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    public void reset() { }

}
