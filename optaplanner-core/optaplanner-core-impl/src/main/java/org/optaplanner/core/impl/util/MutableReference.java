package org.optaplanner.core.impl.util;

import java.util.Objects;

public final class MutableReference<Value_> {

    private Value_ value;

    public MutableReference(Value_ value) {
        this.value = value;
    }

    public Value_ getValue() {
        return value;
    }

    public void setValue(Value_ value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MutableReference<?> that = (MutableReference<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
