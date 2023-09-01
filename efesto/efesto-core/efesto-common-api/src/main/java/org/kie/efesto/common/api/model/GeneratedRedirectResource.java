package org.kie.efesto.common.api.model;

import java.util.Objects;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

/**
 * A <code>GeneratedResource</code> not meant to be directly executed, linking to another <code>GeneratedResource</code>
 */
public final class GeneratedRedirectResource implements GeneratedResource {

    private static final long serialVersionUID = 1356917578380378083L;
    /**
     * the full reference identifier (e.g. "bar/resource/some_final_model")
     */
    private final ModelLocalUriId modelLocalUriId;

    /**
     * the full reference identifier (e.g. "bar/resource/some_final_model")
     */
    private final String target;

    public GeneratedRedirectResource() {
        this(null, null);
    }

    public GeneratedRedirectResource(ModelLocalUriId modelLocalUriId, String target) {
        this.modelLocalUriId = modelLocalUriId;
        this.target = target;
    }

    public ModelLocalUriId getModelLocalUriId() {
        return modelLocalUriId;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "GeneratedRedirectResource{" +
                "localUri='" + modelLocalUriId + '\'' +
                ", target='" + target + '\'' +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GeneratedRedirectResource that = (GeneratedRedirectResource) o;
        return Objects.equals(modelLocalUriId, that.modelLocalUriId) && Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), modelLocalUriId, target);
    }
}
