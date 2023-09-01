package org.kie.efesto.runtimemanager.core.mocks;

import java.util.Optional;

import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;

public abstract class AbstractMockKieRuntimeService<T extends AbstractMockEfestoInput> implements KieRuntimeService<String, String, T, MockEfestoOutput, EfestoRuntimeContext> {


    @Override
    public Optional<MockEfestoOutput> evaluateInput(T toEvaluate, EfestoRuntimeContext context) {
        if (!canManageInput(toEvaluate, context)) {
            throw new KieRuntimeServiceException(String.format("Unmanaged input %s", toEvaluate.getModelLocalUriId()));
        }
        return Optional.of(new MockEfestoOutput());
    }

    @Override
    public String getModelType() {
        return "mock";
    }
}
