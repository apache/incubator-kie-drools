package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.DroolsAssemblerContext;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.drl.ast.descr.PackageDescr;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Arrays.asList;

public final class PackageProcessor extends AbstractPackageProcessor {
    private final KnowledgeBuilderImpl knowledgeBuilder;
    private final KnowledgeBaseImpl kBase;
    private final KnowledgeBuilderConfigurationImpl configuration;
    private final TypeDeclarationBuilder typeBuilder;
    private final BiConsumer<InternalKnowledgePackage, String> globalCleanupCallback;

    public PackageProcessor(
            KnowledgeBuilderImpl knowledgeBuilder,
            KnowledgeBaseImpl kBase,
            KnowledgeBuilderConfigurationImpl configuration,
            TypeDeclarationBuilder typeBuilder,
            BiConsumer<InternalKnowledgePackage, String> globalCleanupCallback,
            PackageRegistry pkgRegistry,
            PackageDescr packageDescr) {
        super(pkgRegistry, packageDescr);
        this.knowledgeBuilder = knowledgeBuilder;
        this.kBase = kBase;
        this.configuration = configuration;
        this.typeBuilder = typeBuilder;
        this.globalCleanupCallback = globalCleanupCallback;
    }

    public void process() {
        AnnotationNormalizer annotationNormalizer =
                AnnotationNormalizer.of(
                        pkgRegistry.getTypeResolver(),
                        configuration.getLanguageLevel().useJavaAnnotations());

        List<Processor> processors = asList(
                new ImportProcessor(pkgRegistry, packageDescr),
                new TypeDeclarationAnnotationNormalizer(annotationNormalizer, packageDescr),
                new EntryPointDeclarationProcessor(pkgRegistry, packageDescr),
                new AccumulateFunctionProcessor(pkgRegistry, packageDescr),
                new TypeDeclarationProcessor(packageDescr, typeBuilder, pkgRegistry),
                new WindowDeclarationProcessor(pkgRegistry, packageDescr, knowledgeBuilder),
                new FunctionProcessor(pkgRegistry, packageDescr, configuration),
                new GlobalProcessor(pkgRegistry, packageDescr, kBase, knowledgeBuilder, globalCleanupCallback),
                new RuleAnnotationNormalizer(annotationNormalizer, packageDescr));

        processors.forEach(Processor::process);
        processors.forEach(p -> results.addAll(p.getResults()));

    }

}


