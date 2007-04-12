package org.drools.brms.client.modeldriven.brxml;

/**
 * A fact pattern is a declaration of a fact type, and its constraint,
 * and perhaps a variable that is it bound to
 * It is the equivalent of a "column" in drools terms. 
 * @author Michael Neale
 *
 */
public class FactPattern
    implements
    IPattern {

    public Constraint[] constraints;
    public String       factType;
    public String       boundName;

    public FactPattern() {
        this.constraints = new Constraint[0];
    }

    public FactPattern(final String factType) {
        this.factType = factType;
        this.constraints = new Constraint[0];
    }

    public void addConstraint(final Constraint constraint) {
        if ( this.constraints == null ) {
            this.constraints = new Constraint[1];
            this.constraints[0] = constraint;
        } else {
            final Constraint[] newList = new Constraint[this.constraints.length + 1];
            for ( int i = 0; i < this.constraints.length; i++ ) {
                newList[i] = this.constraints[i];
            }
            newList[this.constraints.length] = constraint;
            this.constraints = newList;
        }
    }

    public void removeConstraint(final int idx) {
        //Unfortunately, this is kinda duplicate code with other methods, 
        //but with typed arrays, and GWT, its not really possible to do anything "better" 
        //at this point in time. 
        final Constraint[] newList = new Constraint[this.constraints.length - 1];
        int newIdx = 0;
        for ( int i = 0; i < this.constraints.length; i++ ) {

            if ( i != idx ) {
                newList[newIdx] = this.constraints[i];
                newIdx++;
            }

        }
        this.constraints = newList;

    }

    /**
     * Returns true if there is a variable bound to this fact.
     */
    public boolean isBound() {
        if ( this.boundName != null && !"".equals( this.boundName ) ) {
            return true;
        } else {
            return false;
        }
    }

}
