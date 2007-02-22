package org.objenesis.instantiator.basic;

import java.lang.reflect.Constructor;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Instantiates a class by grabbing the no args constructor and calling Constructor.newInstance().
 * This can deal with default public constructors, but that's about it.
 * 
 * @see ObjectInstantiator
 */
public class ConstructorInstantiator implements ObjectInstantiator {

   protected Constructor constructor;

   public ConstructorInstantiator(Class type) {
      try {
         constructor = type.getDeclaredConstructor((Class[]) null);
      }
      catch(Exception e) {
         throw new ObjenesisException(e);
      }
   }

   public Object newInstance() {
      try {
         return constructor.newInstance((Object[]) null);
      }
      catch(Exception e) {
          throw new ObjenesisException(e);
      }
   }

}
