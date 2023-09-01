package org.drools.mvel.integrationtests.facts;

public class FactWithCharacter {

    private final char charValue;
    private final Character characterValue;

    public FactWithCharacter(final char charValue) {
        this.charValue = charValue;
        this.characterValue = charValue;
    }

    public char getCharValue() {
        return charValue;
    }

    public Character getCharacterValue() {
        return characterValue;
    }
}
