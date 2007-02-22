package org.objenesis.instantiator.basic;

import java.io.ObjectStreamClass;
import java.lang.reflect.Method;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Instantiates a class by using reflection to make a call to private method
 * ObjectStreamClass.newInstance, present in many JVM implementations. This instantiator will create
 * classes in a way compatible with serialization, calling the first non-serializable superclass'
 * no-arg constructor.
 * 
 * @author Leonardo Mesquita
 * @see ObjectInstantiator
 * @see java.io.Serializable
 */
public class ObjectStreamClassInstantiator implements ObjectInstantiator {

   private static Method newInstanceMethod;

   private static void initialize() {
      if(newInstanceMethod == null) {
         try {
            newInstanceMethod = ObjectStreamClass.class.getDeclaredMethod("newInstance",
               new Class[] {});
            newInstanceMethod.setAccessible(true);
         }
         catch(Exception e) {
            throw new ObjenesisException(e);
         }         
      }
   }

   private ObjectStreamClass objStreamClass;

   public ObjectStreamClassInstantiator(Class type) {
      initialize();
      objStreamClass = ObjectStreamClass.lookup(type);
   }

   public Object newInstance() {
	   
      try {
         return newInstanceMethod.invoke(objStreamClass, new Object[] {});
      }
      catch(Exception e) {
         throw new ObjenesisException(e);
      }
      
   }

}
