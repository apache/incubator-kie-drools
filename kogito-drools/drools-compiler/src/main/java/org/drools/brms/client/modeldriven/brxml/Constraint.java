package org.drools.brms.client.modeldriven.brxml;

/**
 * This represents a contraint on a fact.
 * Can also include optional "connective constraints" that extend the options for matches.
 * @author Michael Neale
 *
 */
public class Constraint
    implements
    PortableObject {
    

    /**
     * This is used only when constraint is first created. 
     * This means that there is no value yet for the constraint.
     */
    public static final int    TYPE_UNDEFINED = 0;
    
    /**
     * This may be string, or number, anything really. 
     */
    public static final int    TYPE_LITERAL   = 1;
    
    /**
     * This is when it is set to a valid previously bound variable.
     */
    public static final int    TYPE_VARIABLE  = 2;
    
    /**
     * This is for a "formula" that calculates a value.
     */
    public static final int    TYPE_RET_VALUE = 3;
    
    /**
     * This is not used yet. ENUMs are not suitable for business rules
     * until we can get data driven non code enums.
     */
    public static final int    TYPE_ENUM      = 4;
    
    /**
     * The fieldName and fieldBinding is not used in the case of a predicate. 
     */
    public static final int    TYPE_PREDICATE = 5;

    public String                 fieldBinding;
    public String                 fieldName;
    public String                 operator;
    public String                 value;
    public int                    type;

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
