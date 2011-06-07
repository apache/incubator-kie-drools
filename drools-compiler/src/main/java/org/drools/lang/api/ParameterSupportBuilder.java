package org.drools.lang.api;

/**
 * An interface for objects that support parameters, like
 * functions and queries
 */
public interface ParameterSupportBuilder<P extends DescrBuilder<?, ?>> {

    public P parameter( String type, String variable );

}
