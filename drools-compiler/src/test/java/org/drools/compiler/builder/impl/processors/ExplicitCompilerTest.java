package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.resources.DrlResourceHandler;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DroolsParserException;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResourceChange;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class ExplicitCompilerTest {

    private List<KnowledgeBuilderResult> results = new ArrayList<>();


    @Test
    @Ignore("not finished")
    public void testCompile() throws DroolsParserException, IOException {
        final Reader reader = null;
        final Resource resource = null;
        InternalKnowledgeBase kBase = null;
        KnowledgeBuilderConfigurationImpl configuration = null;
        TypeDeclarationBuilder typeBuilder = null;
        PackageRegistry packageRegistry = null;
        KnowledgeBuilderImpl kBuilder = null;
        ClassLoader rootClassLoader = null;
        int parallelRulesBuildThreshold = 0;
        Map<String, Map<String, AttributeDescr>> packageAttributes = Collections.emptyMap();

        DrlResourceHandler handler = new DrlResourceHandler(configuration);
        final PackageDescr packageDescr = handler.process(resource);
        this.results.addAll(handler.getResults());

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
                new GlobalCompilationPhase(packageRegistry, packageDescr, kBase, kBuilder, this::filterAcceptsRemoval),
                new RuleAnnotationNormalizer(annotationNormalizer, packageDescr),
                /*         packageRegistry.setDialect(getPackageDialect(packageDescr)) */
                new RuleValidator(packageRegistry, packageDescr, configuration),
                new FunctionCompiler(packageDescr, packageRegistry, this::filterAccepts, rootClassLoader),
                new RuleCompiler(packageRegistry, packageDescr, kBase, parallelRulesBuildThreshold,
                        this::filterAccepts, this::filterAcceptsRemoval, attributesForPackage, resource, kBuilder));


        phases.forEach(CompilationPhase::process);
        phases.forEach(p -> this.results.addAll(p.getResults()));


        ReteCompiler reteCompiler =
                new ReteCompiler(packageRegistry, packageDescr, kBase, this::filterAccepts);
        reteCompiler.process();


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
