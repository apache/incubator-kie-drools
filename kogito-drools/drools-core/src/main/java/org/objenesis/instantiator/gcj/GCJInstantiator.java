package org.objenesis.instantiator.gcj;

import org.objenesis.ObjenesisException;

/**
 * Instantiates a class by making a call to internal GCJ private methods. It is only supposed to
 * work on GCJ JVMs. This instantiator will not call any constructors.
 * 
 * @author Leonardo Mesquita
 * @see org.objenesis.instantiator.ObjectInstantiator
 */
public class GCJInstantiator extends GCJInstantiatorBase {
    public GCJInstantiator(final Class type) {
        super( type );
    }

    public Object newInstance() {
        try {
            return newObjectMethod.invoke( dummyStream,
                                           new Object[]{this.type, Object.class} );
        } catch ( final Exception e ) {
            throw new ObjenesisException( e );
        }
    }
}
