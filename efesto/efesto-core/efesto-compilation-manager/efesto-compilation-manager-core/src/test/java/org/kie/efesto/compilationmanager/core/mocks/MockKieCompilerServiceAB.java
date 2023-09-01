package org.kie.efesto.compilationmanager.core.mocks;

import java.util.Arrays;
import java.util.List;

import org.kie.efesto.compilationmanager.api.model.EfestoResource;

public class MockKieCompilerServiceAB extends AbstractMockKieCompilerService {

    private static List<Class<? extends AbstractMockOutput>> managedResources = Arrays.asList(MockEfestoRedirectOutputA.class, MockEfestoRedirectOutputB.class);

    @Override
    public boolean canManageResource(EfestoResource toProcess) {
        return managedResources.contains(toProcess.getClass());
    }

}
