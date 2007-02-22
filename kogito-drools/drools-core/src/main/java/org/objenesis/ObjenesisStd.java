package org.objenesis;

import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * Objenesis implementation using the {@link org.objenesis.strategy.StdInstantiatorStrategy}.
 * 
 * @author Henri Tremblay
 */
public class ObjenesisStd extends ObjenesisBase {

   /**
    * Default constructor using the {@link org.objenesis.strategy.StdInstantiatorStrategy}
    */
   public ObjenesisStd() {
      super(new StdInstantiatorStrategy());
   }

   /**
    * Instance using the {@link org.objenesis.strategy.StdInstantiatorStrategy} with or without
    * caching {@link org.objenesis.instantiator.ObjectInstantiator}s
    * 
    * @param useCache If {@link org.objenesis.instantiator.ObjectInstantiator}s should be cached
    */
   public ObjenesisStd(boolean useCache) {
      super(new StdInstantiatorStrategy(), useCache);
   }
}
