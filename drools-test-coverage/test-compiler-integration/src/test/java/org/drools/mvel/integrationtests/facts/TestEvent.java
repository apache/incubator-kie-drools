package org.drools.mvel.integrationtests.facts;

/**
 * A simple fact class used in tests.
 */
public class TestEvent {
    private final String value;

    public TestEvent(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestEvent other = (TestEvent) obj;
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TestEvent{" + "value=" + value + '}';
    }
}
