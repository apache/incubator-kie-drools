package org.kie.drl.engine.runtime.kiesession.local.service;

import java.util.Optional;

import org.kie.api.runtime.KieSession;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoInputDrlKieSessionLocal;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoOutputDrlKieSessionLocal;
import org.kie.drl.engine.runtime.kiesession.local.utils.DrlRuntimeHelper;
import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;


public class KieRuntimeServiceDrlKieSessionLocal implements KieRuntimeService<String, KieSession, EfestoInputDrlKieSessionLocal, EfestoOutputDrlKieSessionLocal, EfestoRuntimeContext> {

    @Override
    public EfestoClassKey getEfestoClassKeyIdentifier() {
        return new EfestoClassKey(EfestoInputDrlKieSessionLocal.class, String.class);
    }

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return DrlRuntimeHelper.canManage(toEvaluate, context);
    }

    @Override
    public Optional<EfestoOutputDrlKieSessionLocal> evaluateInput(EfestoInputDrlKieSessionLocal toEvaluate, EfestoRuntimeContext context) {
        return DrlRuntimeHelper.execute(toEvaluate, context);
    }

    @Override
    public String getModelType() {
        return "drl";
    }
}
