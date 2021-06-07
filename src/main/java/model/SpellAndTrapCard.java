package model;

import com.google.gson.annotations.Expose;
import controller.MainController;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class SpellAndTrapCard extends Card {
    protected static ArrayList<SpellAndTrapCard> spellAndTrapCards = new ArrayList<>();
    protected boolean isActive = false;
    @Expose()
    protected boolean isSpell;
    @Expose()
    protected String property;
    @Expose()
    protected boolean isLimited;

    public SpellAndTrapCard() {
        super();
        spellAndTrapCards.add(this);
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
