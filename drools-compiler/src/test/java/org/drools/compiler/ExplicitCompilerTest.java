package org.drools.compiler;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.builder.impl.processors.FunctionCompiler;
import org.drools.compiler.builder.impl.processors.PackageProcessor;
import org.drools.compiler.builder.impl.processors.Processor;
import org.drools.compiler.builder.impl.processors.ReteCompiler;
import org.drools.compiler.builder.impl.processors.RuleCompiler;
import org.drools.compiler.builder.impl.processors.RuleValidator;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.common.ObjectStoreWrapper;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DrlParser;
import org.drools.drl.parser.DroolsParserException;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResourceChange;
import org.kie.internal.builder.conf.LanguageLevelOption;

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
    public void testCompile() throws DroolsParserException {
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

        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL6_STRICT);
        final PackageDescr packageDescr = parser.parse(resource, reader);
        this.results.addAll(parser.getErrors());

        PackageProcessor packageProcessor =
                new PackageProcessor(kBuilder,
                        kBase,
                        configuration,
                        typeBuilder,
                        this::filterAccepts,
                        packageRegistry,
                        packageDescr);
        packageProcessor.process();
        this.results.addAll(packageProcessor.getResults());

        packageRegistry.setDialect(getPackageDialect(packageDescr));

        List<Processor> processors = asList(
                new RuleValidator(packageRegistry, packageDescr, configuration),
                new FunctionCompiler(packageDescr, packageRegistry, this::filterAccepts, rootClassLoader),
                new RuleCompiler(packageRegistry, packageDescr, kBase, parallelRulesBuildThreshold,
                        this::filterAccepts, this::filterAcceptsRemoval, packageAttributes, resource, kBuilder));
        processors.forEach(Processor::process);
        processors.forEach(p -> this.results.addAll(p.getResults()));


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
