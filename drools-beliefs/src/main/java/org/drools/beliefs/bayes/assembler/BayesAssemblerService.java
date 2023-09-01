package org.drools.beliefs.bayes.assembler;

import org.drools.beliefs.bayes.JunctionTree;
import org.drools.compiler.builder.AbstractAssemblerService;
import org.drools.compiler.builder.ResourceProcessor;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

public class BayesAssemblerService extends AbstractAssemblerService<BayesPackage, JunctionTree> {

    @Override
    public ResourceType getResourceType() {
        return ResourceType.BAYES;
    }

    @Override
    protected BayesPackage createPackage(String namespace) {
        return new BayesPackage(namespace);
    }

    @Override
    protected ResourceProcessor<JunctionTree> createResourceProcessor(Resource resource) {
        return new JunctionTreeProcessor(resource);
    }
}
