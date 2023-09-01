package org.kie.efesto.runtimemanager.core.mocks;

import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;

public abstract class AbstractMockEfestoInput implements EfestoInput<String> {

    private final ModelLocalUriId modelLocalUriId =
            new ModelLocalUriId(LocalUri.parse("/" + this.getClass().getSimpleName() + "/" + this.getClass().getPackage().getName()));

    @Override
    public ModelLocalUriId getModelLocalUriId() {
        return modelLocalUriId;
    }

    @Override
    public String getInputData() {
        return this.getClass().getSimpleName();
    }
}
