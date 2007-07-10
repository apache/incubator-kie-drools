/**
 * COPYRIGHT & LICENSE
 *
 * This code is Copyright (c) 2006 BEA Systems, inc. It is provided free, as-is and without any warranties for the purpose of
 * inclusion in Objenesis or any other open source project with a FSF approved license, as long as this notice is not
 * removed. There are no limitations on modifying or repackaging the code apart from this. 
 *
 * BEA does not guarantee that the code works, and provides no support for it. Use at your own risk.
 *
 * Originally developed by Leonardo Mesquita. Copyright notice added by Henrik Sthl, BEA JRockit Product Manager.
 *  
 */

package org.drools.objenesis.instantiator.jrockit;

import java.lang.reflect.Method;

import org.drools.objenesis.ObjenesisException;
import org.drools.objenesis.instantiator.ObjectInstantiator;

/**
 * Instantiates a class by making a call to internal JRockit private methods. It is only supposed to
 * work on JRockit 1.4.2 JVMs prior to release R25.1. From release R25.1 on, JRockit supports
 * sun.reflect.ReflectionFactory, making this "trick" unnecessary. This instantiator will not call
 * any constructors.
 * 
 * @author Leonardo Mesquita
 * @see org.drools.objenesis.instantiator.ObjectInstantiator
 * @see org.drools.objenesis.instantiator.sun.SunReflectionFactoryInstantiator
 */
public class JRockitLegacyInstantiator
    implements
    ObjectInstantiator {
    private static Method safeAllocObjectMethod = null;

    private static void initialize() {
        if ( safeAllocObjectMethod == null ) {
            Class memSystem;
            try {
                memSystem = Class.forName( "jrockit.vm.MemSystem" );
                safeAllocObjectMethod = memSystem.getDeclaredMethod( "safeAllocObject",
                                                                     new Class[]{Class.class} );
                safeAllocObjectMethod.setAccessible( true );
            } catch ( final Exception e ) {
                throw new ObjenesisException( e );
            }
        }
    }

    private Class type;

    public JRockitLegacyInstantiator(final Class type) {
        initialize();
        this.type = type;
    }

    public Object newInstance() {
        try {
            return safeAllocObjectMethod.invoke( null,
                                                 new Object[]{this.type} );
        } catch ( final Exception e ) {
            throw new ObjenesisException( e );
        }
    }
}
