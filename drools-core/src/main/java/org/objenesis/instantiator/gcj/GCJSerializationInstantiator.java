package org.objenesis.instantiator.gcj;

import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.SerializationInstantiatorHelper;

/**
 * Instantiates a class by making a call to internal GCJ private methods. It is only supposed to
 * work on GCJ JVMs. This instantiator will create classes in a way compatible with serialization,
 * calling the first non-serializable superclass' no-arg constructor.
 * 
 * @author Leonardo Mesquita
 * @see org.objenesis.instantiator.ObjectInstantiator
 */
public class GCJSerializationInstantiator extends GCJInstantiatorBase {
   private Class superType;

   public GCJSerializationInstantiator(Class type) {
      super(type);
      this.superType = SerializationInstantiatorHelper.getNonSerializableSuperClass(type);
   }

   public Object newInstance() {
      try {
         return newObjectMethod.invoke(dummyStream, new Object[] {type, superType});
      }
      catch(Exception e) {
         throw new ObjenesisException(e);
      }
   }

}
