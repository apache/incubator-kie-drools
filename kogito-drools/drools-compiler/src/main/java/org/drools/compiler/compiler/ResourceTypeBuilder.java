package org.drools.compiler.compiler;

import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

public interface ResourceTypeBuilder {

    void setPackageBuilder(PackageBuilder packageBuilder);

    void addKnowledgeResource(Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception;

}
