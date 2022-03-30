package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;

import java.util.List;

import static java.util.Arrays.asList;

public final class PackageCompilationPhase extends AbstractPackageCompilationPhase {
    private final KnowledgeBuilderImpl knowledgeBuilder;
    private final InternalKnowledgeBase kBase;
    private final KnowledgeBuilderConfigurationImpl configuration;
    private final TypeDeclarationBuilder typeBuilder;
    private final KnowledgeBuilderImpl.AssetFilter filterCondition;

    public PackageCompilationPhase(
            KnowledgeBuilderImpl knowledgeBuilder,
            InternalKnowledgeBase kBase,
            KnowledgeBuilderConfigurationImpl configuration,
            TypeDeclarationBuilder typeBuilder,
            KnowledgeBuilderImpl.AssetFilter filterCondition,
            PackageRegistry pkgRegistry,
            PackageDescr packageDescr) {
        super(pkgRegistry, packageDescr);
        this.knowledgeBuilder = knowledgeBuilder;
        this.kBase = kBase;
        this.configuration = configuration;
        this.typeBuilder = typeBuilder;
        this.filterCondition = filterCondition;
    }

    public void process() {
        AnnotationNormalizer annotationNormalizer =
                AnnotationNormalizer.of(
                        pkgRegistry.getTypeResolver(),
                        configuration.getLanguageLevel().useJavaAnnotations());

        List<CompilationPhase> phases = asList(
                new ImportCompilationPhase(pkgRegistry, packageDescr),
                new TypeDeclarationAnnotationNormalizer(annotationNormalizer, packageDescr),
                new EntryPointDeclarationCompilationPhase(pkgRegistry, packageDescr),
                new AccumulateFunctionCompilationPhase(pkgRegistry, packageDescr),
                new TypeDeclarationCompilationPhase(packageDescr, typeBuilder, pkgRegistry),
                new WindowDeclarationCompilationPhase(pkgRegistry, packageDescr, knowledgeBuilder),
                new FunctionCompilationPhase(pkgRegistry, packageDescr, configuration),
                new GlobalCompilationPhase(pkgRegistry, packageDescr, kBase, knowledgeBuilder, filterCondition),
                new RuleAnnotationNormalizer(annotationNormalizer, packageDescr));

        phases.forEach(CompilationPhase::process);
        phases.forEach(p -> results.addAll(p.getResults()));

    }

}


