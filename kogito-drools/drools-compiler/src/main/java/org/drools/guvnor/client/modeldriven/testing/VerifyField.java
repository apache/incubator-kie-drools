package org.drools.guvnor.client.modeldriven.testing;

import org.drools.guvnor.client.modeldriven.brl.PortableObject;

public class VerifyField implements PortableObject {

    public String fieldName;
    public String expected;

    public String actualResult;
    public Boolean successResult;

    /**
     * This is a natural language explanation of the outcome for reporting purposes.
     */
    public String explanation;

    /**
     * Operator is generally "==" or "!="  - an MVEL operator.
     */
    public String operator = "==";
    public long nature;
     /**
      * This is used only when action is first created.
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
     public static final int TYPE_FORMULA = 3;

     /**
      * This is not used yet. ENUMs are not suitable for business rules
      * until we can get data driven non code enums.
      */
     public static final int TYPE_ENUM      = 4;

     /**
      * The fieldName and fieldBinding is not used in the case of a predicate.
      */
     public static final int TYPE_PREDICATE = 5;

 
    public VerifyField() {}

    public VerifyField(String fieldName, String expected, String operator) {
        this.fieldName = fieldName;
        this.expected = expected;
        this.operator = operator;
    }

}
