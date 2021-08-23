package org.drools.modelcompiler.drlx;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.descr.PackageDescr;
import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

public class DrlxAssembler implements KieAssemblerService {

    @Override
    public ResourceType getResourceType() {
        return ResourceType.DRLX;
    }

    @Override
    public void addResourceBeforeRules(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        KnowledgeBuilderImpl kBuilder = (KnowledgeBuilderImpl) kbuilder;
        DrlxCompiler drlxCompiler = new DrlxCompiler();
        PackageDescr packageDescr = drlxCompiler.toPackageDescr(resource);
        kBuilder.addPackage(packageDescr);
        kBuilder.updateResults(drlxCompiler.getResults());
    }

    @Override
    public void addResourceAfterRules(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {

    }
}
