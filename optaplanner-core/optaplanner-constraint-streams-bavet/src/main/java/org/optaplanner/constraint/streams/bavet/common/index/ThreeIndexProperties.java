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
    public int maxLength() {
        return 3;
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
    public int hashCode() {
        return Objects.hash(propertyA, propertyB, propertyC);
    }

    @Override
    public String toString() {
        return "[" + propertyA + ", " + propertyB + ", " + propertyC + "]";
    }

}
