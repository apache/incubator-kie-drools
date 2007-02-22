package org.objenesis.instantiator.basic;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * The simplest instantiator - simply calls Class.newInstance(). This can deal with default public
 * constructors, but that's about it.
 * 
 * @see ObjectInstantiator
 */
public class NewInstanceInstantiator implements ObjectInstantiator {

   private final Class type;

   public NewInstanceInstantiator(Class type) {
      this.type = type;
   }

   public Object newInstance() {
      try {
         return type.newInstance();
      }      
      catch(Exception e) {
         throw new ObjenesisException(e);
      }
   }

}
