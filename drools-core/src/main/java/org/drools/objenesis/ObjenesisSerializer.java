package org.drools.objenesis;

import org.drools.objenesis.strategy.SerializingInstantiatorStrategy;

/**
 * Objenesis implementation using the {@link SerializingInstantiatorStrategy}.
 * 
 * @author Henri Tremblay
 */
public class ObjenesisSerializer extends ObjenesisBase {

    /**
     * Default constructor using the {@link org.drools.objenesis.strategy.SerializingInstantiatorStrategy}
     */
    public ObjenesisSerializer() {
        super( new SerializingInstantiatorStrategy() );
    }

    /**
     * Instance using the {@link org.drools.objenesis.strategy.SerializingInstantiatorStrategy} with or without caching
     * {@link org.drools.objenesis.instantiator.ObjectInstantiator}s
     * 
     * @param useCache If {@link org.drools.objenesis.instantiator.ObjectInstantiator}s should be cached
     */
    public ObjenesisSerializer(final boolean useCache) {
        super( new SerializingInstantiatorStrategy(),
               useCache );
    }
}
