package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;
import java.util.Objects;

final class ShallowCloningFieldCloner implements FieldCloner {

    private final Field field;

    public ShallowCloningFieldCloner(Field field) {
        this.field = Objects.requireNonNull(field);
    }

    @Override
    public <C> Unprocessed clone(DeepCloningUtils deepCloningUtils, C original, C clone) {
        Object originalValue = FieldCloner.getGenericFieldValue(original, field);
        FieldCloner.setGenericFieldValue(clone, field, originalValue);
        return null;
    }

}
