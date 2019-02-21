package org.kie.dmn.validation.dtanalysis.model;

import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;

public class Bound<V extends Comparable<V>> implements Comparable<Bound<V>> {

    private final V value;
    private final Range.RangeBoundary boundaryType;
    private final Interval parent;

    public Bound(V value, RangeBoundary boundaryType, Interval parent) {
        this.value = value;
        this.boundaryType = boundaryType;
        this.parent = parent;
    }

    @Override
    public int compareTo(Bound<V> o) {
        int valueCompare = this.value.compareTo(o.value);
        if (valueCompare == 0) {
            if (this.boundaryType == o.boundaryType) {
                return 0;
            } else if (this.boundaryType == RangeBoundary.OPEN) {
                return -1;
            } else {
                return 1;
            }
        }
        return valueCompare;
    }

    public V getValue() {
        return value;
    }

    public Range.RangeBoundary getBoundaryType() {
        return boundaryType;
    }

    public Interval getParent() {
        return parent;
    }

    public boolean isLowerBound() {
        return parent.getLowerBound() == this;
    }

    public boolean isUpperBound() {
        return parent.getUpperBound() == this;
    }

    @Override
    public String toString() {
        if (isLowerBound()) {
            return (boundaryType == RangeBoundary.OPEN ? "(" : "[") + value;
        } else {
            return value + (boundaryType == RangeBoundary.OPEN ? ")" : "]");
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((boundaryType == null) ? 0 : boundaryType.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Bound other = (Bound) obj;
        if (boundaryType != other.boundaryType)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
}
