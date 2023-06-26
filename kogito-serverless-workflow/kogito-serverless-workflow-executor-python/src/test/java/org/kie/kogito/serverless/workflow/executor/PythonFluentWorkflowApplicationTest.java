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

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.serverless.workflow.fluent.ActionBuilder.ScriptType;
import org.kie.kogito.serverless.workflow.python.PythonWorkItemHandlerUtils;

import com.fasterxml.jackson.databind.node.TextNode;

import io.serverlessworkflow.api.Workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.call;
import static org.kie.kogito.serverless.workflow.fluent.ActionBuilder.script;
import static org.kie.kogito.serverless.workflow.fluent.FunctionBuilder.python;
import static org.kie.kogito.serverless.workflow.fluent.StateBuilder.operation;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.jsonObject;
import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.workflow;

public class PythonFluentWorkflowApplicationTest {

    @Test
    void testPythonNoArgs() {
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("PythonTest").start(operation().action(script("x='pepe'", ScriptType.PYTHON)).outputFilter("{result:$WORKFLOW.python.x}"))
                    .end().build();
            assertThat(application.execute(workflow, Collections.emptyMap()).getWorkflowdata().get("result").asText()).isEqualTo("pepe");
        }
    }

    @Test
    void testPythonWithArgs() {
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("PythonTest").start(operation().action(script("x*=2", ScriptType.PYTHON, jsonObject().put("x", ".x"))).outputFilter("{result:$WORKFLOW.python.x}"))
                    .end().build();
            assertThat(application.execute(workflow, Map.of("x", 2)).getWorkflowdata().get("result").asInt()).isEqualTo(4);
        }
    }

    @Test
    void testPythonService() {
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            Workflow workflow = workflow("Factorial").start(operation().action(call(python("factorial", "math", "factorial"), new TextNode(".x")).outputFilter(".result")))
                    .end().build();
            assertThat(application.execute(workflow, Map.of("x", 5)).getWorkflowdata().get("result").asInt()).isEqualTo(120);
        }
    }

    @Test
    void testNotStandardPythonService() {
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create(Map.of(PythonWorkItemHandlerUtils.SEARCH_PATH_PROPERTY, "./src/test/resources/"))) {
            Workflow workflow = workflow("Factorial").start(operation().action(call(python("factorial", "custom", "factorial"), new TextNode(".x")).outputFilter(".result")))
                    .end().build();
            assertThat(application.execute(workflow, Map.of("x", 5)).getWorkflowdata().get("result").asInt()).isEqualTo(120);
        }
    }
}
