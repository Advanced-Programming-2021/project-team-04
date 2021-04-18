package model;

public class SpellAndTrapCard extends Card {
    private boolean isActive;

    public SpellAndTrapCard() {
        super();
    }

    public void set(){

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
