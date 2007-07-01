package org.drools.objenesis.instantiator;

/**
 * Instantiates a new object.
 */
public interface ObjectInstantiator {

    /**
     * Returns a new instance of an object. The returned object's class is defined by the
     * implementation.
     * 
     * @return A new instance of an object.
     */
    Object newInstance();

}
