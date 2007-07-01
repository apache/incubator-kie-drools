package org.drools.objenesis.instantiator.sun;

import org.drools.objenesis.ObjenesisException;
import org.drools.objenesis.instantiator.SerializationInstantiatorHelper;

/**
 * Instantiates a class by making a call to internal Sun private methods. It is only supposed to
 * work on Sun HotSpot 1.3 JVM. This instantiator will create classes in a way compatible with
 * serialization, calling the first non-serializable superclass' no-arg constructor.
 * 
 * @author Leonardo Mesquita
 * @see org.drools.objenesis.instantiator.ObjectInstantiator
 */
public class Sun13SerializationInstantiator extends Sun13InstantiatorBase {
    private final Class superType;

    public Sun13SerializationInstantiator(final Class type) {
        super( type );
        this.superType = SerializationInstantiatorHelper.getNonSerializableSuperClass( type );
    }

    public Object newInstance() {
        try {
            return allocateNewObjectMethod.invoke( null,
                                                   new Object[]{this.type, this.superType} );
        } catch ( final Exception e ) {
            throw new ObjenesisException( e );
        }
    }

}
