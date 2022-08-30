package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;
import java.util.function.Consumer;

final class FloatFieldCloner implements FieldCloner {

    static final FieldCloner INSTANCE = new FloatFieldCloner();

    @Override
    public <C> void clone(DeepCloningUtils deepCloningUtils, Field field, Class<? extends C> instanceClass,
            C original, C clone, Consumer<Object> deferredValueConsumer) {
        float originalValue = getFieldValue(original, field);
        setFieldValue(clone, field, originalValue);
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

    private FloatFieldCloner() {

    }

}
