package org.drools.lang.api;

/**
 * An interface for objects that support parameters, like
 * functions and queries
 */
public interface ParameterSupportBuilder<T extends DescrBuilder<?>> {

    public T parameter( String type, String variable );

}
