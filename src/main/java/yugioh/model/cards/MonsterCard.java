package yugioh.model.cards;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import yugioh.model.MonsterCardModeInField;

@Getter
@Setter
public class MonsterCard extends Card {

    public static final int ONE_TRIBUTE_BOUND = 5;
    public static final int TWO_TRIBUTES_BOUND = 7;
    @Expose
    protected int classAttackPower;
    @Expose
    protected int classDefensePower;
    protected int thisCardAttackPower;
    protected int thisCardDefensePower;
    @Expose
    protected int level;
    @Expose
    protected String monsterType;
    @Expose
    protected String cardType;
    @Expose
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
        this.thisCardDefensePower = this.classDefensePower;
        this.thisCardAttackPower = this.classAttackPower;
        this.hasBeenUsedInThisTurn = false;
        this.isAbleToBeRemoved = true;
        this.isChangedPosition = false;
        this.attacked = false;
        this.hasBeenSetOrSummoned = false;
        //TODO is it enough?
    }

    @Override
    public String toString() {
        return "Name: " + this.name + "\nLevel: " + this.level + "\nType: " + this.monsterType
                +"\nATK: " + getThisCardAttackPower() + "\nDEF: " + this.thisCardDefensePower + "\nDescription: " + this.description;
    }
}