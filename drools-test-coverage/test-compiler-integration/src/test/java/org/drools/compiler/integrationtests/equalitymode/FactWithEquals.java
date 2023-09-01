package org.drools.compiler.integrationtests.equalitymode;

import java.util.Objects;

public class FactWithEquals {
    private final Integer value;

    public FactWithEquals(Integer value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FactWithEquals that = (FactWithEquals) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
