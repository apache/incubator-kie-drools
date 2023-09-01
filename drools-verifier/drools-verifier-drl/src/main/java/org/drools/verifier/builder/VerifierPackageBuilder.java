package org.drools.verifier.builder;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageBuilderErrors;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilderFactory;

/**
 * Wraps the PackageBuilder for Verifier.
 * Used to build PackageDescrs.
 */
class VerifierPackageBuilder {

    private InnerBuilder innerBuilder = new InnerBuilder();

    private PackageDescr packageDescr;

    public void addKnowledgeResource(Resource resource,
                                     ResourceType type,
                                     ResourceConfiguration configuration) {
        innerBuilder.addKnowledgeResource( resource,
                                           type,
                                           configuration );
    }

    public PackageDescr getPackageDescr() {
        return packageDescr;
    }

    public boolean hasErrors() {
        return innerBuilder.hasErrors();
    }

    public PackageBuilderErrors getErrors() {
        return innerBuilder.getErrors();
    }

    class InnerBuilder extends KnowledgeBuilderImpl {
        public InnerBuilder() {
            super(KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY));
        }

        @Override
        protected void addPackageWithResource(PackageDescr pDescr, Resource unused) {
            packageDescr = pDescr;
        }
    }
}
