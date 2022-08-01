package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.Objects;

final class ThreeIndexProperties implements IndexProperties {

    private final Object propertyA;
    private final Object propertyB;
    private final Object propertyC;

    ThreeIndexProperties(Object propertyA, Object propertyB, Object propertyC) {
        this.propertyA = propertyA;
        this.propertyB = propertyB;
        this.propertyC = propertyC;
    }

    @Override
    public <Type_> Type_ getProperty(int index) {
        switch (index) {
            case 0:
                return (Type_) propertyA;
            case 1:
                return (Type_) propertyB;
            case 2:
                return (Type_) propertyC;
            default:
                throw new IllegalArgumentException("Impossible state: index (" + index + ") != 0");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ThreeIndexProperties)) {
            return false;
        }
        ThreeIndexProperties other = (ThreeIndexProperties) o;
        return Objects.equals(propertyA, other.propertyA)
                && Objects.equals(propertyB, other.propertyB)
                && Objects.equals(propertyC, other.propertyC);
    }

    @Override
    public int hashCode() { // Not using Objects.hash(Object...) as that would create an array on the hot path.
        int result = Objects.hashCode(propertyA);
        result = 31 * result + Objects.hashCode(propertyB);
        result = 31 * result + Objects.hashCode(propertyC);
        return result;
    }

    @Override
    public String toString() {
        return "[" + propertyA + ", " + propertyB + ", " + propertyC + "]";
    }

}
