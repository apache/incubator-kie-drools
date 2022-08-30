package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;
import java.util.function.Consumer;

final class BooleanFieldCloner<C> implements FieldCloner<C> {

    private static final FieldCloner INSTANCE = new BooleanFieldCloner();

    public static <C> FieldCloner<C> getInstance() {
        return INSTANCE;
    }

    @Override
    public void clone(DeepCloningUtils deepCloningUtils, Field field, Class<? extends C> instanceClass,
            C original, C clone, Consumer<Object> deferredValueConsumer) {
        boolean originalValue = getFieldValue(original, field);
        setFieldValue(clone, field, originalValue);
    }

    private static boolean getFieldValue(Object bean, Field field) {
        try {
            return field.getBoolean(bean);
        } catch (IllegalAccessException e) {
            throw FieldCloner.createExceptionOnRead(bean, field, e);
        }
    }

    private static void setFieldValue(Object bean, Field field, boolean value) {
        try {
            field.setBoolean(bean, value);
        } catch (IllegalAccessException e) {
            throw FieldCloner.createExceptionOnWrite(bean, field, value, e);
        }
    }

    private BooleanFieldCloner() {

    }

}
