package org.kie.efesto.compilationmanager.api.model;

import java.util.Set;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

public abstract class EfestoSetResource<T> implements EfestoResource<Set<T>> {

    private final Set<T> resources;
    private final ModelLocalUriId modelLocalUriId;

    protected EfestoSetResource(Set<T> resources, ModelLocalUriId modelLocalUriId) {
        this.resources = resources;
        this.modelLocalUriId = modelLocalUriId;
    }

    @Override
    public Set<T> getContent() {
        return resources;
    }

    public ModelLocalUriId getModelLocalUriId() {
        return modelLocalUriId;
    }
}
