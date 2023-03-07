package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;
import java.util.Objects;

final class CharFieldCloner implements FieldCloner {

    private final Field field;

    public CharFieldCloner(Field field) {
        this.field = Objects.requireNonNull(field);
    }

    @Override
    public <C> Unprocessed clone(DeepCloningUtils deepCloningUtils, C original, C clone) {
        char originalValue = getFieldValue(original, field);
        setFieldValue(clone, field, originalValue);
        return null;
    }

    private static char getFieldValue(Object bean, Field field) {
        try {
            return field.getChar(bean);
        } catch (IllegalAccessException e) {
            throw FieldCloner.createExceptionOnRead(bean, field, e);
        }
    }

    private static void setFieldValue(Object bean, Field field, char value) {
        try {
            field.setChar(bean, value);
        } catch (IllegalAccessException e) {
            throw FieldCloner.createExceptionOnWrite(bean, field, value, e);
        }
    }

}
