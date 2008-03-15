package org.drools.brms.client.modeldriven.brl;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;

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
     * This is not used yet. ENUMs are not suitable for business rules
     * until we can get data driven non code enums.
     */
    public static final int TYPE_ENUM      = 4;

    /**
     * The fieldName and fieldBinding is not used in the case of a predicate.
     */
    public static final int TYPE_PREDICATE = 5;

    public String           value;
    public int              constraintValueType;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        value   = (String)in.readObject();
        constraintValueType = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(value);
        out.writeInt(constraintValueType);
    }
}
