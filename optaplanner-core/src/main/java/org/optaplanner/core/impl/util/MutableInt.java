package org.optaplanner.core.impl.util;

import java.util.Objects;

public final class MutableInt extends Number implements Comparable<MutableInt> {

    private int value;

    public MutableInt() {
        this(0);
    }

    public MutableInt(int value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int increment() {
        return add(1);
    }

    public int decrement() {
        return subtract(1);
    }

    public int add(int addend) {
        value += addend;
        return value;
    }

    public int subtract(int subtrahend) {
        value -= subtrahend;
        return value;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !Objects.equals(getClass(), o.getClass())) {
            return false;
        }
        MutableInt that = (MutableInt) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int compareTo(MutableInt other) {
        return Integer.compare(value, other.value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

}
