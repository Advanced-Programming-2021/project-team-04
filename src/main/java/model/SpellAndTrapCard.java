package model;

import com.google.gson.annotations.Expose;
import controller.MainController;

import java.util.ArrayList;

public class SpellAndTrapCard extends Card {
    protected static ArrayList<SpellAndTrapCard> spellAndTrapCards = new ArrayList<>();
    protected boolean isActive = false;
    @Expose(serialize = true, deserialize = true)
    protected boolean isSpell;
    @Expose(serialize = true, deserialize = true)
    protected String property;
    @Expose(serialize = true, deserialize = true)
    protected boolean isLimited;

    public SpellAndTrapCard() {
        super();
        spellAndTrapCards.add(this);
    }

    public boolean isLimited() {
        return isLimited;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
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

    @Override
    public String toString() {
        var spellAndTrap = "Trap";
        if (isSpell) spellAndTrap = "Spell";
        return "Name: " + this.name + "\n" + spellAndTrap + "\nType: " + this.property
                + "\nDescription: " + this.description;
    }

    @Override
    public void reset() {
        hasBeenUsedInThisTurn = false;
        this.setOwner(MainController.getInstance().getLoggedIn());
        if (Owner.getField().getGraveyard().contains(this))
            isActive = false;
    }

    public int getSpeed() {
        if (isSpell) {
            if (property.equals("Quick-play"))
                return 2;
            else return 1;
        }
        else return 2;
    }

}
