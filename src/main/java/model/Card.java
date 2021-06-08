package model;

import com.google.gson.annotations.Expose;
import controller.ImportAndExport;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.TreeMap;

@Getter
@Setter
public class Card {

    protected static ArrayList<Card> allCards;
    protected static TreeMap<String, String> nameToDescriptionMap;

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

    static {
        allCards = new ArrayList<>();
        nameToDescriptionMap = ImportAndExport.getInstance().readCardNameToDescriptionMap();
    }

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

    public static String getDescriptionByName(String name) {
        return nameToDescriptionMap.get(name);
    }

    public void reset() { }

}
