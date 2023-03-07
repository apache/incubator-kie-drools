package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;

final class Unprocessed {

    final Object bean;
    final Field field;
    final Object originalValue;

    public Unprocessed(Object bean, Field field, Object originalValue) {
        this.bean = bean;
        this.field = field;
        this.originalValue = originalValue;
    }

}
