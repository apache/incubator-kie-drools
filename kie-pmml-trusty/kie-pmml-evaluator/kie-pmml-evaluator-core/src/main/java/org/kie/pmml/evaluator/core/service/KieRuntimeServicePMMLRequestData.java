package org.kie.pmml.evaluator.core.service;

import java.util.Optional;

import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.pmml.evaluator.core.model.EfestoOutputPMML;

import static org.kie.pmml.commons.Constants.PMML_STRING;
import static org.kie.pmml.evaluator.core.utils.PMMLRuntimeHelper.canManageEfestoInput;
import static org.kie.pmml.evaluator.core.utils.PMMLRuntimeHelper.executeEfestoInput;

public class KieRuntimeServicePMMLRequestData implements KieRuntimeService<PMMLRequestData, PMML4Result,
        EfestoInput<PMMLRequestData>, EfestoOutputPMML, EfestoRuntimeContext> {

    @Override
    public EfestoClassKey getEfestoClassKeyIdentifier() {
        return new EfestoClassKey(BaseEfestoInput.class, PMMLRequestData.class);
    }

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return canManageEfestoInput(toEvaluate, context);
    }

    @Override
    public Optional<EfestoOutputPMML> evaluateInput(EfestoInput<PMMLRequestData> toEvaluate,
                                                    EfestoRuntimeContext context) {
        return executeEfestoInput(toEvaluate, context);
    }

    @Override
    public String getModelType() {
        return PMML_STRING;
    }
}
