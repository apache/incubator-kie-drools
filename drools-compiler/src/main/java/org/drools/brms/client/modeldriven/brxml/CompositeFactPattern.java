package org.drools.brms.client.modeldriven.brxml;

/**
 * Represents first order logic like Or, Not, Exists.
 * 
 * @author Michael Neale
 */
public class CompositeFactPattern
    implements
    IPattern {
    public static final String COMPOSITE_TYPE_NOT    = "not";
    public static final String COMPOSITE_TYPE_EXISTS = "exists";
    public static final String COMPOSITE_TYPE_OR     = "or";

    /**
     * this will one of: [Not, Exist, Or]
     */
    public String              type;
    
    /**
     * The patterns.
     */
    public FactPattern[]       patterns;

    
    /**
     * This type should be from the contants in this class of course.
     */
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
