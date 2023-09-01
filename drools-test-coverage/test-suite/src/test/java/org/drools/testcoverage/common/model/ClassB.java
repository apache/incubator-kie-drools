package org.drools.testcoverage.common.model;

public class ClassB implements InterfaceB {

    private String id = "123";

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ClassB classB = (ClassB) o;

        return id != null ? id.equals(classB.id) : classB.id == null;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(id);
    }
}
