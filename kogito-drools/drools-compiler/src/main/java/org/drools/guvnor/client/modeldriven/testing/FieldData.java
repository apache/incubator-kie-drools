package org.drools.guvnor.client.modeldriven.testing;

import org.drools.guvnor.client.modeldriven.brl.PortableObject;

public class FieldData implements PortableObject {

    /** the name of the field */
    public String name;

    /** The value of the field to be set to.
     * This will either be a literal value (which will be coerced by MVEL).
     * Or if it starts with an "=" then it is an EL that will be evaluated to yield a value.
     */
    public String value;
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
  

    public FieldData() {}
    public FieldData(String name, String value) {
        this.name = name;
        this.value = value;
    }



}
