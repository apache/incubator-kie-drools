package org.drools.compiler.integrationtests.phases;

import org.drools.compiler.builder.DroolsAssemblerContext;
import org.drools.compiler.builder.impl.BuildResultAccumulator;
import org.drools.compiler.builder.impl.BuildResultAccumulatorImpl;
import org.drools.compiler.builder.impl.DroolsAssemblerContextImpl;
import org.drools.compiler.builder.impl.GlobalVariableContext;
import org.drools.compiler.builder.impl.GlobalVariableContextImpl;
import org.drools.compiler.builder.impl.InternalKnowledgeBaseProvider;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.PackageAttributeManagerImpl;
import org.drools.compiler.builder.impl.PackageRegistryManagerImpl;
import org.drools.compiler.builder.impl.RootClassLoaderProvider;
import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.builder.impl.TypeDeclarationContextImpl;
import org.drools.compiler.builder.impl.processors.AccumulateFunctionCompilationPhase;
import org.drools.compiler.builder.impl.processors.AnnotationNormalizer;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.compiler.builder.impl.processors.EntryPointDeclarationCompilationPhase;
import org.drools.compiler.builder.impl.processors.FunctionCompilationPhase;
import org.drools.compiler.builder.impl.processors.FunctionCompiler;
import org.drools.compiler.builder.impl.processors.GlobalCompilationPhase;
import org.drools.compiler.builder.impl.processors.ImportCompilationPhase;
import org.drools.compiler.builder.impl.processors.ReteCompiler;
import org.drools.compiler.builder.impl.processors.RuleAnnotationNormalizer;
import org.drools.compiler.builder.impl.processors.RuleCompiler;
import org.drools.compiler.builder.impl.processors.RuleValidator;
import org.drools.compiler.builder.impl.processors.TypeDeclarationAnnotationNormalizer;
import org.drools.compiler.builder.impl.processors.TypeDeclarationCompilationPhase;
import org.drools.compiler.builder.impl.processors.WindowDeclarationCompilationPhase;
import org.drools.compiler.builder.impl.resources.DrlResourceHandler;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.impl.RuleBase;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DroolsParserException;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.kiesession.rulebase.SessionsAwareKnowledgeBase;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.internal.builder.ResourceChange;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class ExplicitCompilerTest {

    @Test
    //@Ignore("not finished")
    public void testCompile() throws DroolsParserException, IOException {
        ClassLoader rootClassLoader = this.getClass().getClassLoader();
        KnowledgeBuilderConfigurationImpl configuration = new KnowledgeBuilderConfigurationImpl();
        BuildResultAccumulator results = new BuildResultAccumulatorImpl();

        Resource resource = new ClassPathResource("com/sample/from.drl");

        int parallelRulesBuildThreshold = 0;

        DrlResourceHandler handler = new DrlResourceHandler(configuration);
        final PackageDescr packageDescr = handler.process(resource);
        handler.getResults().forEach(results::addBuilderResult);

        PackageAttributeManagerImpl packageAttributes = new PackageAttributeManagerImpl();
        RootClassLoaderProvider rootClassLoaderProvider = () -> rootClassLoader;
        InternalKnowledgeBaseProvider internalKnowledgeBaseProvider = () -> null;
        PackageRegistryManagerImpl packageRegistryManager =
                new PackageRegistryManagerImpl(
                        configuration, packageAttributes, rootClassLoaderProvider, internalKnowledgeBaseProvider);
        TypeDeclarationContextImpl typeDeclarationContext = new TypeDeclarationContextImpl(configuration, packageRegistryManager);
        TypeDeclarationBuilder typeBuilder = new TypeDeclarationBuilder(typeDeclarationContext);
        typeDeclarationContext.setTypeDeclarationBuilder(typeBuilder);

        PackageRegistry packageRegistry = packageRegistryManager.getOrCreatePackageRegistry(packageDescr);
        GlobalVariableContext globalVariableContext = new GlobalVariableContextImpl();

        InternalKnowledgeBase kBase = null;
        DroolsAssemblerContext kBuilder =
                new DroolsAssemblerContextImpl(
                        configuration,
                        rootClassLoader,
                        kBase,
                        globalVariableContext,
                        typeBuilder,
                        packageRegistryManager,
                        results);


        AnnotationNormalizer annotationNormalizer =
                AnnotationNormalizer.of(
                        packageRegistry.getTypeResolver(),
                        configuration.getLanguageLevel().useJavaAnnotations());


        packageRegistry.setDialect(getPackageDialect(packageDescr));

        Map<String, AttributeDescr> attributesForPackage = packageAttributes.get(packageDescr.getNamespace());
        List<CompilationPhase> phases = asList(
                new ImportCompilationPhase(packageRegistry, packageDescr),
                new TypeDeclarationAnnotationNormalizer(annotationNormalizer, packageDescr),
                new EntryPointDeclarationCompilationPhase(packageRegistry, packageDescr),
                new AccumulateFunctionCompilationPhase(packageRegistry, packageDescr),
                new TypeDeclarationCompilationPhase(packageDescr, typeBuilder, packageRegistry),
                new WindowDeclarationCompilationPhase(packageRegistry, packageDescr, kBuilder),
                new FunctionCompilationPhase(packageRegistry, packageDescr, configuration),
                new GlobalCompilationPhase(packageRegistry, packageDescr, kBase, globalVariableContext, this::filterAcceptsRemoval),
                new RuleAnnotationNormalizer(annotationNormalizer, packageDescr),
                /*         packageRegistry.setDialect(getPackageDialect(packageDescr)) */
                new RuleValidator(packageRegistry, packageDescr, configuration),
                new FunctionCompiler(packageDescr, packageRegistry, this::filterAccepts, rootClassLoader),
                new RuleCompiler(packageRegistry, packageDescr, kBase, parallelRulesBuildThreshold,
                        this::filterAccepts, this::filterAcceptsRemoval, attributesForPackage, resource, kBuilder));


        phases.forEach(CompilationPhase::process);
        phases.forEach(p -> p.getResults().forEach(results::addBuilderResult));


        ReteCompiler reteCompiler =
                new ReteCompiler(packageRegistry, packageDescr, kBase, this::filterAccepts);
        reteCompiler.process();

        RuleBase kbase = RuleBaseFactory.newRuleBase(new RuleBaseConfiguration(rootClassLoader));
        kbase.addPackages(packageRegistryManager.getPackageRegistry().values().stream().map(PackageRegistry::getPackage).collect(Collectors.toList()));
        StatefulKnowledgeSessionImpl statefulKnowledgeSession = new StatefulKnowledgeSessionImpl(1, new SessionsAwareKnowledgeBase(kbase));

        statefulKnowledgeSession.insert("HELLO");

        statefulKnowledgeSession.fireAllRules();


    }

    private String getPackageDialect(PackageDescr packageDescr) {
        return null;
    }

    private boolean filterAccepts(ResourceChange.Type type, String namespace, String name) {
        return true;
    }

    private boolean filterAcceptsRemoval(ResourceChange.Type type, String namespace, String name) {
        return false;
    }
}
