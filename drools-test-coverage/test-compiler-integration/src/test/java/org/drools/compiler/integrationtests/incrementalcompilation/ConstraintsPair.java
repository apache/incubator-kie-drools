package org.drools.compiler.integrationtests.incrementalcompilation;

public class ConstraintsPair {
    private final String constraints1;
    private final String constraints2;

    public ConstraintsPair(final String constraints1, final String constraints2) {
        this.constraints1 = constraints1;
        this.constraints2 = constraints2;
    }

    public String getConstraints1() {
        return constraints1;
    }

    public String getConstraints2() {
        return constraints2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConstraintsPair that = (ConstraintsPair) o;

        return (constraints1.equals(that.constraints1) && constraints2.equals(that.constraints2))
                || (constraints1.equals(that.constraints2) && constraints2.equals(that.constraints1));
    }

    @Override
    public int hashCode() {
        return constraints1.hashCode() + constraints2.hashCode();
    }
}
