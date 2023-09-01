package org.kie.efesto.runtimemanager.core.mocks;

import java.util.Optional;

import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;

// This service is required to find IndexFile "test" in classpath
public class TestKieRuntimeService<T extends AbstractMockEfestoInput> implements KieRuntimeService<String, String, T,
        MockEfestoOutput, EfestoRuntimeContext> {

    @Override
    public EfestoClassKey getEfestoClassKeyIdentifier() {
        // THis should always return an unmatchable key
        return new EfestoClassKey(TestKieRuntimeService.class);
    }

    @Override
    public Optional<MockEfestoOutput> evaluateInput(T toEvaluate, EfestoRuntimeContext context) {
        return Optional.empty();
    }

    @Override
    public String getModelType() {
        return "test";
    }

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return false;
    }
}
