package org.kie.dmn.model.api;

public enum AssociationDirection {

    NONE( "None" ),
    ONE( "One" ),
    BOTH( "Both" );

    private final String value;

    AssociationDirection( final String v ) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AssociationDirection fromValue( final String v ) {
        for ( AssociationDirection c : AssociationDirection.values() ) {
            if ( c.value.equals( v ) ) {
                return c;
            }
        }
        throw new IllegalArgumentException( v );
    }

}
