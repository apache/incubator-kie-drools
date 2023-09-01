package org.kie.efesto.compilationmanager.core.mocks;

import java.util.Collections;
import java.util.List;

import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;

public abstract class AbstractMockKieCompilerService implements KieCompilerService<EfestoCompilationOutput, EfestoCompilationContext> {

    @Override
    @SuppressWarnings("unchecked")
    public List<EfestoCompilationOutput> processResource(EfestoResource toProcess, EfestoCompilationContext context) {
        if (!canManageResource(toProcess)) {
            throw new KieCompilerServiceException(String.format("Unmanaged resource %s", toProcess.getClass()));
        }
        return Collections.singletonList(new MockEfestoCallableOutput());
    }

    @Override
    public String getModelType() {
        return "mock";
    }
}
