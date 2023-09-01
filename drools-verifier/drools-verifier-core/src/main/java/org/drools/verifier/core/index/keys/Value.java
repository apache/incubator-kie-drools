package org.drools.verifier.core.index.keys;

public class Value
        implements Comparable<Value> {

    private final Comparable comparable;

    public Value(final Comparable comparable) {
        this.comparable = comparable;
    }

    public Comparable getComparable() {
        return comparable;
    }

    @Override
    public String toString() {
        return "" + comparable;
    }

    @Override
    public int compareTo(final Value value) {
        if (comparable == null && value.comparable == null) {
            return 0;
        } else if (comparable == null) {
            return -1;
        } else if (value.comparable == null) {
            return 1;
        } else {
            try {
                return this.comparable.compareTo(value.comparable);
            } catch (final Exception cce) {
                return this.comparable.toString().compareTo(value.comparable.toString());
            }
        }
    }
}
