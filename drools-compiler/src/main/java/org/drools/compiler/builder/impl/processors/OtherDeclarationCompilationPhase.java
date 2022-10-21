package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.AssetFilter;
import org.drools.compiler.builder.impl.GlobalVariableContext;
import org.drools.compiler.builder.impl.TypeDeclarationContext;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;

import java.util.List;

import static java.util.Arrays.asList;

public class OtherDeclarationCompilationPhase extends AbstractPackageCompilationPhase {

    private final GlobalVariableContext globalVariableContext;
    private final TypeDeclarationContext typeDeclarationContext;
    private final InternalKnowledgeBase kBase;
    private final KnowledgeBuilderConfiguration configuration;
    private final AssetFilter assetFilter;

    public OtherDeclarationCompilationPhase(
            PackageRegistry pkgRegistry,
            PackageDescr packageDescr,
            GlobalVariableContext globalVariableContext,
            TypeDeclarationContext typeDeclarationContext,
            InternalKnowledgeBase kBase,
            KnowledgeBuilderConfiguration configuration,
            AssetFilter assetFilter) {
        super(pkgRegistry, packageDescr);
        this.globalVariableContext = globalVariableContext;
        this.typeDeclarationContext = typeDeclarationContext;
        this.kBase = kBase;
        this.configuration = configuration;
        this.assetFilter = assetFilter;
    }

    @Override
    public void process() {
        List<CompilationPhase> phases = asList(
                new AccumulateFunctionCompilationPhase(pkgRegistry, packageDescr),
                new WindowDeclarationCompilationPhase(pkgRegistry, packageDescr, typeDeclarationContext),
                new FunctionCompilationPhase(pkgRegistry, packageDescr, configuration),
                GlobalCompilationPhase.of(pkgRegistry, packageDescr, kBase, globalVariableContext, assetFilter));

        phases.forEach(CompilationPhase::process);
        phases.forEach(p -> results.addAll(p.getResults()));
    }

}
