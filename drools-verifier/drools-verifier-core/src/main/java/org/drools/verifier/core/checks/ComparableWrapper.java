package org.drools.verifier.core.checks;

import java.util.Comparator;
import java.util.Objects;

public class ComparableWrapper
        implements Comparable<ComparableWrapper> {

    public final static ComparableWrapper MIN_VALUE = new ComparableWrapper(null, Type.NEGATIVE_INFINITE);
    public final static ComparableWrapper MAX_VALUE = new ComparableWrapper(null, Type.INFINITE);

    enum Type {
        NEGATIVE_INFINITE,
        NORMAL,
        INFINITE
    }

    private final Comparable value;
    private final Type type;

    public ComparableWrapper(final Comparable value) {
        this(value, Type.NORMAL);
    }

    private ComparableWrapper(final Comparable value,
                              final Type type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public int compareTo(final ComparableWrapper other) {

        if (!Objects.equals(type, Type.NORMAL) || !Objects.equals(other.type, Type.NORMAL)) {
            return type.compareTo(other.type);
        } else {
            Comparator<Comparable> nullFirstCompare = Comparator.nullsFirst(Comparable::compareTo);
            return nullFirstCompare.compare(value, other.value);
        }
    }

    public Comparable getValue() {
        return value;
    }
}