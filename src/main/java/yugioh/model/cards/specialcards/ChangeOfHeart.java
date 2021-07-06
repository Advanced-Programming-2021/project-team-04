package yugioh.model.cards.specialcards;


import yugioh.model.Duelist;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;


public class ChangeOfHeart extends SpellAndTrapCard {


    private MonsterCard hijackedCard = null;
    private Duelist originalOwner = null;


    public ChangeOfHeart() {
        super();
    }


    public void setHijackedCard(MonsterCard hijackedCard) {
        this.hijackedCard = hijackedCard;
        this.originalOwner = hijackedCard.getOwner();
        this.getOwner().getField().getMonsterCards().add(hijackedCard);
        originalOwner.getField().getMonsterCards().remove(hijackedCard);
        hijackedCard.setOwner(this.getOwner());
    }


    @Override
    public void reset() {
        if (isActive()) {
            if (hijackedCard != null) {
                hijackedCard.setOwner(originalOwner);
                originalOwner.getField().getMonsterCards().add(hijackedCard);
                this.getOwner().getField().getMonsterCards().remove(hijackedCard);
                hijackedCard = null;
                originalOwner = null;
            }
            this.getOwner().getField().getSpellAndTrapCards().remove(this);
            this.getOwner().getField().getGraveyard().add(this);
        }
    }
}