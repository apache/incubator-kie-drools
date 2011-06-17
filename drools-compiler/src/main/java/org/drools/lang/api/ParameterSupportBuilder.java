package org.drools.lang.api;

/**
 * An interface for objects that support parameters, like
 * functions and queries
 */
public interface ParameterSupportBuilder<P extends DescrBuilder<?, ?>> {

    /**
     * Adds a parameter to the parameter list
     * 
     * @param type parameter type
     * @param variable parameter id
     * 
     * @return itself
     */
    public P parameter( String type, String variable );

}
