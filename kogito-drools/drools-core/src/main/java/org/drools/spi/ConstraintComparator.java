package org.drools.spi;

public interface ConstraintComparator
{
    public static final int EQUAL            = 1;
    public static final int NOT_EQUAL        = 2;
    public static final int LESS             = 3;
    public static final int LESS_OR_EQUAL    = 4;
    public static final int GREATER          = 5;
    public static final int GREATER_OR_EQUAL = 6;
    
    public boolean compare(Object object1, Object object2);
        
}
