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
package org.drools.compiler.compiler;

import java.util.List;
import java.util.Map;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.QueryImpl;
import org.drools.compiler.rule.builder.AccumulateBuilder;
import org.drools.compiler.rule.builder.ConsequenceBuilder;
import org.drools.compiler.rule.builder.EnabledBuilder;
import org.drools.compiler.rule.builder.EngineElementBuilder;
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
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.EntryPointDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.OrDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.ProcessDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.util.TypeResolver;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;

import static org.drools.base.base.CoreComponentsBuilder.throwExceptionForMissingMvel;

/**
 * A Dialect implementation handles the building and execution of code
 * expressions and blocks for a rule. This api is considered unstable, and
 * subject to change. Those wishing to implement their own dialects should look
 * ove the MVEL and Java dialect implementations.
 */
public interface Dialect {

    String getId();

    Map<Class<?>, EngineElementBuilder> getBuilders();

    TypeResolver getTypeResolver();

    SalienceBuilder getSalienceBuilder();

    EnabledBuilder getEnabledBuilder();

    PatternBuilder getPatternBuilder();

    PatternBuilderForQuery getPatternBuilderForQuery(QueryImpl query);

    RuleConditionBuilder getEvalBuilder();

    AccumulateBuilder getAccumulateBuilder();

    GroupByBuilder getGroupByBuilder();

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

        private final ClassLoader rootClassLoader;
        private final PackageRegistry packageRegistry;

        DummyDialect(ClassLoader rootClassLoader, PackageRegistry pkgRegistry, InternalKnowledgePackage pkg) {
            this.rootClassLoader = rootClassLoader;
            this.packageRegistry = pkgRegistry;

            JavaDialectRuntimeData data = (JavaDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData(ID);

            // initialise the dialect runtime data if it doesn't already exist
            if (data == null) {
                data = new JavaDialectRuntimeData();
                pkg.getDialectRuntimeRegistry().setDialectData(ID, data);
                data.onAdd(pkg.getDialectRuntimeRegistry(), rootClassLoader);
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
        public PatternBuilderForQuery getPatternBuilderForQuery(QueryImpl query) {
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

        public GroupByBuilder getGroupByBuilder() {
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
