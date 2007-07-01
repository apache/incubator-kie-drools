package org.drools.objenesis.instantiator.sun;

import org.drools.objenesis.ObjenesisException;

/**
 * Instantiates a class by making a call to internal Sun private methods. It is only supposed to
 * work on Sun HotSpot 1.3 JVM. This instantiator will not call any constructors.
 * 
 * @author Leonardo Mesquita
 * @see org.drools.objenesis.instantiator.ObjectInstantiator
 */
public class Sun13Instantiator extends Sun13InstantiatorBase {
    public Sun13Instantiator(final Class type) {
        super( type );
    }

    public Object newInstance() {
        try {
            return allocateNewObjectMethod.invoke( null,
                                                   new Object[]{this.type, Object.class} );
        } catch ( final Exception e ) {
            throw new ObjenesisException( e );
        }
    }

}
