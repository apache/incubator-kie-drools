package org.kie.internal.assembler;

import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.utils.KieService;

public interface KieAssemblerService extends KieService {
    ResourceType getResourceType();

    void addResource(KnowledgeBuilder kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception;
}