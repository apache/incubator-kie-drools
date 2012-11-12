package org.drools.compiler;

import org.kie.builder.ResourceConfiguration;
import org.kie.builder.ResourceType;
import org.kie.io.Resource;

public interface ResourceTypeBuilder {

    void setPackageBuilder(PackageBuilder packageBuilder);

    void addKnowledgeResource(Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception;

}
