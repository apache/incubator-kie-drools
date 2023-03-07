package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;
import java.util.Objects;

final class FloatFieldCloner implements FieldCloner {

    private final Field field;

    public FloatFieldCloner(Field field) {
        this.field = Objects.requireNonNull(field);
    }

    @Override
    public <C> Unprocessed clone(DeepCloningUtils deepCloningUtils, C original, C clone) {
        float originalValue = getFieldValue(original, field);
        setFieldValue(clone, field, originalValue);
        return null;
    }

    private static float getFieldValue(Object bean, Field field) {
        try {
            return field.getFloat(bean);
        } catch (IllegalAccessException e) {
            throw FieldCloner.createExceptionOnRead(bean, field, e);
        }
    }

    private static void setFieldValue(Object bean, Field field, float value) {
        try {
            field.setFloat(bean, value);
        } catch (IllegalAccessException e) {
            throw FieldCloner.createExceptionOnWrite(bean, field, value, e);
        }
    }

}
