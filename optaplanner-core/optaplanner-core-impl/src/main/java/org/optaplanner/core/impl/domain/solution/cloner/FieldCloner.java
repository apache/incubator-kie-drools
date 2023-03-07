package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;

interface FieldCloner {

    static Object getGenericFieldValue(Object bean, Field field) {
        try {
            return field.get(bean);
        } catch (IllegalAccessException e) {
            throw createExceptionOnRead(bean, field, e);
        }
    }

    static RuntimeException createExceptionOnRead(Object bean, Field field, Exception rootCause) {
        return new IllegalStateException("The class (" + bean.getClass() + ") has a field (" + field
                + ") which cannot be read to create a planning clone.", rootCause);
    }

    static void setGenericFieldValue(Object bean, Field field, Object value) {
        try {
            field.set(bean, value);
        } catch (IllegalAccessException e) {
            throw createExceptionOnWrite(bean, field, value, e);
        }
    }

    static RuntimeException createExceptionOnWrite(Object bean, Field field, Object value, Exception rootCause) {
        return new IllegalStateException("The class (" + bean.getClass() + ") has a field (" + field
                + ") which cannot be written with the value (" + value + ") to create a planning clone.", rootCause);
    }

    /**
     * Reads field value from original and store it in clone.
     *
     * @param deepCloningUtils never null
     * @param original never null
     * @param clone never null
     * @return null if the cloner performed the clone
     * @throws RuntimeException if reflective field read or write fails
     */
    <C> Unprocessed clone(DeepCloningUtils deepCloningUtils, C original, C clone);

}
