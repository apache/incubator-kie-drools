package org.drools.testcoverage.common.model;

import java.io.Serializable;

public class InternationalAddress extends Address implements Serializable {

    private static final long serialVersionUID = -4500788294548913271L;

    private String state;

    public InternationalAddress() {
        super();
    }

    public InternationalAddress(final String street, final int number, final String city, final String state) {
        super(street, number, city);
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        InternationalAddress that = (InternationalAddress) o;

        return state != null ? state.equals(that.state) : that.state == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }
}
