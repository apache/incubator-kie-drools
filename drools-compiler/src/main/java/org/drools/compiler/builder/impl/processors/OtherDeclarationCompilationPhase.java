package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.DroolsAssemblerContext;
import org.drools.compiler.builder.impl.GlobalVariableContext;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;

import java.util.List;

import static java.util.Arrays.asList;

public class OtherDeclarationCompilationPhase extends AbstractPackageCompilationPhase {

    private final GlobalVariableContext globalVariableContext;
    private final DroolsAssemblerContext droolsAssemblerContext;
    private final InternalKnowledgeBase kBase;
    private final KnowledgeBuilderConfiguration configuration;
    private final KnowledgeBuilderImpl.AssetFilter assetFilter;

    public OtherDeclarationCompilationPhase(
            PackageRegistry pkgRegistry,
            PackageDescr packageDescr,
            GlobalVariableContext globalVariableContext,
            DroolsAssemblerContext droolsAssemblerContext,
            InternalKnowledgeBase kBase,
            KnowledgeBuilderConfiguration configuration,
            KnowledgeBuilderImpl.AssetFilter assetFilter) {
        super(pkgRegistry, packageDescr);
        this.globalVariableContext = globalVariableContext;
        this.droolsAssemblerContext = droolsAssemblerContext;
        this.kBase = kBase;
        this.configuration = configuration;
        this.assetFilter = assetFilter;
    }

    @Override
    public void process() {
        List<CompilationPhase> phases = asList(
                new AccumulateFunctionCompilationPhase(pkgRegistry, packageDescr),
                new WindowDeclarationCompilationPhase(pkgRegistry, packageDescr, droolsAssemblerContext),
                new FunctionCompilationPhase(pkgRegistry, packageDescr, configuration),
                new GlobalCompilationPhase(pkgRegistry, packageDescr, kBase, globalVariableContext, assetFilter));

        phases.forEach(CompilationPhase::process);
        phases.forEach(p -> results.addAll(p.getResults()));
    }

}
