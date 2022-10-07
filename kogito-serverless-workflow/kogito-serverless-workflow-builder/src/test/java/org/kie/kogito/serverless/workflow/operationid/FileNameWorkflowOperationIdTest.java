/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.operationid;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class FileNameWorkflowOperationIdTest {

    private Workflow workflow;
    private FunctionDefinition definition;

    @BeforeEach
    void setup() {
        workflow = mock(Workflow.class);
        definition = new FunctionDefinition("pepe");

    }

    @Test
    void testOperationId() {
        definition.setType(Type.REST);
        definition.setOperation("http://myserver.com/spec/PePE1.yaml#doSomething");
        WorkflowOperationId id = WorkflowOperationIdFactoryType.FILE_NAME.factory().from(workflow, definition, Optional.empty());
        assertThat(id.getOperation()).isEqualTo("doSomething");
        assertThat(id.getFileName()).isEqualTo("PePE1.yaml");
        assertThat(ServerlessWorkflowUtils.getOpenApiClassName(id.getFileName(), id.getOperation())).isEqualTo("Pepe1_doSomething");
        assertThat(ServerlessWorkflowUtils.getOpenApiWorkItemName(id.getFileName(), id.getOperation())).isEqualTo("PePE1_doSomething");
        assertThat(id.getPackageName()).isEqualTo("pepe");
        assertThat(id.getUri()).hasToString("http://myserver.com/spec/PePE1.yaml");
        assertThat(id.getService()).isNull();
    }

    @Test
    void testOperationIdWithService() {
        definition.setType(Type.RPC);
        definition.setOperation("http://myserver.com/spec/PePE1.yaml#service#doSomething");
        WorkflowOperationId id = WorkflowOperationIdFactoryType.FILE_NAME.factory().from(workflow, definition, Optional.empty());
        assertThat(id.getOperation()).isEqualTo("doSomething");
        assertThat(id.getFileName()).isEqualTo("PePE1.yaml");
        assertThat(ServerlessWorkflowUtils.getOpenApiWorkItemName(id.getFileName(), id.getOperation())).isEqualTo("PePE1_doSomething");
        assertThat(id.getPackageName()).isEqualTo("pepe");
        assertThat(id.getUri()).hasToString("http://myserver.com/spec/PePE1.yaml");
        assertThat(id.getService()).isEqualTo("service");
    }
}
