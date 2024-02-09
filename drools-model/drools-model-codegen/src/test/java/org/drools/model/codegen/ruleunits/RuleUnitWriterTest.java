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
package org.drools.model.codegen.ruleunits;

import com.github.javaparser.ast.CompilationUnit;
import org.drools.codegen.common.DroolsModelBuildContext;
import org.drools.codegen.common.context.JavaDroolsModelBuildContext;
import org.drools.codegen.common.context.QuarkusDroolsModelBuildContext;
import org.drools.codegen.common.context.SpringBootDroolsModelBuildContext;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.RuleUnitWriter;
import org.drools.model.codegen.execmodel.generator.DRLIdGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.internal.ruleunit.RuleUnitVariable;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleUnitWriterTest {

    public static Stream<Arguments> contextBuilders() {
        return Stream.of(
                         Arguments.of(JavaDroolsModelBuildContext.builder()),
                         Arguments.of(QuarkusDroolsModelBuildContext.builder()),
                         Arguments.of(SpringBootDroolsModelBuildContext.builder()));
    }

    @ParameterizedTest
    @MethodSource("org.drools.model.codegen.ruleunits.RuleUnitWriterTest#contextBuilders")
    public void getUnitSource(DroolsModelBuildContext.Builder contextBuilder) {

        RuleUnitWriter ruleUnitWriter = createRuleUnitWriterWithTestParameters(contextBuilder);
        String unitSource = ruleUnitWriter.getUnitSource();

        assertThat(unitSource).contains("org.kie.api.conf.EventProcessingOption.STREAM"); // TestRuleUnitDescription.getKieBaseOptions
        assertThat(unitSource).contains("org.kie.api.runtime.conf.ClockTypeOption.PSEUDO"); // TestRuleUnitDescription.getClockType
    }

    private RuleUnitWriter createRuleUnitWriterWithTestParameters(DroolsModelBuildContext.Builder contextBuilder) {
        PackageModel packageModel = new PackageModel("org.example:test:1.0.0", "test", KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY), new DialectCompiletimeRegistry(), new DRLIdGenerator());
        packageModel.setContext(contextBuilder.build());
        CompilationUnit cu = new CompilationUnit();
        PackageModel.RuleSourceResult ruleSourceResult = new PackageModel.RuleSourceResult(cu);
        ruleSourceResult.withModel("org.example.HelloWorldUnit", "org.example.Generated_HelloWorldUnit");
        RuleUnitDescription ruleUnitDescr = new TestRuleUnitDescription();

        RuleUnitWriter ruleUnitWriter = new RuleUnitWriter(packageModel, ruleSourceResult, ruleUnitDescr);
        return ruleUnitWriter;
    }

    public static class TestRuleUnitDescription implements RuleUnitDescription {

        private RuleUnitVariable var = new TestRuleUnitVariable();

        @Override
        public String getRuleUnitName() {
            return "org.example.HelloWorldUnit";
        }

        @Override
        public String getCanonicalName() {
            return "org.example.HelloWorldUnit";
        }

        @Override
        public String getSimpleName() {
            return "HelloWorldUnit";
        }

        @Override
        public String getPackageName() {
            return "org.example";
        }

        @Override
        public Optional<Class<?>> getDatasourceType(String name) {
            return Optional.of(String.class);
        }

        @Override
        public Optional<Type> getVarType(String name) {
            return Optional.of(String.class);
        }

        @Override
        public RuleUnitVariable getVar(String name) {
            return var;
        }

        @Override
        public boolean hasVar(String name) {
            return true;
        }

        @Override
        public Collection<String> getUnitVars() {
            return Arrays.asList(new String[]{"strings"});
        }

        @Override
        public Collection<? extends RuleUnitVariable> getUnitVarDeclarations() {
            return Arrays.asList(new RuleUnitVariable[]{var});
        }

        @Override
        public boolean hasDataSource(String name) {
            return true;
        }

        @Override
        public ClockTypeOption getClockType() {
            return ClockTypeOption.PSEUDO;
        }

        @Override
        public Collection<KieBaseOption> getKieBaseOptions() {
            return Arrays.asList(new KieBaseOption[]{EventProcessingOption.STREAM});
        }

    }

    public static class TestRuleUnitVariable implements RuleUnitVariable {

        @Override
        public boolean isDataSource() {
            return true;
        }

        @Override
        public boolean isDataStore() {
            return true;
        }

        @Override
        public String getName() {
            return "strings";
        }

        @Override
        public String getter() {
            return "getStrings";
        }

        @Override
        public String setter() {
            return null;
        }

        @Override
        public Type getType() {
            return String.class;
        }

        @Override
        public Class<?> getDataSourceParameterType() {
            return String.class;
        }

        @Override
        public Class<?> getBoxedVarType() {
            return String.class;
        }

    }
}
