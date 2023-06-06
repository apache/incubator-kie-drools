package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.AssetFilter;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.conf.LanguageLevelOption;

import java.util.List;

import static java.util.Arrays.asList;

public final class PackageCompilationPhase extends AbstractPackageCompilationPhase {
    private final KnowledgeBuilderImpl knowledgeBuilder;
    private final InternalKnowledgeBase kBase;
    private final KnowledgeBuilderConfiguration configuration;
    private final TypeDeclarationBuilder typeBuilder;
    private final AssetFilter filterCondition;
    private final Resource currentResource;

    public PackageCompilationPhase(
            KnowledgeBuilderImpl knowledgeBuilder,
            InternalKnowledgeBase kBase,
            KnowledgeBuilderConfiguration configuration,
            TypeDeclarationBuilder typeBuilder,
            AssetFilter filterCondition,
            PackageRegistry pkgRegistry,
            PackageDescr packageDescr,
            Resource currentResource) {
        super(pkgRegistry, packageDescr);
        this.knowledgeBuilder = knowledgeBuilder;
        this.kBase = kBase;
        this.configuration = configuration;
        this.typeBuilder = typeBuilder;
        this.filterCondition = filterCondition;
        this.currentResource = currentResource;
    }

    public void process() {
        AnnotationNormalizer annotationNormalizer =
                AnnotationNormalizer.of(
                        pkgRegistry.getTypeResolver(),
                        configuration.getOption(LanguageLevelOption.KEY).useJavaAnnotations());

        List<CompilationPhase> phases = asList(
                new ImportCompilationPhase(pkgRegistry, packageDescr),
                new TypeDeclarationAnnotationNormalizer(annotationNormalizer, packageDescr),
                new EntryPointDeclarationCompilationPhase(pkgRegistry, packageDescr),
                new AccumulateFunctionCompilationPhase(pkgRegistry, packageDescr),
                new TypeDeclarationCompilationPhase(packageDescr, typeBuilder, pkgRegistry, currentResource),
                new WindowDeclarationCompilationPhase(pkgRegistry, packageDescr, knowledgeBuilder),
                new FunctionCompilationPhase(pkgRegistry, packageDescr, configuration),
                GlobalCompilationPhase.of(pkgRegistry, packageDescr, kBase, knowledgeBuilder, filterCondition),
                new RuleAnnotationNormalizer(annotationNormalizer, packageDescr));

        phases.forEach(CompilationPhase::process);
        phases.forEach(p -> results.addAll(p.getResults()));

    }

}


