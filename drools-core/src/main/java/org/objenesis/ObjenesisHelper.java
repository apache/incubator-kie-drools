package org.objenesis;

import java.io.Serializable;

import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Use Objenesis in a static way. <strong>It is strongly not recommended to use this class.</strong>
 * 
 * @author Henri Tremblay
 */
public final class ObjenesisHelper {

    private static final Objenesis OBJENESIS_STD        = new ObjenesisStd();

    private static final Objenesis OBJENESIS_SERIALIZER = new ObjenesisSerializer();

    private ObjenesisHelper() {
    }

    /**
     * Will create a new object without any constructor being called
     * 
     * @param clazz Class to instantiate
     * @return New instance of clazz
     */
    public static final Object newInstance(final Class clazz) {
        return OBJENESIS_STD.newInstance( clazz );
    }

    /**
     * Will create an object just like it's done by ObjectInputStream.readObject (the default
     * constructor of the first non serializable class will be called)
     * 
     * @param clazz Class to instantiate
     * @return New instance of clazz
     */
    public static final Serializable newSerializableInstance(final Class clazz) {
        return (Serializable) OBJENESIS_SERIALIZER.newInstance( clazz );
    }

    /**
     * Will pick the best instantiator for the provided class. If you need to create a lot of
     * instances from the same class, it is way more efficient to create them from the same
     * ObjectInstantiator than calling {@link #newInstance(Class)}.
     * 
     * @param clazz Class to instantiate
     * @return Instantiator dedicated to the class
     */
    public static final ObjectInstantiator getInstantiatorOf(final Class clazz) {
        return OBJENESIS_STD.getInstantiatorOf( clazz );
    }

    /**
     * Same as {@link #getInstantiatorOf(Class)} but providing an instantiator emulating
     * ObjectInputStream.readObject behavior.
     * 
     * @see #newSerializableInstance(Class)
     * @param clazz Class to instantiate
     * @return Instantiator dedicated to the class
     */
    public static final ObjectInstantiator getSerializableObjectInstantiatorOf(final Class clazz) {
        return OBJENESIS_SERIALIZER.getInstantiatorOf( clazz );
    }
}
