package org.drools.mvel.integrationtests.facts;

public enum CategoryTypeEnum {
    ODD, PAIR;

    public static CategoryTypeEnum fromString(final String s) {
        if (s.equalsIgnoreCase( "odd" )) {
            return ODD;
        }
        if (s.equalsIgnoreCase( "pair" )) {
            return PAIR;
        }
        throw new RuntimeException( "Unknown category: " + s );
    }
}
