package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;
import java.util.function.Consumer;

final class DeepCloningFieldCloner implements FieldCloner {

    static final FieldCloner INSTANCE = new DeepCloningFieldCloner();

    @Override
    public <C> void clone(DeepCloningUtils deepCloningUtils, Field field, Class<? extends C> instanceClass, C original, C clone,
            Consumer<Object> deferredValueConsumer) {
        Object originalValue = FieldCloner.getFieldValue(original, field);
        if (isDeepCloneField(deepCloningUtils, field, instanceClass, originalValue)) { // Deffer filling in the field.
            deferredValueConsumer.accept(originalValue);
        } else { // Shallow copy.
            FieldCloner.setFieldValue(clone, field, originalValue);
        }
    }

    private static boolean isDeepCloneField(DeepCloningUtils deepCloningUtils, Field field, Class<?> fieldInstanceClass,
            Object originalValue) {
        if (originalValue == null) {
            return false;
        }
        return deepCloningUtils.getDeepCloneDecision(field, fieldInstanceClass, originalValue.getClass());
    }

    @Override
    public boolean mayDeferClone() {
        return true;
    }

    private DeepCloningFieldCloner() {

    }
}
