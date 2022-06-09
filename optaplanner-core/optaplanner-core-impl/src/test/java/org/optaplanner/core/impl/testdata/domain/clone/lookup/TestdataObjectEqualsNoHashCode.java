package org.optaplanner.core.impl.testdata.domain.clone.lookup;

import java.util.Objects;

public class TestdataObjectEqualsNoHashCode {

    private final Integer id;

    public TestdataObjectEqualsNoHashCode(Integer id) {
        this.id = id;
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
        final TestdataObjectEqualsNoHashCode other = (TestdataObjectEqualsNoHashCode) obj;
        return Objects.equals(this.id, other.id);
    }

}
