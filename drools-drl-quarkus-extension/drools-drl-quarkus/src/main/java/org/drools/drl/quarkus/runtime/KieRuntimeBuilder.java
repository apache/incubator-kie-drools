package org.drools.drl.quarkus.runtime;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public interface KieRuntimeBuilder {
    KieBase getKieBase();
    KieBase getKieBase(String name);

    KieSession newKieSession();
    KieSession newKieSession(String sessionName);
}
