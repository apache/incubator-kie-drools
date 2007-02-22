package org.objenesis.instantiator.sun;

import java.io.ObjectInputStream;
import java.lang.reflect.Method;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Base class for Sun 1.3 based instantiators. It initializes reflection access to static method
 * ObjectInputStream.allocateNewObject.
 * 
 * @author Leonardo Mesquita
 */
public abstract class Sun13InstantiatorBase implements ObjectInstantiator {
   protected static Method allocateNewObjectMethod = null;

   private static void initialize() {
      if(allocateNewObjectMethod == null) {
         try {
            allocateNewObjectMethod = ObjectInputStream.class.getDeclaredMethod(
               "allocateNewObject", new Class[] {Class.class, Class.class});
            allocateNewObjectMethod.setAccessible(true);
         }
         catch(Exception e) {
            throw new ObjenesisException(e);
         }
      }
   }

   protected final Class type;

   public Sun13InstantiatorBase(Class type) {
      this.type = type;
      initialize();
   }

   public abstract Object newInstance();

}
