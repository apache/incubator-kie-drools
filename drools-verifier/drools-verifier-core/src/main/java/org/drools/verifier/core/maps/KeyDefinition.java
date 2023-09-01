package org.drools.verifier.core.maps;

import org.drools.verifier.core.util.PortablePreconditions;

public class KeyDefinition
        implements Comparable<KeyDefinition> {

    private final String id;
    private boolean updatable;

    private KeyDefinition(final String id) {
        this.id = PortablePreconditions.checkNotNull("id",
                                                     id);
    }

    public String getId() {
        return id;
    }

    @Override
    public int compareTo(final KeyDefinition other) {
        return id.compareTo(other.id);
    }

    public static Builder newKeyDefinition() {
        return new Builder();
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public static class Builder {

        private String id;

        private boolean updatable = false;

        public Builder withId(final String id) {
            this.id = PortablePreconditions.checkNotNull("id",
                                                         id);
            return this;
        }

        public KeyDefinition build() {
            PortablePreconditions.checkNotNull("id",
                                               id);
            final KeyDefinition keyDefinition = new KeyDefinition(id);
            keyDefinition.updatable = updatable;
            return keyDefinition;
        }

        public Builder updatable() {
            updatable = true;
            return this;
        }
    }
}
