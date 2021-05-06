package model;

public class ChangeOfHeart extends SpellAndTrapCard {

    private MonsterCard hijackedCard = null;
    private Duelist originalOwner = null;

    public ChangeOfHeart() {
        super();
        setName("Change of Heart");
        isSpell = true;
        property = "Normal";
        setDescription();
        setAllowedNumber(1);
        setPrice(2500);
    }

    private void setDescription() {
        description = "Target 1 monster your opponent controls; take control of it until the End Phase.";
    }

    public void setHijackedCard(MonsterCard hijackedCard) {
        this.hijackedCard = hijackedCard;
        this.originalOwner = hijackedCard.getOwner();
        this.getOwner().getField().getMonsterCards().add(hijackedCard);
        originalOwner.getField().getMonsterCards().remove(hijackedCard);
        hijackedCard.setOwner(this.getOwner());
    }

    public void reset() {
        if (isActive) {
            if (hijackedCard != null) {
                hijackedCard.setOwner(originalOwner);
                originalOwner.getField().getMonsterCards().add(hijackedCard);
                this.getOwner().getField().getMonsterCards().remove(hijackedCard);
                hijackedCard = null;
                originalOwner = null;
            }
            this.getOwner().getField().getTrapAndSpell().remove(this);
            this.getOwner().getField().getGraveyard().add(this);
        }
    }
}
