package model;

public enum CardStatusInField {
    MONSTER_FIELD, SPELL_FIELD, FIELD_ZONE, HAND;

    public static CardStatusInField getCardStatusInField(String string) {
        if (string.contains("-m"))
            return MONSTER_FIELD;
        if (string.contains("-s"))
            return SPELL_FIELD;
        if (string.contains("-f"))
            return FIELD_ZONE;
        if (string.contains("-h"))
            return HAND;
        return null;
    }

}
