package org.drools.quarkus.test;

public class ProbeEvent {

    private int value = 1;

    public int getValue() {
        return value;
    }

    public ProbeEvent(final int value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ProbeEvent that = (ProbeEvent) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "ProbeEvent{" +
                "value=" + value +
                '}';
    }
}
