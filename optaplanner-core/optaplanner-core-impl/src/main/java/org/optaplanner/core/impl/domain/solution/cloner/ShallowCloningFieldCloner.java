package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;
import java.util.function.Consumer;

final class ShallowCloningFieldCloner implements FieldCloner {

    static final FieldCloner INSTANCE = new ShallowCloningFieldCloner();

    @Override
    public <C> void clone(DeepCloningUtils deepCloningUtils, Field field, Class<? extends C> instanceClass,
            C original, C clone, Consumer<Object> deferredValueConsumer) {
        Object originalValue = FieldCloner.getFieldValue(original, field);
        FieldCloner.setFieldValue(clone, field, originalValue);
    }

    private ShallowCloningFieldCloner() {

    }

}
