package org.drools.objenesis.instantiator.gcj;

import org.drools.objenesis.ObjenesisException;
import org.drools.objenesis.instantiator.SerializationInstantiatorHelper;

/**
 * Instantiates a class by making a call to internal GCJ private methods. It is only supposed to
 * work on GCJ JVMs. This instantiator will create classes in a way compatible with serialization,
 * calling the first non-serializable superclass' no-arg constructor.
 * 
 * @author Leonardo Mesquita
 * @see org.drools.objenesis.instantiator.ObjectInstantiator
 */
public class GCJSerializationInstantiator extends GCJInstantiatorBase {
    private Class superType;

    public GCJSerializationInstantiator(final Class type) {
        super( type );
        this.superType = SerializationInstantiatorHelper.getNonSerializableSuperClass( type );
    }

    public Object newInstance() {
        try {
            return newObjectMethod.invoke( dummyStream,
                                           new Object[]{this.type, this.superType} );
        } catch ( final Exception e ) {
            throw new ObjenesisException( e );
        }
    }

}
