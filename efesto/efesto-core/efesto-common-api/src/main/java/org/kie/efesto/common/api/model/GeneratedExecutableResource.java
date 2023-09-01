package org.kie.efesto.common.api.model;

import java.util.List;
import java.util.Objects;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

/**
 * A <code>GeneratedResource</code> meant to be directly executed, with a <b>full reference name (frn)</b> identifier
 */
public final class GeneratedExecutableResource implements GeneratedResource {

    private static final long serialVersionUID = 6588314882989626752L;
    /**
     * the full reference identifier (e.g. "bar/resource/some_final_model")
     */
    private final ModelLocalUriId modelLocalUriId;


    private final List<String> fullClassNames;

    public GeneratedExecutableResource() {
        this(null, null);
    }

    public GeneratedExecutableResource(ModelLocalUriId modelLocalUriId, List<String> fullClassNames) {
        this.modelLocalUriId = modelLocalUriId;
        this.fullClassNames = fullClassNames;
    }

    public ModelLocalUriId getModelLocalUriId() {
        return modelLocalUriId;
    }

    public List<String> getFullClassNames() {
        return fullClassNames;
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelLocalUriId);
    }

    /**
     * Two <code>GeneratedExecutableResource</code>s are equals if they have the same full path <b>OR</b>
     * if they have the same full reference name
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (super.equals(o)) return true;
        if (!(o instanceof GeneratedExecutableResource)) {
            return false;
        }
        GeneratedExecutableResource that = (GeneratedExecutableResource) o;
        return modelLocalUriId.equals(that.modelLocalUriId);
    }

    @Override
    public String toString() {
        return "GeneratedExecutableResource{" +
                "modelLocalUriId='" + modelLocalUriId + '\'' +
                "} " + super.toString();
    }
}
