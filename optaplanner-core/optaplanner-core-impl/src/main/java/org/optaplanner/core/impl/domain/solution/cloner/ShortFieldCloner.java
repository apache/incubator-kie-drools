package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;
import java.util.Objects;

final class ShortFieldCloner implements FieldCloner {

    private final Field field;

    public ShortFieldCloner(Field field) {
        this.field = Objects.requireNonNull(field);
    }

    @Override
    public <C> Unprocessed clone(DeepCloningUtils deepCloningUtils, C original, C clone) {
        short originalValue = getFieldValue(original, field);
        setFieldValue(clone, field, originalValue);
        return null;
    }

    private static short getFieldValue(Object bean, Field field) {
        try {
            return field.getShort(bean);
        } catch (IllegalAccessException e) {
            throw FieldCloner.createExceptionOnRead(bean, field, e);
        }
    }

    private static void setFieldValue(Object bean, Field field, short value) {
        try {
            field.setShort(bean, value);
        } catch (IllegalAccessException e) {
            throw FieldCloner.createExceptionOnWrite(bean, field, value, e);
        }
    }

}
