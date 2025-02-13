/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.kie.kogito.codegen.decision;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.kogito.dmn.DmnExecutionIdSupplier;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.codegen.core.CodegenUtils.newObject;
import static org.kie.kogito.codegen.decision.DecisionCodegen.getCustomDMNProfiles;
import static org.kie.kogito.codegen.decision.DecisionCodegenTest.CUSTOM_PROFILES_PACKAGE;
import static org.kie.kogito.codegen.decision.DecisionContainerGenerator.MONITORED_DECISIONMODEL_TRANSFORMER;

class DecisionContainerGeneratorTest {

    private MethodCallExpr initMethod;

    @BeforeEach
    void setUp() {
        initMethod = new MethodCallExpr();
        initMethod.setName("init");
    }

    @Test
    void setupExecIdSupplierVariableWithTracing() {
        assertThat(initMethod.getArguments()).isEmpty();
        DecisionContainerGenerator.setupExecIdSupplierVariable(initMethod, true);
        assertThat(initMethod.getArguments()).hasSize(1);
        Expression expected = newObject(DmnExecutionIdSupplier.class);
        assertThat(initMethod.getArguments().get(0)).isEqualTo(expected);
    }

    @Test
    void setupExecIdSupplierVariableWithoutTracing() {
        assertThat(initMethod.getArguments()).isEmpty();
        DecisionContainerGenerator.setupExecIdSupplierVariable(initMethod, false);
        assertThat(initMethod.getArguments()).hasSize(1);
        Expression expected = new NullLiteralExpr();
        assertThat(initMethod.getArguments().get(0)).isEqualTo(expected);
    }

    @Test
    void setupDecisionModelTransformerVariableWithMonitoring() {
        assertThat(initMethod.getArguments()).isEmpty();
        DecisionContainerGenerator.setupDecisionModelTransformerVariable(initMethod, true);
        assertThat(initMethod.getArguments()).hasSize(1);
        Expression expected = newObject(MONITORED_DECISIONMODEL_TRANSFORMER);
        assertThat(initMethod.getArguments().get(0)).isEqualTo(expected);
    }

    @Test
    void setupDecisionModelTransformerVariableWithoutMonitoring() {
        assertThat(initMethod.getArguments()).isEmpty();
        DecisionContainerGenerator.setupDecisionModelTransformerVariable(initMethod, false);
        assertThat(initMethod.getArguments()).hasSize(1);
        Expression expected = new NullLiteralExpr();
        assertThat(initMethod.getArguments().get(0)).isEqualTo(expected);
    }

    @Test
    void setupCustomDMNProfiles() {
        NodeList<Expression> arguments = initMethod.getArguments();
        assertThat(initMethod.getArguments()).isEmpty();
        Set<String> customDMNProfileStrings = IntStream.range(0, 3)
                .mapToObj(index -> String.format("%s.Profile_%d", CUSTOM_PROFILES_PACKAGE, index))
                .collect(Collectors.toSet());
        Set<DMNProfile> customDMNProfiles = getCustomDMNProfiles(customDMNProfileStrings, Thread.currentThread().getContextClassLoader());
        DecisionContainerGenerator.setupCustomDMNProfiles(initMethod, customDMNProfiles);
        assertThat(initMethod.getArguments()).hasSize(1);
        Expression retrieved = arguments.get(0);
        assertThat(retrieved).isInstanceOf(MethodCallExpr.class);
        MethodCallExpr methodCallExpr = (MethodCallExpr) retrieved;
        assertThat(methodCallExpr.getScope()).isNotEmpty();
        assertThat(methodCallExpr.getScope().get()).isEqualTo(new NameExpr(Set.class.getCanonicalName()));
        assertThat(methodCallExpr.getName().getIdentifier()).isEqualTo("of");
        NodeList<Expression> retrievedArguments = methodCallExpr.getArguments();
        assertThat(retrievedArguments).hasSize(customDMNProfileStrings.size());
        customDMNProfileStrings.forEach(profileString -> {
            Expression expectedArgument = newObject(profileString);
            assertThat(retrievedArguments).contains(expectedArgument);
        });
    }

    @ParameterizedTest
    @MethodSource("booleans")
    void setupEnableRuntimeTypeCheckOptionFalse(boolean enableRuntimeTypeCheckOption) {
        NodeList<Expression> arguments = initMethod.getArguments();
        assertThat(initMethod.getArguments()).isEmpty();
        DecisionContainerGenerator.setupEnableRuntimeTypeCheckOption(initMethod, enableRuntimeTypeCheckOption);
        assertThat(initMethod.getArguments()).hasSize(1);
        Expression retrieved = arguments.get(0);
        assertThat(retrieved).isInstanceOf(BooleanLiteralExpr.class);
        assertThat(((BooleanLiteralExpr) retrieved).getValue()).isEqualTo(enableRuntimeTypeCheckOption);
    }

    static Stream<Boolean> booleans() {
        return Stream.of(true, false);
    }

}
