package org.kie.pmml.evaluator.core.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.kie.api.pmml.PMML4Result;
import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.pmml.evaluator.core.model.EfestoOutputPMML;

import static org.kie.pmml.commons.Constants.PMML_STRING;
import static org.kie.pmml.evaluator.core.utils.PMMLRuntimeHelper.canManageEfestoInput;
import static org.kie.pmml.evaluator.core.utils.PMMLRuntimeHelper.executeEfestoInputFromMap;

public class KieRuntimeServicePMMLMapInput implements KieRuntimeService<Map<String, Object>, PMML4Result,
        EfestoInput<Map<String, Object>>, EfestoOutputPMML, EfestoRuntimeContext> {

    @Override
    public EfestoClassKey getEfestoClassKeyIdentifier() {
        return new EfestoClassKey(BaseEfestoInput.class, HashMap.class);
    }

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return canManageEfestoInput(toEvaluate, context);
    }

    @Override
    public Optional<EfestoOutputPMML> evaluateInput(EfestoInput<Map<String, Object>> toEvaluate,
                                                    EfestoRuntimeContext context) {
        return executeEfestoInputFromMap(toEvaluate, context);
    }

    @Override
    public String getModelType() {
        return PMML_STRING;
    }
}
