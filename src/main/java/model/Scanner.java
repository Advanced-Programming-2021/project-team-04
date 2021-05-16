package model;

public class Scanner extends MonsterCard {

    private MonsterCard cardReplaced;
    private Duelist originalOwner;

    public Scanner() {
        super();
        setName("Scanner");
        setLevel(1);
        setMonsterType("Machine");
        setPrice(8000);
        setClassAttackPower(0);
        setClassDefensePower(0);
        setDescription();
    }

    public MonsterCard getCardReplaced() {
        return cardReplaced;
    }

    public void setCardReplaced(MonsterCard cardReplaced) {
        originalOwner = cardReplaced.getOwner();
        cardReplaced.setOwner(this.getOwner());
        this.cardReplaced = cardReplaced;
    }


    private void setDescription() {
        this.description = "Once per turn, you can select 1 of your opponent's monsters that is removed from play." +
                " Until the End Phase, this card's name is treated as the selected monster's name, " +
                "and this card has the same Attribute, Level, ATK, and DEF as the selected monster. " +
                "If this card is removed from the field while this effect is applied, remove it from play.";
    }

    public void reset() {
        Field field = this.getOwner().getField();
        if (field.getMonsterCards().contains(cardReplaced)) {
            field.getMonsterCards().remove(cardReplaced);
            field.getMonsterCards().add(this);
        } else {
            field.getGraveyard().remove(cardReplaced);
            field.getGraveyard().add(this);
        }
        originalOwner.getField().getGraveyard().add(cardReplaced);
        cardReplaced.setOwner(originalOwner);
        originalOwner = null;
        cardReplaced = null;
    }
}
