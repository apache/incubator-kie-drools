package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;
import java.util.function.Consumer;

final class ShallowCloningFieldCloner<C> implements FieldCloner<C> {

    private static final FieldCloner INSTANCE = new ShallowCloningFieldCloner();

    public static <C> FieldCloner<C> getInstance() {
        return INSTANCE;
    }

    @Override
    public void clone(DeepCloningUtils deepCloningUtils, Field field, Class<? extends C> instanceClass,
            C original, C clone, Consumer<Object> deferredValueConsumer) {
        Object originalValue = FieldCloner.getFieldValue(original, field);
        FieldCloner.setFieldValue(clone, field, originalValue);
    }

    private ShallowCloningFieldCloner() {

    }

}
