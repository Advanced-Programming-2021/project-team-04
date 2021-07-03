package yugioh.model.cards;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;


@Getter
@Setter
public class SpellAndTrapCard extends Card {


    protected static ArrayList<SpellAndTrapCard> spellAndTrapCards = new ArrayList<>();
    protected boolean isActive = false;
    @JsonProperty
    protected boolean isSpell;
    @JsonProperty
    protected String property;
    @JsonProperty
    protected boolean isLimited;
    protected String[] fieldPositiveEffects;
    protected String[] fieldNegativeEffects;

    public SpellAndTrapCard() {
        super();
        spellAndTrapCards.add(this);
    }


    @Override
    public String toString() {
        return "Name: " + this.name + "\n" + (isSpell ? "Spell" : "Trap") +
                "\nType: " + this.property + "\nDescription: " + this.description;
    }


    @Override
    public void reset() {
        hasBeenUsedInThisTurn = false;
        if (getOwner().getField().getGraveyard().contains(this))
            isActive = false;
    }
}