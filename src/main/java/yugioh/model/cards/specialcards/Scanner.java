package yugioh.model.cards.specialcards;


import yugioh.model.Duelist;
import yugioh.model.Field;
import yugioh.model.cards.MonsterCard;


import java.util.Objects;


public class Scanner extends MonsterCard {


    private MonsterCard cardReplaced;
    private Duelist originalOwner;


    public Scanner() {
        super();
    }


    public void setCardReplaced(MonsterCard cardReplaced) {
        originalOwner = cardReplaced.getOwner();
        cardReplaced.setOwner(this.getOwner());
        this.cardReplaced = cardReplaced;
    }


    @Override
    public void reset() {
        if (cardReplaced == null) return;
        super.reset();
        Field field = this.getOwner().getField();
        if (Objects.nonNull(cardReplaced)) {
            if (field.getMonsterCards().contains(cardReplaced)) {
                field.getMonsterCards().remove(cardReplaced);
                field.getMonsterCards().add(this);
            } else {
                field.getGraveyard().remove(cardReplaced);
                field.getGraveyard().add(this);
            }
            if (Objects.nonNull(originalOwner)) {
                originalOwner.getField().getGraveyard().add(cardReplaced);
                cardReplaced.setOwner(originalOwner);
                originalOwner = null;
            }
            cardReplaced = null;
        }
    }
}