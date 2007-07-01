package org.drools.objenesis.instantiator.sun;

import java.lang.reflect.Constructor;

import org.drools.objenesis.ObjenesisException;
import org.drools.objenesis.instantiator.ObjectInstantiator;

import sun.reflect.ReflectionFactory;

/**
 * Instantiates an object, WITHOUT calling it's constructor, using internal
 * sun.reflect.ReflectionFactory - a class only available on JDK's that use Sun's 1.4 (or later)
 * Java implementation. This is the best way to instantiate an object without any side effects
 * caused by the constructor - however it is not available on every platform.
 * 
 * @see ObjectInstantiator
 */
public class SunReflectionFactoryInstantiator
    implements
    ObjectInstantiator {

    private final Constructor mungedConstructor;

    public SunReflectionFactoryInstantiator(final Class type) {

        final ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
        Constructor javaLangObjectConstructor;

        try {
            javaLangObjectConstructor = Object.class.getConstructor( (Class[]) null );
        } catch ( final NoSuchMethodException e ) {
            throw new Error( "Cannot find constructor for java.lang.Object!" );
        }
        this.mungedConstructor = reflectionFactory.newConstructorForSerialization( type,
                                                                              javaLangObjectConstructor );
        this.mungedConstructor.setAccessible( true );
    }

    public Object newInstance() {
        try {
            return this.mungedConstructor.newInstance( (Object[]) null );
        } catch ( final Exception e ) {
            throw new ObjenesisException( e );
        }
    }
}
