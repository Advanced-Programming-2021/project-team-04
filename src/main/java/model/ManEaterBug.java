package model;

public class ManEaterBug extends MonsterCard {

    public ManEaterBug() {
        super();
        setName("Man-Eater Bug");
        setLevel(2);
        setMonsterType("Insect");
        setClassAttackPower(450);
        setClassDefensePower(600);
        setThisCardAttackPower(450);
        setThisCardDefensePower(600);
        setPrice(600);
        setDescription();
    }

    private void setDescription() {
        this.description = "FLIP: Target 1 monster on the field; destroy that target.";
    }


    public void setMonsterCardModeInField(MonsterCardModeInField monsterCardModeInField) {
        if (this.monsterCardModeInField == MonsterCardModeInField.DEFENSE_FACE_DOWN &&
                (monsterCardModeInField == MonsterCardModeInField.ATTACK_FACE_UP ||
                        monsterCardModeInField == MonsterCardModeInField.DEFENSE_FACE_UP)) {
            //TODO: call method in game controller to kill someone
        }
        this.monsterCardModeInField = monsterCardModeInField;
    }
}
