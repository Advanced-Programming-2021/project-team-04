package model;

public class ChangeOfHeart extends SpellAndTrapCard {
    public ChangeOfHeart() {
        super();
        setName("Change of Heart");
        isSpell = true;
        type = SpellAndTrapTypes.NORMAL;
        setDescription();
        setAllowedNumber(1);
        setPrice(2500);
    }
    private void setDescription() {
        description = "Target 1 monster your opponent controls; take control of it until the End Phase.";
    }
}
