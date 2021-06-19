package model.cards;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class SpellAndTrapCard extends Card {

    protected static ArrayList<SpellAndTrapCard> spellAndTrapCards = new ArrayList<>();
    @Expose // TODO: 6/19/2021 delete this @Expose
    protected boolean isActive = false;
    @Expose
    protected boolean isSpell;
    @Expose
    protected String property;
    @Expose
    protected boolean isLimited;

    public SpellAndTrapCard() {
        super();
        spellAndTrapCards.add(this);
    }

    @Override
    public String toString() {
        return "Name: " + this.name + "\n" + (isSpell ? "Spell" : "Trap") + "\nType: " + this.property
                + "\nDescription: " + this.description;
    }

    @Override
    public void reset() {
        hasBeenUsedInThisTurn = false;
        if (getOwner().getField().getGraveyard().contains(this))
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
