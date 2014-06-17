package org.kie.internal.assembler;

import org.kie.api.io.ResourceType;

import java.util.HashMap;
import java.util.Map;

public class KieAssemblersImpl implements KieAssemblers {
    private Map<ResourceType, KieAssemblerService> assemblers;

    public KieAssemblersImpl() {
        assemblers = new HashMap<ResourceType, KieAssemblerService>();
    }

    @Override
    public Map<ResourceType, KieAssemblerService> getAssemblers() {
        return this.assemblers;
    }

    @Override
    public Class getServiceInterface() {
        return KieAssemblerService.class;
    }
}
