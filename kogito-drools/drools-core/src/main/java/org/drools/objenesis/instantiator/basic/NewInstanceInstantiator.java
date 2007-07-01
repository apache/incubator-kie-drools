package org.drools.objenesis.instantiator.basic;

import org.drools.objenesis.ObjenesisException;
import org.drools.objenesis.instantiator.ObjectInstantiator;

/**
 * The simplest instantiator - simply calls Class.newInstance(). This can deal with default public
 * constructors, but that's about it.
 * 
 * @see ObjectInstantiator
 */
public class NewInstanceInstantiator
    implements
    ObjectInstantiator {

    private final Class type;

    public NewInstanceInstantiator(final Class type) {
        this.type = type;
    }

    public Object newInstance() {
        try {
            return this.type.newInstance();
        } catch ( final Exception e ) {
            throw new ObjenesisException( e );
        }
    }

}
