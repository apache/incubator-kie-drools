package org.drools.compiler;

import org.kie.io.Resource;
import org.kie.io.ResourceConfiguration;
import org.kie.io.ResourceType;

public interface ResourceTypeBuilder {

    void setPackageBuilder(PackageBuilder packageBuilder);

    void addKnowledgeResource(Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception;

}
