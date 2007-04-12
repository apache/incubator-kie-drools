package org.objenesis.instantiator;

/**
 * The instantiator that always return a null instance
 * 
 * @author Henri Tremblay
 */
public class NullInstantiator
    implements
    ObjectInstantiator {

    /**
     * @return Always null
     */
    public Object newInstance() {
        return null;
    }
}
