package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;
import java.util.Objects;

final class DoubleFieldCloner implements FieldCloner {

    private final Field field;

    public DoubleFieldCloner(Field field) {
        this.field = Objects.requireNonNull(field);
    }

    @Override
    public <C> Unprocessed clone(DeepCloningUtils deepCloningUtils, C original, C clone) {
        double originalValue = getFieldValue(original, field);
        setFieldValue(clone, field, originalValue);
        return null;
    }

    private static double getFieldValue(Object bean, Field field) {
        try {
            return field.getDouble(bean);
        } catch (IllegalAccessException e) {
            throw FieldCloner.createExceptionOnRead(bean, field, e);
        }
    }

    private static void setFieldValue(Object bean, Field field, double value) {
        try {
            field.setDouble(bean, value);
        } catch (IllegalAccessException e) {
            throw FieldCloner.createExceptionOnWrite(bean, field, value, e);
        }
    }

}
