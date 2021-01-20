package org.drools.compiler.builder.impl;

import org.drools.compiler.compiler.BaseKnowledgeBuilderResultImpl;
import org.drools.compiler.lang.descr.PackageDescr;
import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;

public class TestAssembler implements KieAssemblerService {

    @Override
    public ResourceType getResourceType() {
        // we are just picking an arbitrary resource that does not have a local implementation
        return ResourceType.DMN;
    }

    @Override
    public void addResourceAsPackageDescr(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        KnowledgeBuilderImpl kb = (KnowledgeBuilderImpl) kbuilder;
        kb.registerPackage(new PackageDescr("FAKEpkg"));
        kb.addBuilderResult(BEFORE_RULES);
    }

    @Override
    public void addResource(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        KnowledgeBuilderImpl kb = (KnowledgeBuilderImpl) kbuilder;
        kb.addBuilderResult(AFTER_RULES);

    }

    public static final KnowledgeBuilderResult AFTER_RULES = new BaseKnowledgeBuilderResultImpl(null) {

        @Override
        public ResultSeverity getSeverity() {
            return ResultSeverity.INFO;
        }

        @Override
        public String getMessage() {
            return null;
        }

        @Override
        public int[] getLines() {
            return new int[0];
        }

    };

    public static final KnowledgeBuilderResult BEFORE_RULES = new BaseKnowledgeBuilderResultImpl(null) {

        @Override
        public ResultSeverity getSeverity() {
            return ResultSeverity.INFO;
        }

        @Override
        public String getMessage() {
            return null;
        }

        @Override
        public int[] getLines() {
            return new int[0];
        }

    };
}
