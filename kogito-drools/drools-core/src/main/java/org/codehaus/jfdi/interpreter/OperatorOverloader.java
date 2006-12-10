package org.codehaus.jfdi.interpreter;

/**
 * This interface is to be implemented by users, to provide declarations of operator overloading.
 * The engine can use this to apply operators accordingly when it is registered. 
 * 
 * @author Michael Neale
 */
public interface OperatorOverloader {

    Object plus(Object left, Object right);
    Object minus(Object left, Object right);
    Object multiply(Object left, Object right);
    Object divide(Object left, Object right);
    
    Class getApplicableType();
    
    
}
