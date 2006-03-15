package org.drools.spi;

public interface Evaluator {
    // Operators
    public static final int EQUAL            = 1;
    public static final int NOT_EQUAL        = 10;
    public static final int LESS             = 20;
    public static final int LESS_OR_EQUAL    = 30;
    public static final int GREATER          = 40;
    public static final int GREATER_OR_EQUAL = 50;
    public static final int CONTAINS         = 60;
    
    // Types
    public static final int CHAR_TYPE        = 100;
    public static final int BYTE_TYPE        = 110;
    public static final int SHORT_TYPE       = 120;
    public static final int INTEGER_TYPE     = 130;
    public static final int LONG_TYPE        = 140;
    public static final int FLOAT_TYPE       = 150;
    public static final int DOUBLE_TYPE      = 160;
    public static final int BOOLEAN_TYPE     = 170;
    public static final int STRING_TYPE      = 180;
    public static final int DATE_TYPE        = 190;
    public static final int ARRAY_TYPE       = 200;
    public static final int OBJECT_TYPE      = 210;
    public static final int NULL_TYPE        = 300;
    
    
    public int getType();

    public int getOperator();

    /**
     * This method will apply the evaluator on the objects.
     * 
     * Typically, they follow this form:
     * Fact(object1 operator object2)
     * 
     * Where operator selects the appropraite concrete evaluator, and object1, and object2
     * are parameters to this method.
     * 
     * So Person(age < 42) will have object1==age field (of the Person fact object)
     * and "42" will be the object2 value.
     */
    public boolean evaluate(Object object1,
                            Object object2);
    
   
}
