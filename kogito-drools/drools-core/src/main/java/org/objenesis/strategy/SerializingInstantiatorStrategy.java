package org.objenesis.strategy;

import java.io.NotSerializableException;
import java.io.Serializable;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.basic.ObjectStreamClassInstantiator;
import org.objenesis.instantiator.gcj.GCJSerializationInstantiator;
import org.objenesis.instantiator.sun.Sun13SerializationInstantiator;

/**
 * Guess the best serializing instantiator for a given class. The returned instantiator will
 * instantiate classes like the genuine java serialization framework (the constructor of the first
 * not serializable class will be called). Currently, the selection doesn't depend on the class. It
 * relies on the
 * <ul>
 * <li>JVM version</li>
 * <li>JVM vendor</li>
 * <li>JVM vendor version</li>
 * </ul>
 * However, instantiators are stateful and so dedicated to their class.
 * 
 * @see ObjectInstantiator
 */
public class SerializingInstantiatorStrategy extends BaseInstantiatorStrategy {

   /**
    * Return an {@link ObjectInstantiator} allowing to create instance following the java
    * serialization framework specifications.
    * 
    * @param type Class to instantiate
    * @return The ObjectInstantiator for the class
    */
   public ObjectInstantiator newInstantiatorOf(Class type) {
      if(!Serializable.class.isAssignableFrom(type)) {
         throw new ObjenesisException(new NotSerializableException(type+" not serializable"));
      }
      if(JVM_NAME.startsWith(SUN)) {
         if(VM_VERSION.startsWith("1.3")) {
            return new Sun13SerializationInstantiator(type);
         }
      }
      else if(JVM_NAME.startsWith(GNU)) {
         return new GCJSerializationInstantiator(type);
      }
      return new ObjectStreamClassInstantiator(type);
   }

}
