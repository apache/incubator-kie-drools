package org.kie.efesto.compilationmanager.core.mocks;

import org.kie.efesto.compilationmanager.api.model.EfestoResource;

public class MockKieCompilerServiceC extends AbstractMockKieCompilerService {

    @Override
    public boolean canManageResource(EfestoResource toProcess) {
        return toProcess instanceof MockEfestoRedirectOutputC;
    }

}
