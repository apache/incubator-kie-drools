package org.drools.base.base;

import java.io.Externalizable;

/**
 * Semantic object type differentiator.
 */
public interface ObjectType
    extends
    Externalizable {

    boolean isAssignableFrom(Class<?> clazz);

    boolean isAssignableTo(Class<?> clazz);

    boolean isAssignableFrom(ObjectType objectType);

    /**
     * Returns true if the object type represented by this object
     * is an event object type. False otherwise.
     * @return
     */
    boolean isEvent();

    ValueType getValueType();

    Object getTypeKey();

    String getClassName();

    boolean hasField(String name);

    boolean isTemplate();
}
