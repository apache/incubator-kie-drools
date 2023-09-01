package org.kie.efesto.runtimemanager.core.mocks;

import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;

public class MockKieRuntimeServiceC extends AbstractMockKieRuntimeService {

    private final static ModelLocalUriId modelLocalUriIdC =
            new ModelLocalUriId(LocalUri.parse("/" + MockEfestoInputC.class.getSimpleName() + "/" + MockEfestoInputC.class.getPackage().getName()));

    @Override
    public EfestoClassKey getEfestoClassKeyIdentifier() {
        return new EfestoClassKey(MockEfestoInputC.class, String.class);
    }

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return toEvaluate.getModelLocalUriId().equals(modelLocalUriIdC);
    }
}
