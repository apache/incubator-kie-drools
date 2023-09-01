package org.kie.api.internal.assembler;

import java.util.List;

import org.kie.api.internal.utils.KieService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;

public interface KieAssemblers extends KieService {

    void addResourceBeforeRules(
            Object knowledgeBuilder,
            Resource resource,
            ResourceType type,
            ResourceConfiguration configuration) throws Exception;

    void addResourceAfterRules(
            Object knowledgeBuilder,
            Resource resource,
            ResourceType type,
            ResourceConfiguration configuration) throws Exception;

    void addResourcesAfterRules(
            Object knowledgeBuilder,
            List<ResourceWithConfiguration> resources,
            ResourceType type) throws Exception;
}
