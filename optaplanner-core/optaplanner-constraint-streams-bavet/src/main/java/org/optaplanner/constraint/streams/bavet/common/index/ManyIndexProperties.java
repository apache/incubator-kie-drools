package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.Arrays;

final class ManyIndexProperties implements IndexProperties {

    private final Object[] properties;

    ManyIndexProperties(Object... properties) {
        this.properties = properties;
    }

    @Override
    public <Type_> Type_ getProperty(int index) {
        return (Type_) properties[index];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ManyIndexProperties)) {
            return false;
        }
        ManyIndexProperties other = (ManyIndexProperties) o;
        return Arrays.equals(properties, other.properties);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(properties);
    }

    @Override
    public String toString() {
        return Arrays.toString(properties);
    }

}
