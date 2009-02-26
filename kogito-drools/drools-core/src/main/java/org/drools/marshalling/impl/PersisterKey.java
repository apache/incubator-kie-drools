package org.drools.marshalling.impl;

public interface PersisterKey {
   public boolean equal(Object object1, Object object2);
   
   public int hashCode(Object object);
}
