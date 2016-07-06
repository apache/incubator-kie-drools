package org.drools.core.fluent.impl;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

import java.util.HashMap;
import java.util.Map;

public class CommandRegister {

    public Map<String, Object> createCommandRegister() {
        Map<String, Object> register = new HashMap<String, Object>();

        register.put(KieContainer.class.getName(), null);
        register.put(KieBase.class.getName(), null);
        register.put(KieSession.class.getName(), null);
        register.put(StatelessKieSession.class.getName(), null);

        return register;
    }
}
