package org.kie.api.runtime;

import org.kie.api.KieBase;

public interface KieRuntimeBuilder {
    KieBase getKieBase();
    KieBase getKieBase(String name);

    KieSession newKieSession();
    KieSession newKieSession(String sessionName);
}
