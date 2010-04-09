package org.drools.guvnor.client.modeldriven.brl;


/**
 * Represents a constraint, which may be part of a direct field constraint or a connective.
 * @author Michael Neale
 *
 */
public class ISingleFieldConstraint
    implements
    PortableObject {

    /**
     * This is used only when constraint is first created.
     * This means that there is no value yet for the constraint.
     */
    public static final int TYPE_UNDEFINED = 0;

    /**
     * This may be string, or number, anything really.
     */
    public static final int TYPE_LITERAL   = 1;

    /**
     * This is when it is set to a valid previously bound variable.
     */
    public static final int TYPE_VARIABLE  = 2;

    /**
     * This is for a "formula" that calculates a value.
     */
    public static final int TYPE_RET_VALUE = 3;

    /**
     * This is for a "expression builder" that calculates a value.
     */
    public static final int TYPE_EXPR_BUILDER = 6;

    /**
     * This is not used yet. ENUMs are not suitable for business rules
     * until we can get data driven non code enums.
     */
    public static final int TYPE_ENUM      = 4;

    /**
     * The fieldName and fieldBinding is not used in the case of a predicate.
     */
    public static final int TYPE_PREDICATE = 5;
    
    /**
     * This is for a field to be a placeholder for a template
     */
    public static final int TYPE_TEMPLATE = 7;

    public String           value;
    public int              constraintValueType;


}
