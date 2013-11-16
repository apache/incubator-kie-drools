package org.drools.core.beliefsystem.defeasible;

public enum DefeasibilityStatus {
    DEFINITELY( "strict" ),
    DEFEASIBLY( "defeasibly" ),
    DEFEATEDLY( "defeater" ),
    UNDECIDABLY( "nil" );

    private String id;

    DefeasibilityStatus( String id ) {
        this.id = id;
    }

    public String getValue() {
        return id;
    }

    public static DefeasibilityStatus resolve( Object value ) {
        if ( value == null ) {
            return null;
        } else if ( DEFINITELY.id.equals( value ) ) {
            return DEFINITELY;
        } else if ( DEFEASIBLY.id.equals( value ) ) {
            return DEFEASIBLY;
        }  else if ( DEFEATEDLY.id.equals( value ) ) {
            return DEFEATEDLY;
        }
        return null;
    }
}
