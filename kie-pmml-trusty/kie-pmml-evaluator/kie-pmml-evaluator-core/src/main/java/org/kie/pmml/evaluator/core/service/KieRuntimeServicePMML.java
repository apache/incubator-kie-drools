package org.kie.pmml.evaluator.core.service;

import java.util.Optional;

import org.kie.api.pmml.PMML4Result;
import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.evaluator.core.PMMLRuntimeContextImpl;
import org.kie.pmml.evaluator.core.model.EfestoInputPMML;
import org.kie.pmml.evaluator.core.model.EfestoOutputPMML;

import static org.kie.pmml.commons.Constants.PMML_STRING;
import static org.kie.pmml.evaluator.core.utils.PMMLRuntimeHelper.canManageEfestoInput;
import static org.kie.pmml.evaluator.core.utils.PMMLRuntimeHelper.executeEfestoInputPMML;

public class KieRuntimeServicePMML implements KieRuntimeService<PMMLRuntimeContext, PMML4Result, EfestoInputPMML,
        EfestoOutputPMML, EfestoRuntimeContext> {

    @Override
    public EfestoClassKey getEfestoClassKeyIdentifier() {
        return new EfestoClassKey(EfestoInputPMML.class, PMMLRuntimeContextImpl.class);
    }

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return canManageEfestoInput(toEvaluate, context);
    }

    @Override
    public Optional<EfestoOutputPMML> evaluateInput(EfestoInputPMML toEvaluate, EfestoRuntimeContext context) {
        return executeEfestoInputPMML(toEvaluate, context);
    }

    @Override
    public String getModelType() {
        return PMML_STRING;
    }
}
