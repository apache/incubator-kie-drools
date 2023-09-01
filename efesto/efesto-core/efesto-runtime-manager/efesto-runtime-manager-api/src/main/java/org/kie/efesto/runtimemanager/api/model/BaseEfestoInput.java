package org.kie.efesto.runtimemanager.api.model;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

public class BaseEfestoInput<T> implements EfestoInput<T> {

    private final ModelLocalUriId modelLocalUriId;
    private final T inputData;

    public BaseEfestoInput(ModelLocalUriId modelLocalUriId, T inputData) {
        this.modelLocalUriId = modelLocalUriId;
        this.inputData = inputData;
    }

    @Override
    public ModelLocalUriId getModelLocalUriId() {
        return modelLocalUriId;
    }

    @Override
    public T getInputData() {
        return inputData;
    }
}
