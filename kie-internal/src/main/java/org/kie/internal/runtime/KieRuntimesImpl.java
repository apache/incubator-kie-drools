package org.kie.internal.runtime;

import org.kie.internal.assembler.KieAssemblers;

import java.util.HashMap;
import java.util.Map;

public class KieRuntimesImpl implements KieRuntimes {
    private Map<String, Object> runtimes;

    public KieRuntimesImpl() {
        runtimes = new HashMap<String, Object>();
    }

    @Override
    public Map<String, Object> getRuntimes() {
        return this.runtimes;
    }

    @Override
    public Class getServiceInterface() {
        return KieRuntimes.class;
    }
}
