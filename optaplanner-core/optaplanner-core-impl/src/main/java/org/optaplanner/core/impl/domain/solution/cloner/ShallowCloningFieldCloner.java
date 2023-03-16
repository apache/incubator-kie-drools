package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;
import java.util.Objects;

import org.optaplanner.core.api.function.TriConsumer;

final class ShallowCloningFieldCloner {

    public static ShallowCloningFieldCloner of(Field field) {
        Class<?> fieldType = field.getType();
        if (fieldType == boolean.class) {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyBoolean);
        } else if (fieldType == byte.class) {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyByte);
        } else if (fieldType == char.class) {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyChar);
        } else if (fieldType == short.class) {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyShort);
        } else if (fieldType == int.class) {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyInt);
        } else if (fieldType == long.class) {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyLong);
        } else if (fieldType == float.class) {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyFloat);
        } else if (fieldType == double.class) {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyDouble);
        } else {
            return new ShallowCloningFieldCloner(field, FieldCloningUtils::copyObject);
        }

    }

    private final Field field;
    private final TriConsumer<Field, Object, Object> copyOperation;

    private ShallowCloningFieldCloner(Field field, TriConsumer<Field, Object, Object> copyOperation) {
        this.field = Objects.requireNonNull(field);
        this.copyOperation = Objects.requireNonNull(copyOperation);
    }

    public <C> void clone(C original, C clone) {
        copyOperation.accept(field, original, clone);
    }

}
