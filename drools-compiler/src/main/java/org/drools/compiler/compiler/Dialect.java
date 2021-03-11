/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.compiler;

import java.util.List;
import java.util.Map;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.EntryPointDescr;
import org.drools.compiler.lang.descr.ExistsDescr;
import org.drools.compiler.lang.descr.FunctionDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.rule.builder.AccumulateBuilder;
import org.drools.compiler.rule.builder.ConsequenceBuilder;
import org.drools.compiler.rule.builder.EnabledBuilder;
import org.drools.compiler.rule.builder.EngineElementBuilder;
import org.drools.compiler.rule.builder.EntryPointBuilder;
import org.drools.compiler.rule.builder.FromBuilder;
import org.drools.compiler.rule.builder.GroupElementBuilder;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.PatternBuilder;
import org.drools.compiler.rule.builder.PredicateBuilder;
import org.drools.compiler.rule.builder.QueryBuilder;
import org.drools.compiler.rule.builder.ReturnValueBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleClassBuilder;
import org.drools.compiler.rule.builder.RuleConditionBuilder;
import org.drools.compiler.rule.builder.SalienceBuilder;
import org.drools.core.addon.TypeResolver;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;

import static org.drools.core.base.CoreComponentsBuilder.throwExceptionForMissingMvel;

/**
 * A Dialect implementation handles the building and execution of code
 * expressions and blocks for a rule. This api is considered unstable, and
 * subject to change. Those wishing to implement their own dialects should look
 * ove the MVEL and Java dialect implementations.
 */
public interface Dialect {

    String getId();

    // this is needed because some dialects use other dialects
    // to build complex expressions. Example: java dialect uses MVEL
    // to execute complex expressions
    String getExpressionDialectName();

    Map<Class<?>, EngineElementBuilder> getBuilders();

    TypeResolver getTypeResolver();

    SalienceBuilder getSalienceBuilder();

    EnabledBuilder getEnabledBuilder();

    PatternBuilder getPatternBuilder();

    QueryBuilder getQueryBuilder();

    RuleConditionBuilder getEvalBuilder();

    AccumulateBuilder getAccumulateBuilder();

    PredicateBuilder getPredicateBuilder();

    ReturnValueBuilder getReturnValueBuilder();

    ConsequenceBuilder getConsequenceBuilder();

    RuleClassBuilder getRuleClassBuilder();

    FromBuilder getFromBuilder();

    EntryPointBuilder getEntryPointBuilder();

    EngineElementBuilder getBuilder(Class clazz);

    AnalysisResult analyzeExpression(final PackageBuildContext context,
                                     final BaseDescr descr,
                                     final Object content,
                                     final BoundIdentifiers availableIdentifiers);

    AnalysisResult analyzeBlock(final PackageBuildContext context,
                                final BaseDescr descr,
                                final String text,
                                final BoundIdentifiers availableIdentifiers);

    void compileAll();

    void addRule(final RuleBuildContext context);

    void addFunction(FunctionDescr functionDescr,
                     TypeResolver typeResolver,
                     Resource resource);

    void addImport(ImportDescr importDescr);

    void addStaticImport(ImportDescr importDescr);

    List<KnowledgeBuilderResult> getResults();

    void clearResults();

    void init(RuleDescr ruleDescr);

    void init(ProcessDescr processDescr);

    void postCompileAddFunction(FunctionDescr functionDescr,
                                TypeResolver typeResolver);

    void preCompileAddFunction(FunctionDescr functionDescr,
                               TypeResolver typeResolver);

    PackageRegistry getPackageRegistry();

    default boolean isStrictMode() { return true; }

    default boolean isJava() { return true; }

    default void addSrc(String resourceName, byte[] content) {
        throw new UnsupportedOperationException();
    }

    class DummyDialect implements Dialect {

        public static final String ID = "java";

        private final InternalKnowledgePackage pkg;
        private final ClassLoader rootClassLoader;
        private final KnowledgeBuilderConfigurationImpl pkgConf;
        private final PackageRegistry packageRegistry;

        DummyDialect(ClassLoader rootClassLoader, KnowledgeBuilderConfigurationImpl pkgConf, PackageRegistry pkgRegistry, InternalKnowledgePackage pkg) {
            this.rootClassLoader = rootClassLoader;
            this.pkgConf = pkgConf;
            this.pkg = pkg;
            this.packageRegistry = pkgRegistry;

            JavaDialectRuntimeData data = (JavaDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData(ID);

            // initialise the dialect runtime data if it doesn't already exist
            if (data == null) {
                data = new JavaDialectRuntimeData();
                this.pkg.getDialectRuntimeRegistry().setDialectData(ID, data);
                data.onAdd(this.pkg.getDialectRuntimeRegistry(), rootClassLoader);
            } else {
                data = (JavaDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData(ID);
            }
        }

        @Override
        public String getId() {
            return ID;
        }

        @Override
        public PackageRegistry getPackageRegistry() {
            return packageRegistry;
        }

        @Override
        public void addImport(ImportDescr importDescr) {
            // we don't need to do anything here
        }

        @Override
        public void addStaticImport(ImportDescr importDescr) {
            // we don't need to do anything here
        }

        @Override
        public void init( RuleDescr ruleDescr ) {
            // we don't need to do anything here
        }

        @Override
        public void init( ProcessDescr processDescr ) {
            // we don't need to do anything here
        }

        @Override
        public EngineElementBuilder getBuilder( Class clazz ) {
            if (clazz == PatternDescr.class) {
                return getPatternBuilder();
            }
            if (clazz == EntryPointDescr.class) {
                return getEntryPointBuilder();
            }
            if (clazz == AndDescr.class || clazz == OrDescr.class || clazz == NotDescr.class || clazz == ExistsDescr.class) {
                return new GroupElementBuilder();
            }
            return throwExceptionForMissingMvel();
        }

        @Override
        public PatternBuilder getPatternBuilder() {
            return new PatternBuilder();
        }

        @Override
        public EntryPointBuilder getEntryPointBuilder() {
            return new EntryPointBuilder();
        }

        @Override
        public TypeResolver getTypeResolver() {
            return this.packageRegistry.getTypeResolver();
        }

        @Override
        public String getExpressionDialectName() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public Map<Class<?>, EngineElementBuilder> getBuilders() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public SalienceBuilder getSalienceBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public EnabledBuilder getEnabledBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public QueryBuilder getQueryBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public RuleConditionBuilder getEvalBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public AccumulateBuilder getAccumulateBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public PredicateBuilder getPredicateBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public ReturnValueBuilder getReturnValueBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public ConsequenceBuilder getConsequenceBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public RuleClassBuilder getRuleClassBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public FromBuilder getFromBuilder() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public AnalysisResult analyzeExpression( PackageBuildContext context, BaseDescr descr, Object content, BoundIdentifiers availableIdentifiers ) {
            return throwExceptionForMissingMvel();
        }

        @Override
        public AnalysisResult analyzeBlock( PackageBuildContext context, BaseDescr descr, String text, BoundIdentifiers availableIdentifiers ) {
            return throwExceptionForMissingMvel();
        }

        @Override
        public void compileAll() {
            throwExceptionForMissingMvel();
        }

        @Override
        public void addRule( RuleBuildContext context ) {
            throwExceptionForMissingMvel();
        }

        @Override
        public void addFunction( FunctionDescr functionDescr, TypeResolver typeResolver, Resource resource ) {
            throwExceptionForMissingMvel();
        }

        @Override
        public List<KnowledgeBuilderResult> getResults() {
            return throwExceptionForMissingMvel();
        }

        @Override
        public void clearResults() {
            throwExceptionForMissingMvel();
        }

        @Override
        public void postCompileAddFunction( FunctionDescr functionDescr, TypeResolver typeResolver ) {
            throwExceptionForMissingMvel();
        }

        @Override
        public void preCompileAddFunction( FunctionDescr functionDescr, TypeResolver typeResolver ) {
            throwExceptionForMissingMvel();
        }
    }
}
