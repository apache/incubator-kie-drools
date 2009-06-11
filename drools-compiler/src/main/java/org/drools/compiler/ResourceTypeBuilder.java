package org.drools.compiler;

import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;

public interface ResourceTypeBuilder {
	
	void setPackageBuilder(PackageBuilder packageBuilder);
	
	void addKnowledgeResource(Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception;

}
