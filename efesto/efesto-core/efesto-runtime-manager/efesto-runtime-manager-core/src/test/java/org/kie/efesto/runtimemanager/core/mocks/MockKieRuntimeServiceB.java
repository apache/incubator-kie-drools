package org.kie.efesto.runtimemanager.core.mocks;

import java.util.Collections;
import java.util.List;

import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;

import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;

public class MockKieRuntimeServiceB extends AbstractMockKieRuntimeService {

    private static List<ModelLocalUriId> managedResources =
            Collections.singletonList(new ModelLocalUriId(LocalUri.parse(SLASH + MockEfestoInputB.class.getSimpleName() + SLASH + MockEfestoInputB.class.getPackage().getName())));

    @Override
    public EfestoClassKey getEfestoClassKeyIdentifier() {
        return new EfestoClassKey(MockEfestoInputB.class, String.class);
    }

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return managedResources.contains(toEvaluate.getModelLocalUriId());
    }
}
