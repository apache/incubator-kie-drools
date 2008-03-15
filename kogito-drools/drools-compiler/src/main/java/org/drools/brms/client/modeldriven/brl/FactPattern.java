package org.drools.brms.client.modeldriven.brl;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;

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

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        constraintList  = (CompositeFieldConstraint)in.readObject();
        factType  = (String)in.readObject();
        boundName  = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(constraintList);
        out.writeObject(factType);
        out.writeObject(boundName);
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
        if ( this.boundName != null && !"".equals( this.boundName ) ) {
            return true;
        } else {
            return false;
        }
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
        } else {
            return this.constraintList.constraints;
        }
    }

}
