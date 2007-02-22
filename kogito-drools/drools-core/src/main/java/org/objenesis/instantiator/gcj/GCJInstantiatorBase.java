package org.objenesis.instantiator.gcj;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Base class for GCJ-based instantiators. It initializes reflection access to method
 * ObjectInputStream.newObject, as well as creating a dummy ObjectInputStream to be used as the
 * "this" argument for the method.
 * 
 * @author Leonardo Mesquita
 */
public abstract class GCJInstantiatorBase implements ObjectInstantiator {
   protected static Method newObjectMethod = null;
   protected static ObjectInputStream dummyStream;

   private static class DummyStream extends ObjectInputStream {
      public DummyStream() throws IOException {
         super();
      }
   }

   private static void initialize() {
      if(newObjectMethod == null) {
         try {
            newObjectMethod = ObjectInputStream.class.getDeclaredMethod("newObject", new Class[] {
               Class.class, Class.class});
            newObjectMethod.setAccessible(true);
            dummyStream = new DummyStream();
         }
         catch(Exception e) {
            throw new ObjenesisException(e);
         }
      }
   }

   protected final Class type;

   public GCJInstantiatorBase(Class type) {
      this.type = type;
      initialize();
   }

   public abstract Object newInstance();
}
