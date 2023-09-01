package org.drools.core;

/**
 * This enum represents all engine supported belief systems
 */
public enum BeliefSystemType {

    SIMPLE("simple"),
    JTMS("jtms"),
    DEFEASIBLE("defeasible");

    private String string;
    BeliefSystemType( String string ) {
        this.string = string;
    }
    
    public String toExternalForm() {
        return this.string;
    }
    
    public String toString() {
        return this.string;
    }
    
    public String getId() {
        return this.string;
    }
    
    public static BeliefSystemType resolveBeliefSystemType(String id) {
        if( SIMPLE.getId().equalsIgnoreCase( id ) ) {
            return SIMPLE;
        } else if( JTMS.getId().equalsIgnoreCase( id ) ) {
            return JTMS;
        } else if( DEFEASIBLE.getId().equalsIgnoreCase( id ) ) {
            return DEFEASIBLE;
        }
        throw new IllegalArgumentException( "Illegal enum value '" + id + "' for BeliefSystem" );
    }

}
