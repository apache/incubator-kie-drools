package org.optaplanner.core.impl.testdata.domain.clone.lookup;

import java.util.Objects;

public class TestdataObjectEquals {

    private final Integer id;

    public TestdataObjectEquals(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestdataObjectEquals other = (TestdataObjectEquals) obj;
        return Objects.equals(this.id, other.id);
    }

}
