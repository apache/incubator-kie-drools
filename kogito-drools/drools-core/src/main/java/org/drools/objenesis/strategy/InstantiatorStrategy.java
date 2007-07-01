package org.drools.objenesis.strategy;

import org.drools.objenesis.instantiator.ObjectInstantiator;

/**
 * Defines a strategy to determine the best instantiator for a class.
 */
public interface InstantiatorStrategy {

    /**
     * Create a dedicated instantiator for the given class
     * 
     * @param type Class that will be instantiate
     * @return Dedicated instantiator
     */
    ObjectInstantiator newInstantiatorOf(Class type);
}
