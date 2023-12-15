/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.compiler.compiler;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.QueryImpl;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.DialectConfiguration;
import org.drools.compiler.compiler.DuplicateFunction;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.rule.builder.AccumulateBuilder;
import org.drools.compiler.rule.builder.ConsequenceBuilder;
import org.drools.compiler.rule.builder.EnabledBuilder;
import org.drools.compiler.rule.builder.EntryPointBuilder;
import org.drools.compiler.rule.builder.FromBuilder;
import org.drools.compiler.rule.builder.GroupByBuilder;
import org.drools.compiler.rule.builder.GroupElementBuilder;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.PatternBuilder;
import org.drools.compiler.rule.builder.PatternBuilderForQuery;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleClassBuilder;
import org.drools.compiler.rule.builder.RuleConditionBuilder;
import org.drools.compiler.rule.builder.SalienceBuilder;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.EvalDescr;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.FunctionImportDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.ProcessDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.mvel.java.JavaForMvelDialectConfiguration;
import org.drools.util.TypeResolver;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.builder.conf.DefaultDialectOption;
import org.kie.internal.builder.conf.KBuilderSeverityOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class PackageBuilderConfigurationTest {

    private static String droolsDialectJavaCompilerOrig;
    private static String droolsDialectDefaultOrig;

    @BeforeClass
    public static void backupPropertyValues() {
        droolsDialectJavaCompilerOrig = System.getProperty( JavaForMvelDialectConfiguration.JAVA_COMPILER_PROPERTY);
        droolsDialectDefaultOrig = System.getProperty(DefaultDialectOption.PROPERTY_NAME);
    }

    @AfterClass
    public static void restorePropertyValues() {
        if (droolsDialectJavaCompilerOrig != null) {
            System.setProperty( JavaForMvelDialectConfiguration.JAVA_COMPILER_PROPERTY, droolsDialectJavaCompilerOrig);
        }
        if (droolsDialectDefaultOrig != null) {
            System.setProperty(DefaultDialectOption.PROPERTY_NAME, droolsDialectDefaultOrig);
        }
    }

    @Before
    public void setUp() throws Exception {
        System.getProperties().remove("drools.dialect.java.compiler");
        System.getProperties().remove("drools.dialect.default");
    }

    @After
    public void tearDown() throws Exception {
        System.getProperties().remove("drools.dialect.java.compiler");
        System.getProperties().remove("drools.dialect.default");
        System.getProperties().remove("drools.kbuilder.severity." + DuplicateFunction.KEY);
    }

    @Test
    public void testProgrammaticProperties() {
        KnowledgeBuilderConfigurationImpl cfg = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY);
        assertThat(cfg.getDefaultDialect().equals("java")).isTrue();

        Properties properties = new Properties();
        properties.setProperty("drools.dialect.default",
                               "mvel");
        KnowledgeBuilderConfigurationImpl cfg1 = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(properties, null).as(KnowledgeBuilderConfigurationImpl.KEY);
        assertThat(cfg1.getDefaultDialect()).isEqualTo("mvel");

        KnowledgeBuilderConfigurationImpl cfg2 = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(properties, null).as(KnowledgeBuilderConfigurationImpl.KEY);
        assertThat(cfg2.getDefaultDialect().getClass()).isEqualTo(cfg1.getDefaultDialect().getClass());
    }

    @Test
    public void testProgramaticProperties2() {
        JavaForMvelDialectConfiguration javaConf = new JavaForMvelDialectConfiguration();
        javaConf.init(KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY));
        javaConf.setCompiler( JavaForMvelDialectConfiguration.CompilerType.ECLIPSE);
        KnowledgeBuilderConfigurationImpl cfg = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY);
        cfg.setDialectConfiguration("java",
                                    javaConf);
        JavaForMvelDialectConfiguration javaConf2 = ( JavaForMvelDialectConfiguration ) cfg.getDialectConfiguration("java");
        assertThat(javaConf2).isSameAs(javaConf);
        assertThat(javaConf2.getCompiler()).isEqualTo(JavaForMvelDialectConfiguration.CompilerType.ECLIPSE);
    }

    @Test
    public void testResultSeverity() {
        System.setProperty("drools.kbuilder.severity." + DuplicateFunction.KEY,
                           "ERROR");
        KnowledgeBuilderConfigurationImpl cfg = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY);
        assertThat(1).isEqualTo(cfg.getOptionSubKeys(KBuilderSeverityOption.KEY).size());
        assertThat(ResultSeverity.ERROR).isEqualTo(cfg.getOption(KBuilderSeverityOption.KEY,
                                                                 DuplicateFunction.KEY).getSeverity());
    }

    @Test
    public void testResultSeverityNonExistingValueDefaultToInfo() {
        System.setProperty("drools.kbuilder.severity." + DuplicateFunction.KEY,
                           "FOO");
        KnowledgeBuilderConfigurationImpl cfg = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY);
        assertThat(1).isEqualTo(cfg.getOptionSubKeys(KBuilderSeverityOption.KEY).size());
        assertThat(ResultSeverity.INFO).isEqualTo(cfg.getOption(KBuilderSeverityOption.KEY,
                                                                DuplicateFunction.KEY).getSeverity());
    }

    @Test
    public void testMockDialect() {
        InternalKnowledgePackage pkg = CoreComponentFactory.get().createKnowledgePackage("org.pkg1");

        KnowledgeBuilderConfigurationImpl cfg1 = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY);
        MockDialectConfiguration mockConf = new MockDialectConfiguration();
        //        cfg1.buildDialectRegistry().addDialect( "mock",
        //                                                mockConf.getDialect() );

        cfg1.addDialect("mock",
                        mockConf);
        cfg1.setDefaultDialect("mock");

        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl(pkg, cfg1);

        PackageRegistry pkgRegistry = builder.getPackageRegistry(pkg.getName());
        DialectCompiletimeRegistry dialectRegistry = pkgRegistry.getDialectCompiletimeRegistry();
        MockDialect mockDialect2 = (MockDialect) dialectRegistry.getDialect(cfg1.getDefaultDialect());

        assertThat(mockDialect2.getPkg()).isSameAs(pkg);
        assertThat(mockDialect2.getRuleDescr()).isNull();

        RuleDescr ruleDescr = new RuleDescr("test rule");
        ruleDescr.addAttribute(new AttributeDescr("dialect",
                                                  "mock"));
        ruleDescr.setLhs(new AndDescr());
        EvalDescr evalDescr = new EvalDescr();
        ruleDescr.getLhs().addDescr(evalDescr);

        PackageDescr pkgDescr = new PackageDescr("org.pkg1");
        pkgDescr.addImport(new ImportDescr("java.util.HashMap"));
        FunctionImportDescr functionImportDescr = new FunctionImportDescr();
        functionImportDescr.setTarget("java.lang.System.currentTimeMillis");
        pkgDescr.addFunctionImport(functionImportDescr);

        pkgDescr.addRule(ruleDescr);

        builder.addPackage(pkgDescr);

        assertThat(mockDialect2.getRuleDescr()).isSameAs(ruleDescr);
        assertThat(mockDialect2.getImport().contains("java.util.HashMap")).isTrue();
        assertThat(mockDialect2.getStaticImport().contains("java.lang.System.currentTimeMillis")).isTrue();
        assertThat(evalDescr.getContent()).isEqualTo("eval was built");
        assertThat(ruleDescr.getConsequence()).isEqualTo("consequence was built");
        assertThat(mockDialect2.isCompileAll()).isTrue();

        assertThat(pkg.getRule("test rule")).isNotNull();

        // make sure there were no other general errors.
        assertThat(builder.hasErrors()).isFalse();
    }

    public static class MockDialectConfiguration
            implements
            DialectConfiguration {

        private KnowledgeBuilderConfigurationImpl conf;

        public Dialect newDialect( ClassLoader rootClassLoader,
                                   KnowledgeBuilderConfigurationImpl pkgConf,
                                   PackageRegistry pkgRegistry,
                                   InternalKnowledgePackage pkg) {
            return new MockDialect(rootClassLoader,
                                   pkgConf,
                                   pkgRegistry,
                                   pkg);
        }

        public KnowledgeBuilderConfigurationImpl getPackageBuilderConfiguration() {
            return this.conf;
        }

        public void init(KnowledgeBuilderConfigurationImpl configuration) {
            this.conf = configuration;
        }
    }

    public static class MockDialect
            implements
            Dialect {

        private InternalKnowledgePackage pkg;
        private RuleDescr ruleDescr;
        private RuleImpl rule;

        private List<String> imports = new ArrayList<String>();

        private boolean compileAll = false;

        public MockDialect(ClassLoader rootClassLoader,
                           KnowledgeBuilderConfigurationImpl pkgConf,
                           PackageRegistry pkgRegistry,
                           InternalKnowledgePackage pkg) {
            this.pkg = pkg;
        }

        public void init(RuleDescr ruleDescr) {
            this.ruleDescr = ruleDescr;
        }

        public InternalKnowledgePackage getPkg() {
            return pkg;
        }

        public RuleDescr getRuleDescr() {
            return ruleDescr;
        }

        public void addFunction(FunctionDescr functionDescr,
                                TypeResolver typeResolver) {
            // TODO Auto-generated method stub

        }

        public void addImport(ImportDescr importDescr) {
            this.imports.add(importDescr.getTarget());
        }

        public List getImport() {
            return this.imports;
        }

        public void addStaticImport(ImportDescr importDescr) {
            this.imports.add(importDescr.getTarget());
        }

        public List getStaticImport() {
            return this.imports;
        }

        public void addRule(RuleBuildContext context) {
            this.rule = context.getRule();
        }

        public RuleImpl getRule() {
            return this.rule;
        }

        public void compileAll() {
            this.compileAll = true;
        }

        public boolean isCompileAll() {
            return this.compileAll;
        }

        public AccumulateBuilder getAccumulateBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        public GroupByBuilder getGroupByBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        public RuleConditionBuilder getBuilder(Class clazz) {
            if (clazz == EvalDescr.class) {
                return getEvalBuilder();
            } else if (clazz == AndDescr.class) {
                return new GroupElementBuilder();
            } else {
                throw new RuntimeException("clazz " + clazz + " is not yet configured ");
            }
        }

        public Map getBuilders() {
            return null;
        }

        public ConsequenceBuilder getConsequenceBuilder() {
            return new MockConsequenceBuilder();
        }

        public RuleConditionBuilder getEvalBuilder() {
            return new MockEvalBuilder();
        }

        public FromBuilder getFromBuilder() {
            return null;
        }

        public PatternBuilder getPatternBuilder() {
            return null;
        }

        public PatternBuilderForQuery getPatternBuilderForQuery(QueryImpl query) {
            return null;
        }

        public List<KnowledgeBuilderResult> getResults() {
            return null;
        }

        public void clearResults() {
        }

        public RuleClassBuilder getRuleClassBuilder() {
            return null;
        }

        public SalienceBuilder getSalienceBuilder() {
            return null;
        }

        public TypeResolver getTypeResolver() {
            return null;
        }

        public String getId() {
            return "mock";
        }

        public void init(ProcessDescr processDescr) {
            // TODO Auto-generated method stub

        }

        public EntryPointBuilder getEntryPointBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        public void postCompileAddFunction(FunctionDescr functionDescr,
                                           TypeResolver typeResolver) {
            // TODO Auto-generated method stub

        }

        public void preCompileAddFunction(FunctionDescr functionDescr,
                                          TypeResolver typeResolver) {
            // TODO Auto-generated method stub

        }

        public EnabledBuilder getEnabledBuilder() {
            // TODO Auto-generated method stub
            return null;
        }

        public void addFunction(FunctionDescr functionDescr,
                                TypeResolver typeResolver,
                                Resource resource) {
            // TODO Auto-generated method stub

        }

        public AnalysisResult analyzeExpression( PackageBuildContext context,
                                                 BaseDescr descr,
                                                 Object content,
                                                 BoundIdentifiers availableIdentifiers) {
            // TODO Auto-generated method stub
            return null;
        }

        public AnalysisResult analyzeExpression(PackageBuildContext context,
                                                BaseDescr descr,
                                                Object content,
                                                BoundIdentifiers availableIdentifiers,
                                                Map<String, Class<?>> localTypes) {
            // TODO Auto-generated method stub
            return null;
        }

        public AnalysisResult analyzeBlock(PackageBuildContext context,
                                           BaseDescr descr,
                                           String text,
                                           BoundIdentifiers availableIdentifiers) {
            // TODO Auto-generated method stub
            return null;
        }

        public PackageRegistry getPackageRegistry() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    public static class MockEvalBuilder
            implements
            RuleConditionBuilder {

        public RuleConditionElement build(RuleBuildContext context,
                                          BaseDescr descr) {
            EvalDescr evalDescr = (EvalDescr) descr;
            evalDescr.setContent("eval was built");
            return null;
        }

        public RuleConditionElement build(RuleBuildContext context,
                                          BaseDescr descr,
                                          Pattern prefixPattern) {
            return null;
        }
    }

    public static class MockConsequenceBuilder
            implements
            ConsequenceBuilder {

        public void build(RuleBuildContext context,
                          String name) {
            context.getRuleDescr().setConsequence("consequence was built");
        }
    }
}
