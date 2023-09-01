package org.drools.core.util.bitmask;

public abstract class SingleLongBitMask implements BitMask {

    public abstract long asLong();

    public abstract SingleLongBitMask clone();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof SingleLongBitMask && asLong() == ((SingleLongBitMask) o).asLong();
    }

    @Override
    public final int hashCode() {
        return (int) (asLong() ^ (asLong() >>> 32));
    }

    @Override
    public final String toString() {
        return "" + asLong();
    }
}
