package org.kie.api.internal.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.internal.assembler.KieAssemblers;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;

public class MockAssemblersImpl implements KieAssemblers,
                                           Consumer<KieAssemblerService> {

    private Map<ResourceType, KieAssemblerService> assemblers = new HashMap();

    public Map<ResourceType, KieAssemblerService> getAssemblers() {
        return this.assemblers;
    }

    @Override
    public void addResourceBeforeRules(Object knowledgeBuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {

    }

    @Override
    public void addResourceAfterRules(Object knowledgeBuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {

    }

    @Override
    public void addResourcesAfterRules(Object knowledgeBuilder, List<ResourceWithConfiguration> resources, ResourceType type) throws Exception {

    }

    @Override
    public void accept(KieAssemblerService kieAssemblerService) {
        this.assemblers.put(kieAssemblerService.getResourceType(), kieAssemblerService);
    }
}
