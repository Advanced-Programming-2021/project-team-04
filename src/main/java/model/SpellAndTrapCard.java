package model;

import java.util.ArrayList;

public class SpellAndTrapCard extends Card {
    protected static ArrayList<SpellAndTrapCard> spellAndTrapCards = new ArrayList<>();
    protected boolean isActive = false;
    protected boolean canBeActivated = true;
    protected boolean isSpell;
    protected String property;
    protected boolean isLimited;

    public SpellAndTrapCard() {
        super();
        spellAndTrapCards.add(this);
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public static ArrayList<SpellAndTrapCard> getSpellAndTrapCards() {
        return spellAndTrapCards;
    }

    public static void setSpellAndTrapCards(ArrayList<SpellAndTrapCard> spellAndTrapCards) {
        SpellAndTrapCard.spellAndTrapCards = spellAndTrapCards;
    }

    public boolean isSpell() {
        return isSpell;
    }

    public void setSpell(boolean spell) {
        isSpell = spell;
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

    @Override
    public String toString() {
        String spellAndTrap = "Trap";
        if (isSpell) spellAndTrap = "Spell";
        return "Name: " + this.name + "\n" + spellAndTrap + "\nType: " + this.property
                + "\nDescription: " + this.description;
    }

    public void reset() {
        hasBeenUsedInThisTurn = false;
    }

}
