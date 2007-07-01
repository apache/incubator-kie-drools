package org.drools.objenesis.instantiator.sun;

import java.io.ObjectInputStream;
import java.lang.reflect.Method;

import org.drools.objenesis.ObjenesisException;
import org.drools.objenesis.instantiator.ObjectInstantiator;

/**
 * Base class for Sun 1.3 based instantiators. It initializes reflection access to static method
 * ObjectInputStream.allocateNewObject.
 * 
 * @author Leonardo Mesquita
 */
public abstract class Sun13InstantiatorBase
    implements
    ObjectInstantiator {
    protected static Method allocateNewObjectMethod = null;

    private static void initialize() {
        if ( allocateNewObjectMethod == null ) {
            try {
                allocateNewObjectMethod = ObjectInputStream.class.getDeclaredMethod( "allocateNewObject",
                                                                                     new Class[]{Class.class, Class.class} );
                allocateNewObjectMethod.setAccessible( true );
            } catch ( final Exception e ) {
                throw new ObjenesisException( e );
            }
        }
    }

    protected final Class type;

    public Sun13InstantiatorBase(final Class type) {
        this.type = type;
        initialize();
    }

    public abstract Object newInstance();

}
