package yugioh.model.cards;

import com.google.gson.annotations.Expose;
import yugioh.controller.ImportAndExport;
import lombok.Getter;
import lombok.Setter;
import yugioh.model.AI;
import yugioh.model.Account;
import yugioh.model.Duelist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

@Getter
@Setter
public class Card {

    protected static ArrayList<Card> allCards;
    protected static TreeMap<String, String> nameToDescriptionMap;
    protected static HashMap<String, String> specialCardNameToClassNameMap;

    @Expose
    protected String name;
    @Expose
    protected int price;
    @Expose
    protected String description;
    protected int allowedNumber = 3;
    protected Duelist owner;
    @Expose
    protected String ownerUsername;
    protected boolean hasBeenUsedInThisTurn = false;
    protected boolean hasBeenSetOrSummoned = false;

    static {
        allCards = new ArrayList<>();
        nameToDescriptionMap = ImportAndExport.getInstance().readCardNameToDescriptionMap();
        specialCardNameToClassNameMap = ImportAndExport.getInstance().readSpecialCardNameToClassNameMap();
    }

    public Card() {
        allCards.add(this);
    }

    public Duelist getOwner() {
        if (owner == null) {
            if (ownerUsername.equals(AI.AI_USERNAME)) owner = AI.getInstance();
            else owner = Account.getAccountByUsername(ownerUsername);
        }
        return owner;
    }

    public static Card getCardByName(String name) {
        return allCards.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    public static String getDescriptionByName(String name) {
        return nameToDescriptionMap.get(name);
    }

    public static boolean isCardSpecial(String cardName) {
        return specialCardNameToClassNameMap.containsKey(cardName);
    }

    public static String getSpecialCardClassName(String cardName) {
        return specialCardNameToClassNameMap.get(cardName);
    }

    public void reset() { }

}
