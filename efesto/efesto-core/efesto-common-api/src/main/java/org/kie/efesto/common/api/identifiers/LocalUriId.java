package org.kie.efesto.common.api.identifiers;

import java.util.Objects;

/**
 * An abstract class for a {@link LocalId} that is represented as a Path.
 * <p>
 * Components should extend this class to get a default base implementation of their
 * LocalId.
 */
public abstract class LocalUriId implements LocalId {
    private LocalUri path;

    public LocalUriId() {
    }

    public LocalUriId(LocalUri path) {
        this.path = path;
    }

    @Override
    public LocalUri asLocalUri() {
        return path;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

    @Override
    public String toString() {
        return String.format("LocalUriId(%s)", path);
    }

    @Override
    public boolean equals(Object o) {
        return this == o ||
                o instanceof LocalId &&
                        Objects.equals(path, ((Id) o).toLocalId().asLocalUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
