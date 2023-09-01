package org.kie.efesto.runtimemanager.api.model;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

public abstract class AbstractEfestoOutput<T> implements EfestoOutput<T> {

    private final ModelLocalUriId modelLocalUriId;
    private final T outputData;

    protected AbstractEfestoOutput(ModelLocalUriId modelLocalUriId, T outputData) {
        this.modelLocalUriId = modelLocalUriId;
        this.outputData = outputData;
    }

    @Override
    public ModelLocalUriId getModelLocalUriId() {
        return modelLocalUriId;
    }

    @Override
    public T getOutputData() {
        return outputData;
    }
}
