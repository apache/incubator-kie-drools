package org.kie.dmn.model.api;

public enum BuiltinAggregator {

    SUM,
    COUNT,
    MIN,
    MAX;

    public String value() {
        return name();
    }

    public static BuiltinAggregator fromValue( final String v ) {
        return valueOf( v );
    }

}
