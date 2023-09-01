package org.kie.drl.engine.runtime.mapinput.service;

import java.util.Map;
import java.util.Optional;

import org.kie.drl.engine.runtime.mapinput.model.EfestoOutputDrlMap;
import org.kie.drl.engine.runtime.mapinput.utils.DrlRuntimeHelper;
import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoMapInputDTO;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;

public class KieRuntimeServiceDrlMapInput implements KieRuntimeService<EfestoMapInputDTO, Map<String, Object>,
        BaseEfestoInput<EfestoMapInputDTO>, EfestoOutputDrlMap, EfestoRuntimeContext> {

    @Override
    public EfestoClassKey getEfestoClassKeyIdentifier() {
        return new EfestoClassKey(BaseEfestoInput.class, EfestoMapInputDTO.class);
    }

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return DrlRuntimeHelper.canManage(toEvaluate, context);
    }

    @Override
    public Optional<EfestoOutputDrlMap> evaluateInput(BaseEfestoInput<EfestoMapInputDTO> toEvaluate,
                                                      EfestoRuntimeContext context) {
        return DrlRuntimeHelper.execute(toEvaluate, context);
    }

    @Override
    public String getModelType() {
        return "drl";
    }
}
