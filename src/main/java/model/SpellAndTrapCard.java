package model;

import java.util.ArrayList;

public class SpellAndTrapCard extends Card {
    protected static ArrayList<SpellAndTrapCard> spellAndTrapCards = new ArrayList<>();
    protected boolean isActive;
    protected boolean canBeActivated = true;

    public SpellAndTrapCard() {
        super();
        spellAndTrapCards.add(this);
    }

    public void set(){

    }

    public static ArrayList<SpellAndTrapCard> getSpellAndTrapCards() {
        return spellAndTrapCards;
    }

    public static void setSpellAndTrapCards(ArrayList<SpellAndTrapCard> spellAndTrapCards) {
        SpellAndTrapCard.spellAndTrapCards = spellAndTrapCards;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isCanBeActivated() {
        return canBeActivated;
    }

    public void setCanBeActivated(boolean canBeActivated) {
        this.canBeActivated = canBeActivated;
    }

    public boolean canBeUsed(Game game) {
        return false;
    }
    public void activate() {

    }
    @Override
    public String toString() {
        return "SpellAndTrapCard{}";
    }
}
