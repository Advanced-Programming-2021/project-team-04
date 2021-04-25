package model;

public class ChangeOfHeart extends SpellAndTrapCard {

    private Card hijackedCard;
    private Account originalOwner;

    public ChangeOfHeart() {
        super();
        setName("Change of Heart");
        isSpell = true;
        property = SpellAndTrapTypes.NORMAL;
        setDescription();
        setAllowedNumber(1);
        setPrice(2500);
    }

    private void setDescription() {
        description = "Target 1 monster your opponent controls; take control of it until the End Phase.";
    }

    public void setHijackedCard(Card hijackedCard) {
        this.hijackedCard = hijackedCard;
        originalOwner = hijackedCard.getOwner();
        hijackedCard.setOwner(this.getOwner());
    }

    public void reset() {
        hijackedCard.setOwner(originalOwner);
        hijackedCard = null;
        originalOwner = null;
        //TODO: remember that monsters are not the only ones with resets
    }
}
