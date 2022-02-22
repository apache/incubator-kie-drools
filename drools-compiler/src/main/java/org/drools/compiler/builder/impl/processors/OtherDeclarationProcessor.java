package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Arrays.asList;

public class OtherDeclarationProcessor extends AbstractPackageProcessor {


    private final KnowledgeBuilderImpl knowledgeBuilder;
    private final InternalKnowledgeBase kBase;
    private final KnowledgeBuilderConfiguration configuration;
    private final BiConsumer<InternalKnowledgePackage, String> globalCleanupCallback;

    public OtherDeclarationProcessor(
            KnowledgeBuilderImpl knowledgeBuilder,
            InternalKnowledgeBase kBase,
            KnowledgeBuilderConfiguration configuration,
            BiConsumer<InternalKnowledgePackage, String> globalCleanupCallback,
            PackageRegistry pkgRegistry,
            PackageDescr packageDescr) {
        super(pkgRegistry, packageDescr);
        this.knowledgeBuilder = knowledgeBuilder;
        this.kBase = kBase;
        this.configuration = configuration;
        this.globalCleanupCallback = globalCleanupCallback;
    }

    @Override
    public void process() {
        List<Processor> processors = asList(
                new AccumulateFunctionProcessor(pkgRegistry, packageDescr),
                new WindowDeclarationProcessor(pkgRegistry, packageDescr, knowledgeBuilder),
                new FunctionProcessor(pkgRegistry, packageDescr, configuration),
                new GlobalProcessor(pkgRegistry, packageDescr, kBase, knowledgeBuilder, globalCleanupCallback));

        processors.forEach(Processor::process);
        processors.forEach(p -> results.addAll(p.getResults()));
    }

}
