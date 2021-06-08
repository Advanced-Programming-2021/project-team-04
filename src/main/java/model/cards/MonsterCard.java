package model.cards;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import model.MonsterCardModeInField;
import model.cards.Card;

@Getter
@Setter
public class MonsterCard extends Card {

    @Expose()
    protected int classAttackPower;
    @Expose()
    protected int classDefensePower;
    protected int thisCardAttackPower;
    protected int thisCardDefensePower;
    @Expose()
    protected int level;
    @Expose()
    protected String monsterType;
    @Expose()
    protected String cardType;
    @Expose()
    protected String attribute;
    protected MonsterCardModeInField monsterCardModeInField;
    protected boolean isAbleToBeRemoved = true;
    protected boolean isAbleToAttack = true;
    protected boolean isChangedPosition = false;
    protected boolean attacked = false;

    public MonsterCard() {
        super();
    }

    public void setMonsterCardModeInField(MonsterCardModeInField monsterCardModeInField) {
        this.monsterCardModeInField = monsterCardModeInField;
        this.isChangedPosition = true;
    }

    public void changeAttackPower(int amount) {
        this.thisCardAttackPower += amount;
    }

    @Override
    public void reset() {
        this.thisCardDefensePower = classDefensePower;
        this.thisCardAttackPower = classAttackPower;
        this.hasBeenUsedInThisTurn = false;
        this.isAbleToBeRemoved = true;
        this.isChangedPosition = false;
        this.attacked = false;
        this.hasBeenSetOrSummoned = false;
//        this.ownerUsername = MainController.getInstance().getLoggedIn().getUsername();
        //TODO is it enough?
    }

    @Override
    public String toString() {
        return "Name: " + this.name + "\nLevel: " + this.level + "\nType: " + this.monsterType
                +"\nATK: " + this.classAttackPower + "\nDEF: " + this.classDefensePower
                + "\nDescription: " + this.description;
    }
}
