package org.kie.dmn.model.api;

public enum DecisionTableOrientation {

    RULE_AS_ROW( "Rule-as-Row" ),
    RULE_AS_COLUMN( "Rule-as-Column" ),
    CROSS_TABLE( "CrossTable" );

    private final String value;

    DecisionTableOrientation( String v ) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DecisionTableOrientation fromValue( final String v ) {
        for ( DecisionTableOrientation c : DecisionTableOrientation.values() ) {
            if ( c.value.equals( v ) ) {
                return c;
            }
        }
        throw new IllegalArgumentException( v );
    }

}
