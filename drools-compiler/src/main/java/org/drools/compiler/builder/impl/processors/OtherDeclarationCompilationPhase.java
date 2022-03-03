package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;

import java.util.List;

import static java.util.Arrays.asList;

public class OtherDeclarationCompilationPhase extends AbstractPackageCompilationPhase {


    private final KnowledgeBuilderImpl knowledgeBuilder;
    private final InternalKnowledgeBase kBase;
    private final KnowledgeBuilderConfiguration configuration;
    private final FilterCondition filter;

    public OtherDeclarationCompilationPhase(
            KnowledgeBuilderImpl knowledgeBuilder,
            InternalKnowledgeBase kBase,
            KnowledgeBuilderConfiguration configuration,
            FilterCondition filter,
            PackageRegistry pkgRegistry,
            PackageDescr packageDescr) {
        super(pkgRegistry, packageDescr);
        this.knowledgeBuilder = knowledgeBuilder;
        this.kBase = kBase;
        this.configuration = configuration;
        this.filter = filter;
    }

    @Override
    public void process() {
        List<CompilationPhase> phases = asList(
                new AccumulateFunctionCompilationPhase(pkgRegistry, packageDescr),
                new WindowDeclarationCompilationPhase(pkgRegistry, packageDescr, knowledgeBuilder),
                new FunctionCompilationPhase(pkgRegistry, packageDescr, configuration),
                new GlobalCompilationPhase(pkgRegistry, packageDescr, kBase, knowledgeBuilder, filter));

        phases.forEach(CompilationPhase::process);
        phases.forEach(p -> results.addAll(p.getResults()));
    }

}
