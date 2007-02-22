/**
 * COPYRIGHT & LICENSE
 *
 * This code is Copyright (c) 2006 BEA Systems, inc. It is provided free, as-is and without any warranties for the purpose of
 * inclusion in Objenesis or any other open source project with a FSF approved license, as long as this notice is not
 * removed. There are no limitations on modifying or repackaging the code apart from this. 
 *
 * BEA does not guarantee that the code works, and provides no support for it. Use at your own risk.
 *
 * Originally developed by Leonardo Mesquita. Copyright notice added by Henrik Ståhl, BEA JRockit Product Manager.
 *  
 */
package org.objenesis.instantiator.jrockit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Instantiates a class by making a call to internal JRockit private methods. It is only supposed to
 * work on JRockit 7.0 JVMs, which are compatible with Java API 1.3.1. This instantiator will not
 * call any constructors.
 * 
 * @author Leonardo Mesquita
 * @see org.objenesis.instantiator.ObjectInstantiator
 */
public class JRockit131Instantiator implements ObjectInstantiator {

   private Constructor mungedConstructor;

   private static Method newConstructorForSerializationMethod;

   private static void initialize() {
      if(newConstructorForSerializationMethod == null) {
         Class cl;
         try {
            cl = Class.forName("COM.jrockit.reflect.MemberAccess");
            newConstructorForSerializationMethod = cl.getDeclaredMethod(
               "newConstructorForSerialization", new Class[] {Constructor.class, Class.class});
            newConstructorForSerializationMethod.setAccessible(true);
         }
         catch(Exception e) {
            throw new ObjenesisException(e);
         }
      }
   }

   public JRockit131Instantiator(Class type) {
      initialize();

      if(newConstructorForSerializationMethod != null) {

         Constructor javaLangObjectConstructor;

         try {
            javaLangObjectConstructor = Object.class.getConstructor((Class[]) null);
         }
         catch(NoSuchMethodException e) {
            throw new Error("Cannot find constructor for java.lang.Object!");
         }

         try {
            mungedConstructor = (Constructor) newConstructorForSerializationMethod.invoke(null,
               new Object[] {javaLangObjectConstructor, type});
         }
         catch(Exception e) {
        	 throw new ObjenesisException(e);
         }
      }

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
