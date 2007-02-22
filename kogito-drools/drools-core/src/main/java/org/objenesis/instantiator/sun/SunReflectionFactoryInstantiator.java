package org.objenesis.instantiator.sun;

import java.lang.reflect.Constructor;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

import sun.reflect.ReflectionFactory;

/**
 * Instantiates an object, WITHOUT calling it's constructor, using internal
 * sun.reflect.ReflectionFactory - a class only available on JDK's that use Sun's 1.4 (or later)
 * Java implementation. This is the best way to instantiate an object without any side effects
 * caused by the constructor - however it is not available on every platform.
 * 
 * @see ObjectInstantiator
 */
public class SunReflectionFactoryInstantiator implements ObjectInstantiator {

   private final Constructor mungedConstructor;

   public SunReflectionFactoryInstantiator(Class type) {

      ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
      Constructor javaLangObjectConstructor;

      try {
         javaLangObjectConstructor = Object.class.getConstructor((Class[]) null);
      }
      catch(NoSuchMethodException e) {
         throw new Error("Cannot find constructor for java.lang.Object!");
      }
      mungedConstructor = reflectionFactory.newConstructorForSerialization(type,
         javaLangObjectConstructor);
      mungedConstructor.setAccessible(true);
   }

   public Object newInstance() {
      try {
         return mungedConstructor.newInstance((Object[]) null);
      }
      catch(Exception e) {
         throw new ObjenesisException(e);
      }
   }
}
