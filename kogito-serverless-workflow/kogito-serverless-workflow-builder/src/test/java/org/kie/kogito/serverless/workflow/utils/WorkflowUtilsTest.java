/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.serverless.workflow.utils;

import java.io.File;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;

import io.serverlessworkflow.api.functions.FunctionDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.resolveFunctionMetadata;

public class WorkflowUtilsTest {

    private static final String TEST_RESOURCES = "src/test/resources";

    KogitoBuildContext context;

    @BeforeEach
    protected void setup() {
        context = JavaKogitoBuildContext.builder()
                .withApplicationProperties(new File(TEST_RESOURCES))
                .withPackageName(this.getClass().getPackage().getName())
                .build();
    }

    @Test
    public void testResolveFunctionMetadata() {
        FunctionDefinition function = new FunctionDefinition().withName("testfunction1").withMetadata(Collections.singletonMap("testprop1", "customtestprop1val"));
        assertThat(resolveFunctionMetadata(function, "testprop1", context)).isNotNull().isEqualTo("customtestprop1val");
        assertThat(resolveFunctionMetadata(function, "testprop2", context)).isNotNull().isEqualTo("testprop2val");
    }
}
