package org.kie.efesto.common.api.model;

import java.util.Objects;

/**
 * A <code>GeneratedResource</code> meant to map a <code>Class</code>
 */
public final class GeneratedClassResource implements GeneratedResource {

    private static final long serialVersionUID = 8140824908598306598L;
    /**
     * the full class name of generated class
     */
    private final String fullClassName;

    public GeneratedClassResource() {
        this(null);
    }

    public GeneratedClassResource(String fullClassName) {
        this.fullClassName = fullClassName;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    @Override
    public String toString() {
        return "GeneratedClassResource{" +
                "fullClassName='" + fullClassName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneratedClassResource that = (GeneratedClassResource) o;
        return Objects.equals(fullClassName, that.fullClassName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullClassName);
    }
}
