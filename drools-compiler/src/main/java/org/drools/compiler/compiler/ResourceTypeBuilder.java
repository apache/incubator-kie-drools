package org.drools.compiler.compiler;

import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;

public interface ResourceTypeBuilder {

    void setPackageBuilder(KnowledgeBuilder kBuilder);

    void addKnowledgeResource(Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception;

}
