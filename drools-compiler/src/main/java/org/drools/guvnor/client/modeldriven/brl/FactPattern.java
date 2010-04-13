package org.drools.guvnor.client.modeldriven.brl;


import java.util.List;

/**
 * A fact pattern is a declaration of a fact type, and its constraint,
 * and perhaps a variable that is it bound to
 * It is the equivalent of a "pattern" in drools terms.
 * @author Michael Neale
 *
 */
public class FactPattern
    implements
    IPattern {

    public CompositeFieldConstraint constraintList;
    public String       factType;
    public String       boundName;

    public FactPattern() {
        //this.constraints = new CompositeFieldConstraint();
    }

    public FactPattern(final String factType) {
        this.factType = factType;
        //this.constraints = new CompositeFieldConstraint();
    }


    /**
     * This will add a top level constraint.
     */
    public void addConstraint(final FieldConstraint constraint) {
        if (constraintList == null) constraintList = new CompositeFieldConstraint();
        this.constraintList.addConstraint( constraint );
    }

    public void removeConstraint(final int idx) {
        this.constraintList.removeConstraint( idx );
    }

    /**
     * Returns true if there is a variable bound to this fact.
     */
    public boolean isBound() {
        return this.boundName != null && !"".equals( this.boundName ) ;
    }

    /**
     * This will return the list of field constraints that are in the root
     * CompositeFieldConstraint object.
     * If there is no root, then an empty array will be returned.
     *
     * @return an empty array, or the list of constraints (which may be composites).
     */
    public FieldConstraint[] getFieldConstraints() {
        if (this.constraintList == null) {
            return new FieldConstraint[0];
        } 
        return this.constraintList.constraints;
    }

    public void setFieldConstraints(final List sortedConstraints) {
        if (sortedConstraints != null) {
            if (this.constraintList != null) {
                this.constraintList.constraints = new FieldConstraint[sortedConstraints.size()];
                for (int i = 0; i < sortedConstraints.size(); i++) {
                    this.constraintList.constraints[i] = (FieldConstraint) sortedConstraints.get(i);
                }
            } else if (sortedConstraints.size() > 0) {
                throw new IllegalStateException("Cannot have constraints if constraint list is null.");
            }
            } else {
                this.constraintList.constraints = null;
            }
        }
}
