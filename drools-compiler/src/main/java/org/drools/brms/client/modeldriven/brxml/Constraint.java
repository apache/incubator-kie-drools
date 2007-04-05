package org.drools.brms.client.modeldriven.brxml;



/**
 * This represents a contraint on a fact.
 * Can also include optional "connective constraints" that extend the options for matches.
 * @author Michael Neale
 *
 */
public class Constraint
    extends
    IConstraint  {


    public String                 fieldBinding;
    public String                 fieldName;
    public String                 operator;


    public ConnectiveConstraint[] connectives;

    public Constraint(String field) {
        this.fieldName = field;
    }

    public Constraint() {
    }

    /**
     * This adds a new connective.
     *
     */
    public void addNewConnective() {
        if ( connectives == null ) {
            connectives = new ConnectiveConstraint[]{new ConnectiveConstraint()};
        } else {
            ConnectiveConstraint[] newList = new ConnectiveConstraint[connectives.length + 1];
            for ( int i = 0; i < connectives.length; i++ ) {
                newList[i] = connectives[i];
            }
            newList[connectives.length] = new ConnectiveConstraint();
            connectives = newList;
        }
    }

    /**
     * Returns true of there is a field binding.
     */
    public boolean isBound() {
        if (fieldBinding != null && !"".equals( fieldBinding )) {
            return true;
        } else {
            return false;
        }
    }


}
