package org.kie.dmn.model.api;

public enum HitPolicy {

    UNIQUE( "UNIQUE" ),
    FIRST( "FIRST" ),
    PRIORITY( "PRIORITY" ),
    ANY( "ANY" ),
    COLLECT( "COLLECT" ),
    RULE_ORDER( "RULE ORDER" ),
    OUTPUT_ORDER( "OUTPUT ORDER" );

    private final String value;

    HitPolicy( final String v ) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static HitPolicy fromValue( final String v ) {
        for ( HitPolicy c : HitPolicy.values() ) {
            if ( c.value.equals( v ) ) {
                return c;
            }
        }
        throw new IllegalArgumentException( v );
    }

    public boolean isMultiHit() {
        switch ( this ) {
            case RULE_ORDER:
            case OUTPUT_ORDER:
            case COLLECT:
                return true;
            default:
                return false;
        }
    }
}
