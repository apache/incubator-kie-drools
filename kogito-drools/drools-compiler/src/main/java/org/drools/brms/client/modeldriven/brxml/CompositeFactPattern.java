package org.drools.brms.client.modeldriven.brxml;

/**
 * Represents first order logic like Or, Not, Exists.
 * 
 * @author Michael Neale
 */
public class CompositeFactPattern
    implements
    IPattern {
    public static final String NOT    = "not";
    public static final String EXISTS = "exists";
    public static final String OR     = "or";

    /**
     * this will one of: [Not, Exist, Or]
     */
    public String              type;
    public FactPattern[]       patterns;

    public CompositeFactPattern(final String type) {
        this.type = type;
    }

    public CompositeFactPattern() {
    }

    public void addFactPattern(final FactPattern pat) {
        if ( this.patterns == null ) {
            this.patterns = new FactPattern[0];
        }

        final FactPattern[] list = this.patterns;
        final FactPattern[] newList = new FactPattern[list.length + 1];

        for ( int i = 0; i < list.length; i++ ) {
            newList[i] = list[i];
        }
        newList[list.length] = pat;

        this.patterns = newList;
    }

}
