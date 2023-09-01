package org.drools.verifier.core.index.keys;

import java.util.HashSet;

import static java.util.stream.Collectors.joining;

public class Values<T extends Comparable>
        extends HashSet<T> {

    public Values(final Comparable... list) {
        super();

        for (final Comparable comparable : list) {
            add((T) comparable);
        }
    }

    public Values() {
    }

    @Override
    public String toString() {
        return stream().map(Object::toString).collect(joining(", "));
    }

    public boolean isThereChanges(final Values otherValues) {
        if (this.isEmpty() && !otherValues.isEmpty()) {
            return true;
        } else if (!this.isEmpty() && otherValues.isEmpty()) {
            return true;
        } else if (this.isEmpty() && otherValues.isEmpty()) {
            return false;
        } else if (this.size() != otherValues.size()) {
            return true;
        } else if (!areValuesEqual(otherValues)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean areValuesEqual(final Values otherValues) {

        if (!otherValues.containsAll(this)) {
            return false;
        }

        if (!containsAll(otherValues)) {
            return false;
        }

        return true;
    }

    public boolean containsAny(final Values values) {
        for (final Object value : values) {
            if (contains(value)) {
                return true;
            }
        }

        return false;
    }

    public static <T extends Comparable> Values<T> nullValue() {
        final Values<T> comparables = new Values<>();
        comparables.add(null);
        return comparables;
    }
}
