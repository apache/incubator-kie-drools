/*
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

package org.kie.kogito.codegen.usertask;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.drools.codegen.common.rest.RestAnnotator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.process.ProcessCodegenException;
import org.kie.kogito.codegen.process.util.CodegenUtil;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.kogito.codegen.process.ProcessResourceGeneratorTest.*;

public class UserTaskCodegenTest {

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#restContextBuilders")
    void testUserTaskManageTransactionalEnabledByDefault(KogitoBuildContext.Builder contextBuilder) {
        KogitoBuildContext context = contextBuilder.build();
        UserTaskCodegen userTaskCodegen = new UserTaskCodegen(context, Collections.emptyList());
        CompilationUnit compilationUnit = userTaskCodegen.createRestEndpointCompilationUnit();

        Collection<MethodDeclaration> restEndpoints = getRestMethods(compilationUnit, context);
        assertThat(restEndpoints).isNotEmpty();

        testTransactionAnnotationIsPresent(restEndpoints, context, false);

        userTaskCodegen.manageTransactional(compilationUnit);

        testTransactionAnnotationIsPresent(restEndpoints, context, true);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#restContextBuilders")
    void testUserTaskManageTransactionalDisabled(KogitoBuildContext.Builder contextBuilder) {
        KogitoBuildContext context = contextBuilder.build();
        UserTaskCodegen userTaskCodegen = new UserTaskCodegen(context, Collections.emptyList());
        context.setApplicationProperty(CodegenUtil.globalProperty(CodegenUtil.TRANSACTION_ENABLED), "false");

        CompilationUnit compilationUnit = userTaskCodegen.createRestEndpointCompilationUnit();

        Collection<MethodDeclaration> restEndpoints = getRestMethods(compilationUnit, context);
        assertThat(restEndpoints).isNotEmpty();

        testTransactionAnnotationIsPresent(restEndpoints, context, false);

        userTaskCodegen.manageTransactional(compilationUnit);

        testTransactionAnnotationIsPresent(restEndpoints, context, false);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#restContextBuilders")
    void testUserTaskManageTransactionalEnabled(KogitoBuildContext.Builder contextBuilder) {
        KogitoBuildContext context = contextBuilder.build();
        UserTaskCodegen userTaskCodegen = new UserTaskCodegen(context, Collections.emptyList());
        context.setApplicationProperty(CodegenUtil.globalProperty(CodegenUtil.TRANSACTION_ENABLED), "true");
        CompilationUnit compilationUnit = userTaskCodegen.createRestEndpointCompilationUnit();

        Collection<MethodDeclaration> restEndpoints = getRestMethods(compilationUnit, context);
        assertThat(restEndpoints).isNotEmpty();

        testTransactionAnnotationIsPresent(restEndpoints, context, false);

        userTaskCodegen.manageTransactional(compilationUnit);

        testTransactionAnnotationIsPresent(restEndpoints, context, true);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#restContextBuilders")
    void testUserTaskFaultToleranceDisabled(KogitoBuildContext.Builder contextBuilder) {
        KogitoBuildContext context = contextBuilder.build();
        UserTaskCodegen userTaskCodegen = new UserTaskCodegen(context, Collections.emptyList());
        context.setApplicationProperty(CodegenUtil.globalProperty(CodegenUtil.FAULT_TOLERANCE_ENABLED), "false");
        CompilationUnit compilationUnit = userTaskCodegen.createRestEndpointCompilationUnit();

        Collection<MethodDeclaration> restEndpoints = getRestMethods(compilationUnit, context);
        assertThat(restEndpoints).isNotEmpty();

        testFaultToleranceAnnotationIsPresent(restEndpoints, context, false);

        userTaskCodegen.manageFaultTolerance(compilationUnit);

        testFaultToleranceAnnotationIsPresent(restEndpoints, context, false);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#restContextBuilders")
    void testUserTaskFaultToleranceEnabled(KogitoBuildContext.Builder contextBuilder) {
        KogitoBuildContext context = contextBuilder.build();
        UserTaskCodegen userTaskCodegen = new UserTaskCodegen(context, Collections.emptyList());
        CompilationUnit compilationUnit = userTaskCodegen.createRestEndpointCompilationUnit();

        Collection<MethodDeclaration> restEndpoints = getRestMethods(compilationUnit, context);
        assertThat(restEndpoints).isNotEmpty();

        testFaultToleranceAnnotationIsPresent(restEndpoints, context, false);

        userTaskCodegen.manageFaultTolerance(compilationUnit);

        testFaultToleranceAnnotationIsPresent(restEndpoints, context, true);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#restContextBuilders")
    void testUserTaskFaultToleranceEnabledWithTransactionsDisabled(KogitoBuildContext.Builder contextBuilder) {
        KogitoBuildContext context = contextBuilder.build();
        context.setApplicationProperty(CodegenUtil.globalProperty(CodegenUtil.TRANSACTION_ENABLED), "false");

        UserTaskCodegen userTaskCodegen = new UserTaskCodegen(context, Collections.emptyList());

        CompilationUnit compilationUnit = userTaskCodegen.createRestEndpointCompilationUnit();

        assertThatThrownBy(() -> {
            userTaskCodegen.manageFaultTolerance(compilationUnit);
        }).isInstanceOf(ProcessCodegenException.class)
                .hasMessageContaining("Fault tolerance is enabled, but transactions are disabled. Please enable transactions before fault tolerance.");
    }

    protected Collection<MethodDeclaration> getRestMethods(CompilationUnit compilationUnit, KogitoBuildContext context) {
        RestAnnotator restAnnotator = context.getRestAnnotator();
        return compilationUnit.findAll(MethodDeclaration.class).stream()
                .filter(restAnnotator::isRestAnnotated)
                .collect(Collectors.toList());
    }
}
