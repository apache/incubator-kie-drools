package org.drools.model.bitmask;

import org.drools.model.BitMask;

public abstract class SingleLongBitMask implements BitMask {

    private Class<?> patternClass;

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

    @Override
    public Class<?> getPatternClass() {
        return patternClass;
    }

    @Override
    public void setPatternClass( Class<?> patternClass ) {
        this.patternClass = patternClass;
    }
}
