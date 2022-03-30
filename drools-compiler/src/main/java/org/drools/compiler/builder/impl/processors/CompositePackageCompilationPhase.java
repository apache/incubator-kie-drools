package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.DroolsAssemblerContext;
import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.BuildResultAccumulator;
import org.drools.compiler.builder.impl.BuildResultAccumulatorImpl;
import org.drools.compiler.builder.impl.GlobalVariableContext;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.core.addon.TypeResolver;
import org.drools.core.util.StringUtils;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

public class CompositePackageCompilationPhase implements CompilationPhase {
    private final Collection<CompositePackageDescr> packages;
    private final PackageRegistryManager pkgRegistryManager;
    private final TypeDeclarationBuilder typeBuilder;
    private GlobalVariableContext globalVariableContext;
    private DroolsAssemblerContext droolsAssemblerContext;
    private KnowledgeBuilderImpl.AssetFilter assetFilter;
    private final InternalKnowledgeBase kBase;
    private final KnowledgeBuilderConfigurationImpl configuration;

    private final BuildResultAccumulator buildResultAccumulator;

    public CompositePackageCompilationPhase(
            Collection<CompositePackageDescr> packages,
            PackageRegistryManager pkgRegistryManager,
            TypeDeclarationBuilder typeBuilder,
            GlobalVariableContext globalVariableContext,
            DroolsAssemblerContext droolsAssemblerContext,
            BuildResultAccumulator buildResultAccumulator,
            KnowledgeBuilderImpl.AssetFilter assetFilter,
            InternalKnowledgeBase kBase,
            KnowledgeBuilderConfigurationImpl configuration) {
        this.packages = packages;
        this.pkgRegistryManager = pkgRegistryManager;
        this.typeBuilder = typeBuilder;
        this.globalVariableContext = globalVariableContext;
        this.droolsAssemblerContext = droolsAssemblerContext;
        this.buildResultAccumulator = buildResultAccumulator;
        this.assetFilter = assetFilter;
        this.kBase = kBase;
        this.configuration = configuration;
    }


    @Override
    public void process() {
        Map<String, Supplier<AnnotationNormalizer>> annotationNormalizers =
                initAnnotationNormalizers();

        Collection<CompilationPhase> phases = asList(
                iteratingPhase((pkgRegistry, packageDescr) ->
                        new TypeDeclarationAnnotationNormalizer(annotationNormalizers.get(packageDescr.getNamespace()).get(), packageDescr)),
                new TypeDeclarationCompositeCompilationPhase(packages, typeBuilder),
                iteratingPhase(ImportCompilationPhase::new),
                iteratingPhase(EntryPointDeclarationCompilationPhase::new),
                iteratingPhase((pkgRegistry, packageDescr) ->
                            new OtherDeclarationCompilationPhase(
                                    pkgRegistry, packageDescr, globalVariableContext, droolsAssemblerContext, kBase, configuration, assetFilter)),
                iteratingPhase((pkgRegistry, packageDescr) ->
                        new RuleAnnotationNormalizer(annotationNormalizers.get(packageDescr.getNamespace()).get(), packageDescr))
        );

        for (CompilationPhase phase : phases) {
            phase.process();
            phase.getResults().forEach(this.buildResultAccumulator::addBuilderResult);
        }

    }

    private Map<String, Supplier<AnnotationNormalizer>> initAnnotationNormalizers() {
        // use a supplier to ensure a fresh instance
        Map<String, Supplier<AnnotationNormalizer>> annotationNormalizers = new HashMap<>();
        boolean isStrict = configuration.getLanguageLevel().useJavaAnnotations();
        for (CompositePackageDescr packageDescr : packages) {
            if (StringUtils.isEmpty(packageDescr.getName())) {
                packageDescr.setName(configuration.getDefaultPackageName());
            }
            PackageRegistry pkgRegistry = pkgRegistryManager.getOrCreatePackageRegistry(packageDescr);
            TypeResolver typeResolver = pkgRegistry.getTypeResolver();

            annotationNormalizers.put(
                    packageDescr.getNamespace(),
                    () -> AnnotationNormalizer.of(typeResolver, isStrict));
        }
        return annotationNormalizers;
    }

    private IteratingPhase iteratingPhase(SinglePackagePhaseFactory phaseFactory) {
        return new IteratingPhase(packages, pkgRegistryManager, phaseFactory);
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return buildResultAccumulator.getResults(ResultSeverity.INFO, ResultSeverity.WARNING, ResultSeverity.ERROR);
    }
}

interface SinglePackagePhaseFactory {
    CompilationPhase create(PackageRegistry pkgRegistry, CompositePackageDescr packageDescr);
}

class IteratingPhase implements CompilationPhase {
    private final Collection<CompositePackageDescr> packages;
    private final PackageRegistryManager pkgRegistryManager;
    private final SinglePackagePhaseFactory phaseFactory;

    private final Collection<KnowledgeBuilderResult> results = new ArrayList<>();

    public IteratingPhase(Collection<CompositePackageDescr> packages, PackageRegistryManager pkgRegistryManager, SinglePackagePhaseFactory phaseFactory) {
        this.packages = packages;
        this.pkgRegistryManager = pkgRegistryManager;
        this.phaseFactory = phaseFactory;
    }

    @Override
    public void process() {
        for (CompositePackageDescr compositePackageDescr : packages) {
            CompilationPhase phase = phaseFactory.create(
                    pkgRegistryManager.getPackageRegistry(compositePackageDescr.getNamespace()),
                    compositePackageDescr);
            phase.process();
            results.addAll(phase.getResults());
        }
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return results;
    }
}