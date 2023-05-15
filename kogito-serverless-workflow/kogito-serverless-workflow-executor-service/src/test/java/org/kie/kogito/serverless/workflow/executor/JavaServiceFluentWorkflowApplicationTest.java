/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.executor;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.process.Process;
import org.kie.kogito.serverless.workflow.models.JsonNodeModel;

import com.fasterxml.jackson.databind.JsonNode;

import io.serverlessworkflow.api.Workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.call;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.java;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.parallel;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.jsonObject;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.workflow;

public class JavaServiceFluentWorkflowApplicationTest {

    public int duplicate(int number) {
        return number * 2;
    }

    public int multiply(int one, int two) {
        return one * two;
    }

    @Test
    void testService() {
        final String DOUBLE = "double";
        final String PRODUCT = "product";
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("ServiceTest").function(java(DOUBLE, this.getClass().getName(), "duplicate"))
                    .function(java(PRODUCT, this.getClass().getName(), "multiply"))
                    .start(parallel()
                            .newBranch().action(call(DOUBLE, ".one").outputFilter(".double")).endBranch()
                            .newBranch().action(call(PRODUCT, jsonObject().put("one", ".one").put("two", ".two")).outputFilter(".product")).endBranch())
                    .end().build();
            Process<JsonNodeModel> process = application.process(workflow);
            JsonNode result = application.execute(process, Map.of("one", 4, "two", 8)).getWorkflowdata();
            assertThat(result.get("double").asInt()).isEqualTo(8);
            assertThat(result.get("product").asInt()).isEqualTo(32);
        }
    }
}
