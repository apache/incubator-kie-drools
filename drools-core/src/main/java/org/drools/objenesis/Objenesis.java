package org.drools.objenesis;

import org.drools.objenesis.instantiator.ObjectInstantiator;

/**
 * Common interface to all kind of Objenesis objects
 * 
 * @author Henri Tremblay
 */
public interface Objenesis {

    /**
     * Will create a new object without any constructor being called
     * 
     * @param clazz Class to instantiate
     * @return New instance of clazz
     */
    Object newInstance(Class clazz);

    /**
     * Will pick the best instantiator for the provided class. If you need to create a lot of
     * instances from the same class, it is way more efficient to create them from the same
     * ObjectInstantiator than calling {@link #newInstance(Class)}.
     * 
     * @param clazz Class to instantiate
     * @return Instantiator dedicated to the class
     */
    ObjectInstantiator getInstantiatorOf(Class clazz);
}
