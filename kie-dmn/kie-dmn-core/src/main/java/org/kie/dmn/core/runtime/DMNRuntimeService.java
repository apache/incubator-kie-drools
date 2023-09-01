package org.kie.dmn.core.runtime;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.api.KieBase;
import org.kie.api.internal.runtime.KieRuntimeService;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.impl.DMNRuntimeKBWrappingIKB;

public class DMNRuntimeService implements KieRuntimeService<DMNRuntime> {

    @Override
    public DMNRuntime newKieRuntime(KieBase kieBase) {
        InternalKnowledgeBase kb = (InternalKnowledgeBase) kieBase;
        return new DMNRuntimeImpl(new DMNRuntimeKBWrappingIKB(kb));
    }

    @Override
    public Class getServiceInterface() {
        return DMNRuntime.class;
    }
}
