package org.drools.modelcompiler.assembler;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.lang.descr.FunctionDescr;
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
    public void addResourceBeforeRules(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        KnowledgeBuilderImpl kb = (KnowledgeBuilderImpl) kbuilder;

        kb.addBuilderResult(BEFORE_RULES);

    }

    @Override
    public void addResourceAfterRules(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        KnowledgeBuilderImpl kb = (KnowledgeBuilderImpl) kbuilder;
        PackageDescr fakEpkg = new PackageDescr("FAKEpkg");
        fakEpkg.addFunction(new FunctionDescr("INVALID", "INVALID"));
        kb.addPackage(fakEpkg);

        kb.addBuilderResult(AFTER_RULES);

    }

    public static final KnowledgeBuilderResult AFTER_RULES = new DroolsError(null) {

        @Override
        public ResultSeverity getSeverity() {
            return ResultSeverity.WARNING;
        }

        @Override
        public String getMessage() {
            return "AFTER_RULES";
        }

        @Override
        public int[] getLines() {
            return new int[0];
        }

    };

    public static final KnowledgeBuilderResult BEFORE_RULES = new DroolsError(null) {

        @Override
        public ResultSeverity getSeverity() {
            return ResultSeverity.WARNING;
        }

        @Override
        public String getMessage() {
            return "BEFORE_RULES";
        }

        @Override
        public int[] getLines() {
            return new int[0];
        }

    };
}
